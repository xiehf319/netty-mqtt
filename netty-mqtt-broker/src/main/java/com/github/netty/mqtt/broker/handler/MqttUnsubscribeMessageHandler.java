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
public class MqttUnsubscribeMessageHandler implements MqttMessageHandler<MqttUnsubscribeMessage>{

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.UNSUBSCRIBE == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttUnsubscribeMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttUnsubAckMessage mqttUnsubAckMessage = new MqttUnsubAckMessage(mqttFixedHeader, MqttMessageIdVariableHeader.from(mqttMessage.variableHeader().messageId()));
        channel.writeAndFlush(mqttUnsubAckMessage);
    }
}
