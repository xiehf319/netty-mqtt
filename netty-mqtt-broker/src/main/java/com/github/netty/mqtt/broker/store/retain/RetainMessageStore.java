package com.github.netty.mqtt.broker.store.retain;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述:
 * 保留消息
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/15 9:49
 */
@Data
public class RetainMessageStore implements Serializable {

    /**
     * 主题
     */
    private String topic;

    /**
     * 消息内容
     */
    private byte[] content;

    /**
     * 消息质量等级
     */
    private int qos;
}
