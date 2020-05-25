package com.github.netty.mqtt.broker.internal;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/25 18:29
 */
@Data
@Builder
public class BroadcastMessageDTO implements Serializable {

    /**
     * 代理id
     */
    private String brokerId;

    /**
     * 主题
     */
    private String topic;

    /**
     * 消息指定等级
     */
    private int qos;

    /**
     * 内容
     */
    private byte[] content;

    /**
     * 是否保留
     */
    private boolean retain;

    /**
     * 是否重发
     */
    private boolean dup;

    @Override
    public String toString() {
        return "BroadcastMessageDTO{" +
                "brokerId='" + brokerId + '\'' +
                ", topic='" + topic + '\'' +
                ", qos=" + qos +
                ", content=" + new String(content, StandardCharsets.UTF_8) +
                ", retain=" + retain +
                ", dup=" + dup +
                '}';
    }
}
