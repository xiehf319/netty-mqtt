package com.github.netty.mqtt.broker.util;

import com.alibaba.fastjson.JSONObject;
import com.github.netty.mqtt.broker.store.session.SessionStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @description:
 * @createDate:2019/6/25$11:31$
 * @author: Heyfan Xie
 */
@UtilityClass
@Slf4j
public class StoreUtil {

    public static JSONObject transPublishToMap(SessionStore store) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("clientId", store.getClientId());
            jsonObject.put("channelId", store.getChannelId());
            jsonObject.put("cleanSession", store.isCleanSession());
            jsonObject.put("brokerId", store.getBrokerId());
            jsonObject.put("expire", store.getExpire());
            Optional.of(store.getWillMessage()).ifPresent(message -> {
                jsonObject.put("payload", new String(message.payload().array(), StandardCharsets.UTF_8));
                jsonObject.put("messageType", message.fixedHeader().messageType().value());
                jsonObject.put("idDup", message.fixedHeader().isDup());
                jsonObject.put("qosLevel", message.fixedHeader().qosLevel());
                jsonObject.put("isRetain", message.fixedHeader().isRetain());
                jsonObject.put("remainingLength", message.fixedHeader().remainingLength());
                jsonObject.put("topicName", message.variableHeader().topicName());
                jsonObject.put("hasWillMessage", true);
            });
            return jsonObject;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static SessionStore transMapToPublish(JSONObject jsonObject) {
        SessionStore sessionStore = new SessionStore();
        if ((Boolean) jsonObject.getOrDefault("hasWillMessage", false)) {
            String payload = jsonObject.getString("payload");
            ByteBuf buf = Unpooled.buffer().writeBytes(payload.getBytes());
            MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(
                    MqttMessageType.valueOf(jsonObject.getInteger("messageType")),
                    (Boolean) jsonObject.getOrDefault("isDup", false),
                    MqttQoS.valueOf(jsonObject.getInteger("qosLevel")),
                    jsonObject.getBoolean("isRetain"),
                    jsonObject.getInteger("remainingLength")
            );
            MqttPublishVariableHeader mqttPublishVariableHeader = new MqttPublishVariableHeader(
                    jsonObject.getString("topicName"),
                    jsonObject.getInteger("packetId")
            );
            MqttPublishMessage message = new MqttPublishMessage(mqttFixedHeader, mqttPublishVariableHeader, buf);
            sessionStore.setWillMessage(message);
        }
        sessionStore.setChannelId(jsonObject.getString("channelId"));
        sessionStore.setClientId(jsonObject.getString("clientId"));
        sessionStore.setCleanSession(jsonObject.getBoolean("cleanSession"));
        sessionStore.setBrokerId(jsonObject.getString("brokerId"));
        sessionStore.setExpire(jsonObject.getInteger("expire"));
        return sessionStore;
    }

}
