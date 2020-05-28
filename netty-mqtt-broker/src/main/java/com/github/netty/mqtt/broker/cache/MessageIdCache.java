package com.github.netty.mqtt.broker.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 14:31
 */
@Repository
public class MessageIdCache {

    private static final String MESSAGE_ID_CACHE_PREFIX = "netty:mqtt:message_id:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void put(int messageId) {
        redisTemplate.opsForValue().set(MESSAGE_ID_CACHE_PREFIX + messageId, String.valueOf(messageId));
    }

    public void remove(int messageId) {
        redisTemplate.opsForValue().getOperations().delete(MESSAGE_ID_CACHE_PREFIX + messageId);
    }

    public boolean containKey(int messageId) {
        return redisTemplate.hasKey(MESSAGE_ID_CACHE_PREFIX + messageId);
    }
}
