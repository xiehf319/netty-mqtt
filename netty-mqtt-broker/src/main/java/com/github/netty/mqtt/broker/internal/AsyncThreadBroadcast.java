package com.github.netty.mqtt.broker.internal;

import io.netty.handler.codec.mqtt.MqttQoS;
import org.springframework.scheduling.annotation.Async;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/25 18:26
 */
public class AsyncThreadBroadcast implements Broadcast {

    private BroadcastPublish broadcastPublish;

    public AsyncThreadBroadcast(BroadcastPublish broadcastPublish) {
        this.broadcastPublish = broadcastPublish;
    }

    @Override
    @Async
    public void internalSend(String channelId, BroadcastMessageDTO messageDTO) {
        broadcastPublish.sendPublishMessage(
                messageDTO.getTopic(),
                MqttQoS.valueOf(messageDTO.getQos()),
                messageDTO.getContent(),
                messageDTO.isRetain(),
                messageDTO.isDup());
    }
}
