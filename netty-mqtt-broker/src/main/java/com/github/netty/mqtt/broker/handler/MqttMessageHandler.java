package com.github.netty.mqtt.broker.handler;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;


/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:54
 */
public interface MqttMessageHandler<T extends MqttMessage> {

    /**
     *
     * @param mqttMessageType
     * @return
     */
    boolean match(MqttMessageType mqttMessageType);

    /**
     * 处理消息
     *
     * @param channel     通道
     * @param mqttMessage mqtt消息
     */
    void handle(Channel channel, T mqttMessage);
}
