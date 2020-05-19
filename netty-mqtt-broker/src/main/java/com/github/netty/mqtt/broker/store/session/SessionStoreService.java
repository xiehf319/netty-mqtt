package com.github.netty.mqtt.broker.store.session;

import com.alibaba.fastjson.JSONObject;
import com.github.netty.mqtt.broker.util.StoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/19 16:33
 */
@Service
public class SessionStoreService implements ISessionStoreService {

    private final static String SESSION_CACHE_PREFIX = "NETTY:MQTT:SESSION:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void put(String clientId, SessionStore sessionStore, int expire) {
        JSONObject jsonObject = StoreUtil.transPublishToMap(sessionStore);
        if (jsonObject != null) {
            if (sessionStore.getExpire() > 0) {
                redisTemplate.opsForValue().set(SESSION_CACHE_PREFIX + clientId, jsonObject.toJSONString(), expire, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(SESSION_CACHE_PREFIX + clientId, jsonObject.toJSONString());
            }
        }
    }

    @Override
    public void expire(String clientId, int expire) {
        redisTemplate.expire(SESSION_CACHE_PREFIX + clientId, expire, TimeUnit.SECONDS);
    }

    @Override
    public SessionStore get(String clientId) {
        String value = redisTemplate.opsForValue().get(SESSION_CACHE_PREFIX + clientId);
        if (value == null) {
            return null;
        }
        JSONObject object = JSONObject.parseObject(value);
        return StoreUtil.transMapToPublish(object);
    }

    @Override
    public boolean containKey(String clientId) {
        return redisTemplate.hasKey(SESSION_CACHE_PREFIX + clientId);
    }

    @Override
    public void remove(String clientId) {
        redisTemplate.opsForValue().getOperations().delete(SESSION_CACHE_PREFIX + clientId);
    }
}
