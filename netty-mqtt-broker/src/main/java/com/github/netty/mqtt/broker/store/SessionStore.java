package com.github.netty.mqtt.broker.store;

import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.Data;

import java.io.Serializable;

@Data
public class SessionStore implements Serializable {
    private static final long serialVersionUID = -8112511377194421600L;

    /**
     * 代理id
     */
    private String brokerId;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 通道id
     */
    private String channelId;

    /**
     * 遗嘱保留时间
     */
    private int expire;

    /**
     * 是否会话清理
     */
    private boolean cleanSession;

    /**
     * 遗嘱消息
     */
    private MqttPublishMessage willMessage;

    public SessionStore() {
    }

    public SessionStore(String brokerId, String clientId, String channelId, int expire, boolean cleanSession) {
        this.brokerId = brokerId;
        this.clientId = clientId;
        this.channelId = channelId;
        this.expire = expire;
        this.cleanSession = cleanSession;
    }
}
