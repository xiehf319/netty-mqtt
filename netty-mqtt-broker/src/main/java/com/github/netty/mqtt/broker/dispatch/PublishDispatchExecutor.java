package com.github.netty.mqtt.broker.dispatch;

import com.github.netty.mqtt.broker.store.channel.ChannelGroupStore;
import com.github.netty.mqtt.broker.store.channel.ChannelIdStore;
import com.github.netty.mqtt.broker.store.dup.DupPublishMessageStore;
import com.github.netty.mqtt.broker.store.dup.DupPublishMessageStoreService;
import com.github.netty.mqtt.broker.store.id.MessageIdService;
import com.github.netty.mqtt.broker.store.session.SessionStore;
import com.github.netty.mqtt.broker.store.session.SessionStoreService;
import com.github.netty.mqtt.broker.store.subscribe.ISubscribeStoreService;
import com.github.netty.mqtt.broker.store.subscribe.SubscribeStore;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/25 18:53
 */
@Service
@Slf4j
public class PublishDispatchExecutor {

    @Autowired
    private SessionStoreService sessionStoreService;

    @Autowired
    private ISubscribeStoreService subscribeStoreService;

    @Autowired
    private MessageIdService messageIdService;

    @Autowired
    private DupPublishMessageStoreService dupPublishMessageStoreService;

    /**
     * 发布消息PUBLISH
     *
     * @param topic
     * @param qos
     * @param content
     * @param retain
     * @param dup
     */
    public void sendPublishMessage(String topic, MqttQoS qos, byte[] content, boolean retain, boolean dup) {
        List<SubscribeStore> subscribeStoreList = subscribeStoreService.search(topic);
        subscribeStoreList.forEach(subscribeStore -> {
            String clientId = subscribeStore.getClientId();
            if (sessionStoreService.containKey(clientId)) {
                SessionStore sessionStore = sessionStoreService.get(clientId);
                MqttQoS mqttQos = MqttQoS.valueOf(Math.min(qos.value(), subscribeStore.getQos()));
                if (mqttQos == MqttQoS.AT_MOST_ONCE) {
                    MqttMessage message = MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, mqttQos, retain, 0),
                            new MqttPublishVariableHeader(topic, 0),
                            Unpooled.buffer().writeBytes(content)
                    );
                    log.info("PUBLISH - clientId: {}  topic: {} qos: {}", clientId, topic, qos);
                    ChannelIdStore.get("brokerId" + "_" + sessionStore.getChannelId()).ifPresent(channelId -> {
                        ChannelGroupStore.find(channelId).writeAndFlush(message);
                    });
                } else if (mqttQos == MqttQoS.AT_LEAST_ONCE || mqttQos == MqttQoS.EXACTLY_ONCE) {
                    int messageId = messageIdService.getNextMessageId();
                    MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                            new MqttFixedHeader(MqttMessageType.PUBLISH, dup, mqttQos, retain, 0),
                            new MqttPublishVariableHeader(topic, messageId),
                            Unpooled.buffer().writeBytes(content)
                    );
                    log.info("PUBLISH - clientId: {} topic: {} qos: {} messageId: {}",
                            clientId, topic, mqttQos.value(), messageId);
                    DupPublishMessageStore dupPublishMessageStore = new DupPublishMessageStore();
                    dupPublishMessageStore.setClientId(clientId);
                    dupPublishMessageStore.setTopic(topic);
                    dupPublishMessageStore.setQos(mqttQos.value());
                    dupPublishMessageStore.setContent(content);
                    dupPublishMessageStoreService.put(clientId, dupPublishMessageStore);

                    ChannelIdStore.get(sessionStore.getBrokerId() + "_" + sessionStore.getChannelId()).ifPresent(channelId -> {
                        ChannelGroupStore.find(channelId).writeAndFlush(mqttMessage);
                    });
                }
            }
        });
    }
}
