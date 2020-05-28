package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
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
public class MqttUnsubscribeMessageHandler implements MqttMessageHandler<MqttUnsubscribeMessage>{

    @Autowired
    private ISubscribeStoreService subscribeStoreService;

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.UNSUBSCRIBE == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttUnsubscribeMessage message) {
        List<String> topicFilters = message.payload().topics();
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        topicFilters.forEach(topicFilter -> {
            subscribeStoreService.remove(topicFilter, clientId);
            log.info("UNSUBSCRIBE - clientId: {} topicFilter: {}", clientId, topicFilter);
        });

        MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(message.variableHeader().messageId()),
                null
        );
        channel.writeAndFlush(mqttMessage);
    }
}
