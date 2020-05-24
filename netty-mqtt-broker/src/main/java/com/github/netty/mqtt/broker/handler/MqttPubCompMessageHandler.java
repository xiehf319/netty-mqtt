package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.dup.DupPubRelMessageStoreService;
import com.github.netty.mqtt.broker.store.id.MessageIdService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
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
public class MqttPubCompMessageHandler implements MqttMessageHandler<MqttMessage> {

    @Autowired
    private MessageIdService messageIdService;

    @Autowired
    private DupPubRelMessageStoreService dupPubRelMessageStoreService;

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.PUBCOMP == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttMessage mqttMessage) {

        int messageId = ((MqttMessageIdVariableHeader) mqttMessage.variableHeader()).messageId();
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        log.info("PUBCOMP - clientId: {}  messageId: {}", clientId, messageId);
        dupPubRelMessageStoreService.remove(clientId, messageId);
        messageIdService.releaseMessageId(messageId);
    }
}
