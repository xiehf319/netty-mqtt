package com.github.netty.mqtt.broker.cache;

import com.alibaba.fastjson.JSON;
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
 * 非通配符 订阅缓存
 * 保存在分布式的存储系统中 这里用redis
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 17:24
 */
@Component
public class SubscribeNotWildcardCache {

    private final String NOT_WILDCARD_PREFIX = "NETTY:MQTT:SUBSCRIBE:STORE:NOT_WILDCARD:";

    private final String NOT_WILDCARD_CLIENT_PREFIX = "NETTY:MQTT:SUBSCRIBE:CLIENT:NOT_WILDCARD:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 新增订阅对象
     *
     * @param topic
     * @param clientId
     * @param store
     * @return
     */
    public SubscribeStore put(String topic, String clientId, SubscribeStore store) {
        redisTemplate.opsForHash().put(NOT_WILDCARD_PREFIX + topic, clientId, JSON.toJSONString(store));
        redisTemplate.opsForSet().add(NOT_WILDCARD_CLIENT_PREFIX + clientId, topic);
        return store;
    }

    public SubscribeStore get(String topic, String clientId) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String storeStr = hashOperations.get(NOT_WILDCARD_PREFIX + topic, clientId);
        if (storeStr == null) {
            return null;
        }
        return JSONObject.parseObject(storeStr, SubscribeStore.class);
    }

    public boolean containKey(String topic, String clientId) {
        return redisTemplate.opsForHash().hasKey(NOT_WILDCARD_PREFIX + topic, clientId);
    }

    public void remove(String topic, String clientId) {
        redisTemplate.opsForHash().delete(NOT_WILDCARD_PREFIX + topic, clientId);
        redisTemplate.opsForSet().remove(NOT_WILDCARD_CLIENT_PREFIX + clientId);
    }

    public void removeByClientId(String clientId) {
        Set<String> members = redisTemplate.opsForSet().members(NOT_WILDCARD_CLIENT_PREFIX + clientId);
        if (members != null) {
            members.forEach(topic -> {
                redisTemplate.opsForHash().delete(NOT_WILDCARD_PREFIX + topic, clientId);
            });
        }
        redisTemplate.delete(NOT_WILDCARD_CLIENT_PREFIX + clientId);
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, SubscribeStore>> all() {
        ConcurrentHashMap<String, ConcurrentHashMap<String, SubscribeStore>> map
                = new ConcurrentHashMap<>();
        Set<String> keys = redisTemplate.keys(NOT_WILDCARD_PREFIX + "*");
        if (keys != null) {
            keys.forEach(key -> {
                ConcurrentHashMap<String, SubscribeStore> subMap = new ConcurrentHashMap<>();
                HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
                Map<String, String> entries = hashOperations.entries(key);
                entries.forEach((k, v) -> subMap.put(k, JSONObject.parseObject(v, SubscribeStore.class)));
                map.put(key, subMap);
            });
        }
        return map;
    }

    public List<SubscribeStore> all(String topic) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        Map<String, String> entries = hashOperations.entries(NOT_WILDCARD_PREFIX + topic);
        return entries.values().stream().map(entry -> JSONObject.parseObject(entry, SubscribeStore.class)).collect(Collectors.toList());
    }
}
