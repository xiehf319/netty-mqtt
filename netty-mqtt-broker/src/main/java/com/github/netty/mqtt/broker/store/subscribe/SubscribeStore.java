package com.github.netty.mqtt.broker.store.subscribe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 17:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeStore {

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 订阅的topic
     */
    private String topicFilter;

    /**
     * 订阅最高消息质量等级
     */
    private Integer qos;
}
