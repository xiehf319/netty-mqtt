package com.github.netty.mqtt.broker.cache;

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
public class SubscribeWildcardCache implements ISubscribeCache {

    private final String PREFIX = "NETTY:MQTT:SUBSCRIBE:WILDCARD:";


}
