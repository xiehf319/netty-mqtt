package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.dup.DupPubRelMessageStoreService;
import com.github.netty.mqtt.broker.store.dup.DupPublishMessageStoreService;
import com.github.netty.mqtt.broker.store.session.SessionStore;
import com.github.netty.mqtt.broker.store.session.SessionStoreService;
import com.github.netty.mqtt.broker.store.subscribe.ISubscribeStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
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
public class MqttDisConnectMessageHandler implements MqttMessageHandler<MqttMessage>{

    @Autowired
    private SessionStoreService sessionStoreService;

    @Autowired
    private ISubscribeStoreService subscribeStoreService;

    @Autowired
    private DupPublishMessageStoreService dupPublishMessageStoreService;

    @Autowired
    private DupPubRelMessageStoreService dupPubRelMessageStoreService;

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.DISCONNECT == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttMessage mqttMessage) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        SessionStore sessionStore = sessionStoreService.get(clientId);
        if (sessionStore.isCleanSession()) {
            subscribeStoreService.removeForClient(clientId);
            dupPublishMessageStoreService.remove(clientId);
            dupPubRelMessageStoreService.remove(clientId);
        }
        log.info("DISCONNECT - clientId: {} cleanSession: {}", clientId, sessionStore.isCleanSession());
        sessionStoreService.remove(clientId);
        channel.close();
    }
}
