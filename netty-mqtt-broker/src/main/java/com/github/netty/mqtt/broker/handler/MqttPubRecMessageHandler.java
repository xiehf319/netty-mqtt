package com.github.netty.mqtt.broker.handler;

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
public class MqttPubRecMessageHandler implements MqttMessageHandler<MqttMessage>{

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.PUBREC == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttMessage mqttPubRelMessage = new MqttMessage(mqttFixedHeader);
        channel.writeAndFlush(mqttPubRelMessage);
    }
}
