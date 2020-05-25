package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.id.MessageIdService;
import com.github.netty.mqtt.broker.store.retain.IRetainMessageStoreService;
import com.github.netty.mqtt.broker.store.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:55
 */
@Component
public class MqttSubscribeMessageHandler implements MqttMessageHandler<MqttSubscribeMessage>{

    @Autowired
    private ISubscribeStoreService subscribeStoreService;

    @Autowired
    private MessageIdService messageIdService;

    @Autowired
    private IRetainMessageStoreService retainMessageStoreService;

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.SUBSCRIBE == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttSubscribeMessage mqttMessage) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttSubAckPayload mqttSubAckPayload = new MqttSubAckPayload(1);
        MqttMessageIdVariableHeader messageIdVariableHeader = MqttMessageIdVariableHeader.from(mqttMessage.variableHeader().messageId());
        MqttSubAckMessage mqttSubAckMessage = new MqttSubAckMessage(mqttFixedHeader, messageIdVariableHeader, mqttSubAckPayload);
        channel.writeAndFlush(mqttSubAckMessage);
    }
}
