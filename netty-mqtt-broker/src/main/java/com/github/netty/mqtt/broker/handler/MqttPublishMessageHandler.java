package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.channel.ChannelGroupStore;
import com.github.netty.mqtt.broker.store.channel.ChannelIdStore;
import com.github.netty.mqtt.broker.store.retain.RetainMessageStore;
import com.github.netty.mqtt.broker.store.retain.RetainMessageStoreService;
import com.github.netty.mqtt.broker.store.session.ISessionStoreService;
import com.github.netty.mqtt.broker.store.session.SessionStore;
import com.github.netty.mqtt.broker.store.subscribe.ISubscribeStoreService;
import com.github.netty.mqtt.broker.store.subscribe.SubscribeStore;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class MqttPublishMessageHandler implements MqttMessageHandler<MqttPublishMessage> {

    @Autowired
    private RetainMessageStoreService retainMessageStoreService;

    @Autowired
    private ISubscribeStoreService subscribeStoreService;

    @Autowired
    private ISessionStoreService sessionStoreService;

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.PUBLISH == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttPublishMessage mqttMessage) {
        String topic = mqttMessage.variableHeader().topicName();
        MqttQoS mqttQoS = mqttMessage.fixedHeader().qosLevel();
        // 读取消息内容
        byte[] content = new byte[mqttMessage.payload().readableBytes()];
        mqttMessage.payload().getBytes(mqttMessage.payload().readerIndex(), content);

        if (mqttQoS == MqttQoS.AT_MOST_ONCE) {
            // 不做处理
        } else if (mqttQoS == MqttQoS.AT_LEAST_ONCE) {
            this.sendPubAckMessage(channel, mqttMessage.variableHeader().packetId());
        } else if (mqttQoS == MqttQoS.EXACTLY_ONCE) {
            this.sendPubRecMessage(channel, mqttMessage.variableHeader().packetId());
        }

        // 需要保留的消息
        if (mqttMessage.fixedHeader().isRetain()) {
            if (content.length == 0) {
                retainMessageStoreService.remove(topic);
            } else {
                RetainMessageStore retainMessageStore = new RetainMessageStore();
                retainMessageStore.setTopic(topic);
                retainMessageStore.setQos(mqttQoS.value());
                retainMessageStore.setContent(content);
                retainMessageStoreService.put(topic, retainMessageStore);
            }
        }
    }


    private void sendPubAckMessage(Channel channel, int messageId) {
        MqttMessage pubAckMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null
        );
        channel.writeAndFlush(pubAckMessage);
    }

    private void sendPubRecMessage(Channel channel, int messageId) {
        MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null
        );
        channel.writeAndFlush(pubRecMessage);
    }

}
