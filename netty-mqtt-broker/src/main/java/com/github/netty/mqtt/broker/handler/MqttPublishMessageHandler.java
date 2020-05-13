package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.ChannelGroupStore;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.ChannelMatchers;
import io.netty.handler.codec.mqtt.*;
import org.springframework.stereotype.Component;


/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:55
 */
@Component
public class MqttPublishMessageHandler implements MqttMessageHandler<MqttPublishMessage> {

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.PUBLISH == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttPublishMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBACK, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttMessageIdVariableHeader messageIdVariableHeader = MqttMessageIdVariableHeader.from(mqttMessage.variableHeader().packetId());
        MqttPubAckMessage mqttPubAckMessage = new MqttPubAckMessage(mqttFixedHeader, messageIdVariableHeader);
        channel.writeAndFlush(mqttPubAckMessage);

        ChannelGroupStore.broadcast(mqttMessage, ChannelMatchers.all());
    }

}
