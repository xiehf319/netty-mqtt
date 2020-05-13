package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.ChannelGroupStore;
import io.netty.channel.Channel;
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
public class MqttConnectMessageHandler implements MqttMessageHandler<MqttConnectMessage>{

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.CONNECT == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttConnectMessage mqttMessage) {
        MqttConnAckVariableHeader mqttConnAckVariableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttConnAckMessage mqttConnAckMessage = new MqttConnAckMessage(mqttFixedHeader, mqttConnAckVariableHeader);
        channel.writeAndFlush(mqttConnAckMessage);
        ChannelGroupStore.add(channel);
    }
}
