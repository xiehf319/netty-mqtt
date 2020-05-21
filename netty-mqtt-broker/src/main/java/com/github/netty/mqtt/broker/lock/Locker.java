package com.github.netty.mqtt.broker.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 14:44
 */
@Component
public class Locker {

    public void lock(String key, Perform perform) {

    }
}
