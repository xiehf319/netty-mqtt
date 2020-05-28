package com.github.netty.mqtt.broker.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.netty.mqtt.broker.store.dup.DupPubRelMessageStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 17:24
 */
@Repository
public class DupPubRelMessageStoreCache {

    private static final String DUP_PUB_REL_CACHE_PREFIX = "netty:mqtt:dup:rel:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void put(String clientId, int messageId, DupPubRelMessageStore messageStore) {
        redisTemplate.opsForHash().put(DUP_PUB_REL_CACHE_PREFIX + clientId, String.valueOf(messageId), JSON.toJSONString(messageStore));
    }

    public boolean containKey(String clientId) {
        Boolean result = redisTemplate.hasKey(DUP_PUB_REL_CACHE_PREFIX + clientId);
        return result == null ? false : result;
    }

    public void remove(String clientId, int messageId) {
        redisTemplate.opsForHash().delete(DUP_PUB_REL_CACHE_PREFIX  + clientId, String.valueOf(messageId));
    }

    public void remove(String clientId) {
        redisTemplate.delete(DUP_PUB_REL_CACHE_PREFIX + clientId);
    }

    public ConcurrentHashMap<Integer, DupPubRelMessageStore> get(String clientId) {
        ConcurrentHashMap<Integer, DupPubRelMessageStore> map = new ConcurrentHashMap<>();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        Map<String, String> entries = hashOperations.entries(DUP_PUB_REL_CACHE_PREFIX + clientId);
        if (!StringUtils.isEmpty(entries)) {
            entries.forEach((k, v) -> {
                map.put(Integer.valueOf(k), JSONObject.parseObject(v, DupPubRelMessageStore.class));
            });
        }
        return map;
    }
}
