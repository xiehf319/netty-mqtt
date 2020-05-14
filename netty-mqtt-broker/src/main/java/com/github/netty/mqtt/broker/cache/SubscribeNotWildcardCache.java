package com.github.netty.mqtt.broker.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.netty.mqtt.broker.store.SubscribeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述:
 * 非通配符 订阅缓存
 * 保存在分布式的存储系统中 这里用redis
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 17:24
 */
public class SubscribeNotWildcardCache implements ISubscribeCache {

    private final String PREFIX = "NETTY:MQTT:NOT_WILDCARD:SUBSCRIBE:";

    private final String CLIENT_PREFIX = "NETTY:MQTT:NOT_WILDCARD:SUBSCRIBE_CLIENT:";

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
        redisTemplate.opsForHash().put(PREFIX + topic, clientId, JSON.toJSONString(store));
        redisTemplate.opsForSet().add(CLIENT_PREFIX + clientId, topic);
        return store;
    }

    public SubscribeStore get(String topic, String clientId) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String storeStr = hashOperations.get(PREFIX + topic, clientId);
        if (storeStr == null) {
            return null;
        }
        return JSONObject.parseObject(storeStr, SubscribeStore.class);
    }

    public boolean containKey(String topic, String clientId) {
        return redisTemplate.opsForHash().hasKey(PREFIX + topic, clientId);
    }

    public void remove(String topic, String clientId) {
        redisTemplate.opsForHash().delete(PREFIX + topic, clientId);
        redisTemplate.opsForSet().remove(CLIENT_PREFIX + clientId);
    }

    public void removeByClientId(String clientId) {
        Set<String> members = redisTemplate.opsForSet().members(CLIENT_PREFIX + clientId);
        if (members != null) {
            members.forEach(topic -> {
                redisTemplate.opsForHash().delete(PREFIX + topic, clientId);
            });
        }
        redisTemplate.delete(CLIENT_PREFIX + clientId);
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, SubscribeStore>> all() {
        ConcurrentHashMap<String, ConcurrentHashMap<String, SubscribeStore>> map
                = new ConcurrentHashMap<>();
        Set<String> keys = redisTemplate.keys(PREFIX + "*");
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
}
