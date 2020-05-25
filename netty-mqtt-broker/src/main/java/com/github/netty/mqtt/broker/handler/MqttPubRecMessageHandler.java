package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.dup.DupPubRelMessageStore;
import com.github.netty.mqtt.broker.store.dup.DupPubRelMessageStoreService;
import com.github.netty.mqtt.broker.store.dup.DupPublishMessageStoreService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
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
public class MqttPubRecMessageHandler implements MqttMessageHandler<MqttMessage>{

    @Autowired
    private DupPublishMessageStoreService dupPublishMessageStoreService;

    @Autowired
    private DupPubRelMessageStoreService dupPubRelMessageStoreService;

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.PUBREC == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttMessage mqttMessage) {
        int messageId = ((MqttMessageIdVariableHeader) mqttMessage.variableHeader()).messageId();
        MqttMessage message = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(messageId),
                null
        );
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        log.info("PUBREC - clientId: {}, messageId: {}", clientId, messageId);

        dupPublishMessageStoreService.remove(clientId, messageId);
        DupPubRelMessageStore dupPubRelMessageStore = new DupPubRelMessageStore();
        dupPubRelMessageStore.setClientId(clientId);
        dupPubRelMessageStore.setMessageId(messageId);
        dupPubRelMessageStoreService.put(clientId, dupPubRelMessageStore);
        channel.writeAndFlush(message);
    }
}
