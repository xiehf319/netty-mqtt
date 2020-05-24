package com.github.netty.mqtt.broker.store.dup;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述:
 * PUBREL 重发消息存储对象
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 15:47
 */
@Data
public class DupPubRelMessageStore implements Serializable {

    private static final long serialVersionUID = -8112511377194421600L;

    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 消息id
     */
    private int messageId;
}
