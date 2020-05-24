package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.service.AuthService;
import com.github.netty.mqtt.broker.store.channel.ChannelGroupStore;
import com.github.netty.mqtt.broker.store.channel.ChannelIdStore;
import com.github.netty.mqtt.broker.store.dup.DupPubRelMessageStore;
import com.github.netty.mqtt.broker.store.dup.DupPublishMessageStore;
import com.github.netty.mqtt.broker.store.dup.DupPublishMessageStoreService;
import com.github.netty.mqtt.broker.store.dup.DupPubRelMessageStoreService;
import com.github.netty.mqtt.broker.store.session.ISessionStoreService;
import com.github.netty.mqtt.broker.store.session.SessionStore;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:55
 */
@Component
@Slf4j
public class MqttConnectMessageHandler implements MqttMessageHandler<MqttConnectMessage> {

    @Autowired
    private AuthService authService;

    @Autowired
    private ISessionStoreService sessionStoreService;

    @Autowired
    private DupPublishMessageStoreService dupPublishMessageStoreService;

    @Autowired
    private DupPubRelMessageStoreService dupPubRelMessageStoreService;

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.CONNECT == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttConnectMessage mqttMessage) {
        log.info("接收到的连接信息：{}", mqttMessage.toString());
        if (mqttMessage.decoderResult().isFailure()) {
            Throwable cause = mqttMessage.decoderResult().cause();
            log.error("消息解码存在异常, {}", mqttMessage.toString(), cause);
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                writeConnFailure(channel, MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION);
            } else if (cause instanceof MqttIdentifierRejectedException) {
                writeConnFailure(channel, MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED);
            }
            log.info("连接结束：{}", mqttMessage.toString());
            channel.close();
            return;
        }
        MqttConnectPayload payload = mqttMessage.payload();
        String clientId = payload.clientIdentifier();
        if (StringUtils.isEmpty(clientId)) {
            writeConnFailure(channel, MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED);
            channel.close();
            log.error("连接结束, 缺少clientId: {}", payload.toString());
            return;
        }
        String userName = payload.userName();
        String password = new String(payload.passwordInBytes(), StandardCharsets.UTF_8);
        // 校验用户名密码
        if (authService.check(userName, password)) {
            log.error("用户名密码不正确，{}", payload.toString());
            writeConnFailure(channel, MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
            channel.close();
            return;
        }

        // 检查重连
        if (sessionStoreService.containKey(clientId)) {
            SessionStore sessionStore = sessionStoreService.get(clientId);
            ChannelIdStore.get(sessionStore.getChannelId()).ifPresent(channelId -> {
                Channel previous = ChannelGroupStore.find(channelId);
                boolean cleanSession = sessionStore.isCleanSession();
                if (cleanSession) {
                    sessionStoreService.remove(clientId);

                }
                if (previous != null) {
                    previous.close();
                }
            });
        }

        // 处理心跳
        int expire = 0;
        if (mqttMessage.variableHeader().keepAliveTimeSeconds() > 0) {
            if (channel.pipeline().names().contains("idle")) {
                channel.pipeline().remove("idle");
            }
            expire = Math.round(mqttMessage.variableHeader().keepAliveTimeSeconds() * 1.5f);
            channel.pipeline().addFirst("idle", new IdleStateHandler(0, 0, expire));
        }

        // 处理遗嘱消息
        boolean cleanSession = mqttMessage.variableHeader().isCleanSession();
        SessionStore sessionStore = new SessionStore("brokerId", clientId, channel.id().asLongText(), expire, cleanSession);
        if (mqttMessage.variableHeader().isWillFlag()) {
            MqttPublishMessage willMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.valueOf(mqttMessage.variableHeader().willQos()), mqttMessage.variableHeader().isWillRetain(), 0),
                    new MqttPublishVariableHeader(mqttMessage.payload().willTopic(), 0),
                    Unpooled.buffer().writeBytes(mqttMessage.payload().willMessageInBytes())
            );
            sessionStore.setWillMessage(willMessage);
        }
        sessionStoreService.put(clientId, sessionStore, expire);

        // 存储回话 返回客户端连接确认
        channel.attr(AttributeKey.valueOf("clientId")).set(clientId);
        boolean sessionPresent = sessionStoreService.containKey(clientId);
        MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent);
        MqttFixedHeader mqttConnAckFixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttConnAckMessage mqttConnAckMessage = new MqttConnAckMessage(mqttConnAckFixedHeader, mqttConnAckVariableHeader);
        channel.writeAndFlush(mqttConnAckMessage);
        ChannelGroupStore.add(channel);

        // 如果cleanSession为false 需要重发同一clientId存储的未完成的Qos1和Qos2的DUP消息
        if (!cleanSession) {
            List<DupPublishMessageStore> dupPublishMessageStores = dupPublishMessageStoreService.get(clientId);
            List<DupPubRelMessageStore> dupPubRelMessageStores = dupPubRelMessageStoreService.get(clientId);
            dupPublishMessageStores.forEach(dupPublishMessageStore -> {
                MqttPublishMessage publishMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBLISH, true, MqttQoS.valueOf(dupPublishMessageStore.getQos()), false, 0),
                        new MqttPublishVariableHeader(dupPublishMessageStore.getTopic(), dupPublishMessageStore.getMessageId()),
                        dupPublishMessageStore.getContent()
                        );
                channel.writeAndFlush(publishMessage);
            });

            dupPubRelMessageStores.forEach(dupPubRelMessageStore -> {
                MqttMessage message = MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBREL, true, MqttQoS.AT_MOST_ONCE, false, 0),
                        MqttMessageIdVariableHeader.from(dupPubRelMessageStore.getMessageId()),
                        null
                );
                channel.writeAndFlush(message);
            });
        }
    }

    /**
     * 连接失败
     *
     * @param channel    通道
     * @param returnCode 错误码
     */
    private void writeConnFailure(Channel channel, MqttConnectReturnCode returnCode) {
        MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                new MqttConnAckVariableHeader(returnCode, false),
                null
        );
        channel.writeAndFlush(mqttMessage);
    }
}
