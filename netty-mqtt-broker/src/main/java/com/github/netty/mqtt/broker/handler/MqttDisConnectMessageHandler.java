package com.github.netty.mqtt.broker.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import org.springframework.stereotype.Component;


/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:55
 */
@Component
public class MqttDisConnectMessageHandler implements MqttMessageHandler<MqttMessage>{

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.DISCONNECT == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttMessage mqttMessage) {

    }
}
