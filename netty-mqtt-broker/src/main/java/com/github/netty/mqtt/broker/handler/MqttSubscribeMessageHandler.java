package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.id.MessageIdService;
import com.github.netty.mqtt.broker.store.retain.IRetainMessageStoreService;
import com.github.netty.mqtt.broker.store.retain.RetainMessageStore;
import com.github.netty.mqtt.broker.store.subscribe.ISubscribeStoreService;
import com.github.netty.mqtt.broker.store.subscribe.SubscribeStore;
import com.github.netty.mqtt.broker.util.TopicValidUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
public class MqttSubscribeMessageHandler implements MqttMessageHandler<MqttSubscribeMessage> {

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

        List<MqttTopicSubscription> topicSubscriptions = mqttMessage.payload().topicSubscriptions();
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        List<Integer> mqttQoSList = new ArrayList<>();
        List<MqttTopicSubscription> validTopicSubscriptions = new ArrayList<>();
        for (MqttTopicSubscription topicSubscription : topicSubscriptions) {
            String topicFilter = topicSubscription.topicName();
            Integer mqttQoS = topicSubscription.qualityOfService().value();
            if (!TopicValidUtil.valid(topicFilter)) {
                log.error("SUBSCRIBE - topicFilter: {} invalid", topicFilter);
                mqttQoSList.add(MqttQoS.FAILURE.value());
            } else {
                mqttQoSList.add(mqttQoS);
                SubscribeStore subscribeStore = new SubscribeStore(clientId, topicFilter, mqttQoS);
                subscribeStoreService.put(topicFilter, subscribeStore);
                validTopicSubscriptions.add(topicSubscription);
            }
            log.info("SUBSCRIBE - clientId: {} topicFilter: {} qos: {}", clientId, topicFilter, mqttQoS);
        }

        // 返回给订阅方
        MqttMessage subAckMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(mqttMessage.variableHeader().messageId()),
                new MqttSubAckPayload(mqttQoSList)
        );
        channel.writeAndFlush(subAckMessage);

        // 发布保留消息
        validTopicSubscriptions.forEach(topicSubscription -> {
            String topicFilter = topicSubscription.topicName();
            MqttQoS mqttQoS = topicSubscription.qualityOfService();
            this.sendRetainMessage(channel, topicFilter, mqttQoS);
        });
    }

    /**
     * 发送保留消息到订阅者
     *
     * @param channel
     * @param topicFilter
     * @param qos
     */
    private void sendRetainMessage(Channel channel, String topicFilter, MqttQoS qos) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        List<RetainMessageStore> retainMessageStores = retainMessageStoreService.search(topicFilter);
        retainMessageStores.forEach(retainMessageStore -> {
            // 取最小的 消息质量等级
            MqttQoS mqttQoS = retainMessageStore.getQos() > qos.value() ? qos : MqttQoS.valueOf(retainMessageStore.getQos());
            if (mqttQoS == MqttQoS.AT_MOST_ONCE) {
                MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBLISH, false, mqttQoS, false, 0),
                        new MqttPublishVariableHeader(retainMessageStore.getTopic(), 0),
                        Unpooled.buffer().writeBytes(retainMessageStore.getContent())
                );
                channel.writeAndFlush(mqttMessage);
            } else if (mqttQoS == MqttQoS.AT_LEAST_ONCE || mqttQoS == MqttQoS.EXACTLY_ONCE) {
                int messageId = messageIdService.getNextMessageId();

                MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                        new MqttFixedHeader(MqttMessageType.PUBLISH, false, mqttQoS, false, 0),
                        new MqttPublishVariableHeader(retainMessageStore.getTopic(), messageId),
                        Unpooled.buffer().writeBytes(retainMessageStore.getContent())
                );
                channel.writeAndFlush(mqttMessage);
            }
            log.info("PUBLISH - clientId: {} topic: {} QoS: {}", clientId, retainMessageStore.getTopic(), mqttQoS.value());
        });
    }

}
