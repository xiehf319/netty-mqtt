package com.github.netty.mqtt.broker.handler;

import com.github.netty.mqtt.broker.store.dup.DupPublishMessageStoreService;
import com.github.netty.mqtt.broker.store.id.MessageIdService;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 类描述:
 * broker publish消息给服务端 收到ack消息
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:55
 */
@Component
@Slf4j
public class MqttPubAckMessageHandler implements MqttMessageHandler<MqttPubAckMessage>{

    @Autowired
    private DupPublishMessageStoreService dupPublishMessageStoreService;

    @Autowired
    private MessageIdService messageIdService;

    @Override
    public boolean match(MqttMessageType mqttMessageType) {
        return MqttMessageType.PUBACK == mqttMessageType;
    }

    @Override
    public void handle(Channel channel, MqttPubAckMessage mqttMessage) {
        int messageId = mqttMessage.variableHeader().messageId();
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        log.info("PUBACK - clientId: {} messageId: {}", clientId, messageId);
        messageIdService.releaseMessageId(messageId);
        // 不一定有缓存 也要调用移除
        dupPublishMessageStoreService.remove(clientId, messageId);
    }
}
