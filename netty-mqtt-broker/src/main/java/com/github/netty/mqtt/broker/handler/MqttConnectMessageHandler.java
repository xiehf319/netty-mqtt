package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.service.AuthService;
import com.github.netty.mqtt.broker.store.channel.ChannelGroupStore;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;


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



        MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
        MqttConnectVariableHeader mqttConnectVariableHeader = mqttMessage.variableHeader();
        boolean hasPassword = mqttConnectVariableHeader.hasPassword();
        boolean hasUserName = mqttConnectVariableHeader.hasUserName();
        boolean cleanSession = mqttConnectVariableHeader.isCleanSession();
        boolean willFlag = mqttConnectVariableHeader.isWillFlag();
        boolean willRetain = mqttConnectVariableHeader.isWillRetain();
        int keepAliveTimeSeconds = mqttConnectVariableHeader.keepAliveTimeSeconds();

        MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
        MqttFixedHeader mqttConnAckFixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttConnAckMessage mqttConnAckMessage = new MqttConnAckMessage(mqttConnAckFixedHeader, mqttConnAckVariableHeader);
        channel.writeAndFlush(mqttConnAckMessage);
        ChannelGroupStore.add(channel);
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
