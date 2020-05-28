package com.github.netty.mqtt.broker.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.netty.mqtt.broker.store.dup.DupPubRelMessageStore;
import com.github.netty.mqtt.broker.store.dup.DupPublishMessageStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 16:52
 */
@Repository
public class DupPublishMessageCache {

    private final static String DUP_PUBLISH_CACHE_PREFIX = "netty:mqtt:dup:publish:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void put(String clientId, int messageId, DupPublishMessageStore messageStore) {
        redisTemplate.opsForHash().put(DUP_PUBLISH_CACHE_PREFIX + clientId, String.valueOf(messageId), JSON.toJSONString(messageStore));
    }

    public boolean containKey(String clientId) {
        Boolean result = redisTemplate.hasKey(clientId);
        return result == null ? false : result;
    }

    public ConcurrentHashMap<Integer, DupPublishMessageStore> get(String clientId) {
        ConcurrentHashMap<Integer, DupPublishMessageStore> map = new ConcurrentHashMap<>();
        HashOperations<String, String, String> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        Map<String, String> entries = stringObjectObjectHashOperations.entries(clientId);
        if (entries != null && !entries.isEmpty()) {
            entries.forEach((k, v) -> {
                map.put(Integer.valueOf(k), JSONObject.parseObject(v, DupPublishMessageStore.class));
            });
        }
        return map;
    }

    public void remove(String clientId, int messageId) {
        redisTemplate.opsForHash().delete(clientId, String.valueOf(messageId));
    }

    public void remove(String clientId) {
        redisTemplate.delete(clientId);
    }
}
