package com.github.netty.mqtt.broker.cache;

import org.springframework.stereotype.Component;

/**
 * 类描述:
 * 通配符 + #
 *
 * 保存在分布式的存储系统中 这里用redis
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 17:24
 */
@Component
public class SubscribeWildcardCache implements ISubscribeCache {

    /**
     * 订阅 通配符 的
     */
    private final String WILDCARD_PREFIX = "NETTY:MQTT:SUBSCRIBE:STORE:WILDCARD:";

    private final String WILDCARD_CLIENT_PREFIX = "NETTY:MQTT:SUBSCRIBE:CLIENT:WILDCARD:";

}
