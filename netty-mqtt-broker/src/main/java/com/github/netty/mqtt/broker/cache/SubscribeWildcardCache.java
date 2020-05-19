package com.github.netty.mqtt.broker.cache;

import com.alibaba.fastjson.JSONObject;
import com.github.netty.mqtt.broker.store.subscribe.SubscribeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 类描述:
 * 通配符 + #
 * <p>
 * 保存在分布式的存储系统中 这里用redis
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 17:24
 */
@Component
public class SubscribeWildcardCache {

    /**
     * 订阅 通配符 的
     */
    private final String WILDCARD_PREFIX = "NETTY:MQTT:SUBSCRIBE:STORE:WILDCARD:";

    private final String WILDCARD_CLIENT_PREFIX = "NETTY:MQTT:SUBSCRIBE:CLIENT:WILDCARD:";


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public SubscribeStore put(String topic, String clientId, SubscribeStore store) {
        redisTemplate.opsForHash().put(WILDCARD_PREFIX + topic, clientId, JSONObject.toJSONString(store));
        redisTemplate.opsForSet().add(WILDCARD_CLIENT_PREFIX + clientId, topic);
        return store;
    }

    public SubscribeStore get(String topic, String clientId) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return JSONObject.parseObject(hashOperations.get(WILDCARD_PREFIX + topic, clientId), SubscribeStore.class);
    }

    public boolean containsKey(String topic, String clientId) {
        return redisTemplate.opsForHash().hasKey(topic, clientId);
    }

    public void remove(String topic, String clientId) {
        redisTemplate.opsForHash().delete(WILDCARD_PREFIX + topic, clientId);
        redisTemplate.opsForSet().remove(WILDCARD_CLIENT_PREFIX + clientId, topic);
    }

    public void removeByClientId(String clientId) {
        Set<String> members = redisTemplate.opsForSet().members(WILDCARD_CLIENT_PREFIX + clientId);
        if (members != null) {
            members.forEach(topic -> {
                redisTemplate.opsForHash().delete(WILDCARD_PREFIX + topic, clientId);
            });
        }
        redisTemplate.delete(WILDCARD_CLIENT_PREFIX + clientId);
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, SubscribeStore>> all() {
        ConcurrentHashMap<String, ConcurrentHashMap<String, SubscribeStore>> map = new ConcurrentHashMap<>();
        Set<String> keys = redisTemplate.keys(WILDCARD_PREFIX + "*");
        if (keys != null && keys.size() > 0) {
            keys.forEach(key -> {
                ConcurrentHashMap<String, SubscribeStore> subMap = new ConcurrentHashMap<>();
                HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
                Map<String, String> entries = hashOperations.entries(key);
                entries.forEach((k, v) -> {
                    subMap.put(k, JSONObject.parseObject(v, SubscribeStore.class));
                });
                map.put(key, subMap);
            });
        }
        return map;
    }

    public List<SubscribeStore> all(String topic) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        Map<String, String> entries = hashOperations.entries(topic);
        return entries.values().stream().map(entry -> JSONObject.parseObject(entry, SubscribeStore.class)).collect(Collectors.toList());
    }
}
