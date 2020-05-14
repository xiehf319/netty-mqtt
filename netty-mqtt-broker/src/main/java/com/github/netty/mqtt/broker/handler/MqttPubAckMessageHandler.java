package com.github.netty.mqtt.broker.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import org.springframework.stereotype.Component;


/**
 * 类描述:
 * broker publish消息给服务端 收到ack消息
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:55
 */
@Component
public class MqttPubAckMessageHandler implements MqttMessageHandler<MqttPubAckMessage>{

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.PUBACK == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttPubAckMessage mqttMessage) {

        // delete
    }
}
