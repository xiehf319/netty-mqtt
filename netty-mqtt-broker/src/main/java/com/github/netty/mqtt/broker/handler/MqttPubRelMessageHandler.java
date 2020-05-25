package com.github.netty.mqtt.broker.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:55
 */
@Component
@Slf4j
public class MqttPubRelMessageHandler implements MqttMessageHandler<MqttMessage> {

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.PUBREL == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        MqttMessage message = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_LEAST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(mqttMessageIdVariableHeader.messageId()),
                null
        );
        log.info("PUBREL clientId: {} messageId: {}", channel.attr(AttributeKey.valueOf("clientId")), mqttMessageIdVariableHeader.messageId());
        channel.writeAndFlush(message);
    }
}
