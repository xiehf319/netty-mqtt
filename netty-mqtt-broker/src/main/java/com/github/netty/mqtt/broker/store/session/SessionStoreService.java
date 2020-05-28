package com.github.netty.mqtt.broker.store.session;

import com.alibaba.fastjson.JSONObject;
import com.github.netty.mqtt.broker.cache.SessionStoreCache;
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

    @Autowired
    private SessionStoreCache sessionStoreCache;

    @Override
    public void put(String clientId, SessionStore sessionStore, int expire) {
        sessionStoreCache.put(clientId, sessionStore, expire);
    }

    @Override
    public void expire(String clientId, int expire) {
        sessionStoreCache.expire(clientId, expire);
    }

    @Override
    public SessionStore get(String clientId) {
        return sessionStoreCache.get(clientId);
    }

    @Override
    public boolean containKey(String clientId) {
        return sessionStoreCache.containKey(clientId);
    }

    @Override
    public void remove(String clientId) {
        sessionStoreCache.remove(clientId);
    }
}
