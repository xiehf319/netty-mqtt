package com.github.netty.mqtt.broker.launch;

import com.github.netty.mqtt.broker.handler.MqttMessageHandler;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:59
 */
@Component
public class MqttMessageHandlerSelector {

    @Autowired
    private List<MqttMessageHandler> messageHandlerList;

    public MqttMessageHandler select(MqttMessageType mqttMessageType) {
        for (MqttMessageHandler mqttMessageHandler : messageHandlerList) {
            if (mqttMessageHandler.match(mqttMessageType)) {
                return mqttMessageHandler;
            }
        }
        return null;
    }
}
