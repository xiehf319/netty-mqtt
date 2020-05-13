package com.github.netty.mqtt.broker.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 14:29
 */
@Slf4j
public class BrokerHandler extends SimpleChannelInboundHandler<MqttMessage> {

    private MqttMessageHandlerSelector mqttMessageHandlerSelector;

    public BrokerHandler(MqttMessageHandlerSelector mqttMessageHandlerSelector) {
        this.mqttMessageHandlerSelector = mqttMessageHandlerSelector;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, MqttMessage mqttMessage) throws Exception {
        log.info("收到的消息: {}", mqttMessage.toString());
        MqttMessageType mqttMessageType = mqttMessage.fixedHeader().messageType();
        Channel channel = context.channel();
        MqttMessageHandler handler = mqttMessageHandlerSelector.select(mqttMessageType);
        if (handler != null) {
            handler.handle(channel, mqttMessage);
        }
    }
}
