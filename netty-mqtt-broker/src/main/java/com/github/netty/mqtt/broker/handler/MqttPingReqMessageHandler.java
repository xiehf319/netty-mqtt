package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.channel.ChannelIdStore;
import com.github.netty.mqtt.broker.store.session.SessionStore;
import com.github.netty.mqtt.broker.store.session.SessionStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MqttPingReqMessageHandler implements MqttMessageHandler<MqttMessage>{

    @Autowired
    private SessionStoreService sessionStoreService;

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.PINGREQ == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttMessage mqttMessage) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        log.info("PINGREQ - clientId: {}", clientId);
        if (sessionStoreService.containKey(clientId)) {
            SessionStore sessionStore = sessionStoreService.get(clientId);
            ChannelIdStore.get("brokerId" + "_" + sessionStore.getChannelId()).ifPresent(channelId -> {
                sessionStoreService.expire(clientId, sessionStore.getExpire());
                MqttMessage mqttPingRespMessage = MqttMessage.PINGRESP;
                channel.writeAndFlush(mqttPingRespMessage);
            });
        }
    }
}
