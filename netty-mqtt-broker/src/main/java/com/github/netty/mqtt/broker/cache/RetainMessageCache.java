package com.github.netty.mqtt.broker.cache;

import com.alibaba.fastjson.JSONObject;
import com.github.netty.mqtt.broker.store.retain.RetainMessageStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/15 10:06
 */
@Component
public class RetainMessageCache {

    private static final String RETAIN_MESSAGE_PREFIX = "NETTY:MQTT:RETAIN:MESSAGE:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public RetainMessageStore put(String topic, RetainMessageStore messageStore) {
        redisTemplate.opsForValue().set(RETAIN_MESSAGE_PREFIX + topic, JSONObject.toJSONString(messageStore));
        return messageStore;
    }

    public RetainMessageStore get(String topic) {
        String value = redisTemplate.opsForValue().get(RETAIN_MESSAGE_PREFIX + topic);
        if (value == null) {
            return null;
        }
        return JSONObject.parseObject(value, RetainMessageStore.class);
    }

    public boolean containKey(String topic) {
        return redisTemplate.hasKey(RETAIN_MESSAGE_PREFIX + topic);
    }

    public Map<String, RetainMessageStore> all() {
        Map<String, RetainMessageStore> map = new LinkedHashMap<>();
        Set<String> keys = redisTemplate.keys(RETAIN_MESSAGE_PREFIX + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            keys.forEach(key -> {
                map.put(key.substring(RETAIN_MESSAGE_PREFIX.length()),
                        JSONObject.parseObject(valueOperations.get(key), RetainMessageStore.class));
            });
        }
        return map;
    }

    public void remove(String topic) {
        redisTemplate.opsForValue().getOperations().delete(RETAIN_MESSAGE_PREFIX + topic);
    }
}
