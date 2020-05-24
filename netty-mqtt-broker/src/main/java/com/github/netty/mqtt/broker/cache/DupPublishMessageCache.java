package com.github.netty.mqtt.broker.cache;

import com.github.netty.mqtt.broker.store.dup.DupPublishMessageStore;
import org.springframework.stereotype.Repository;

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

    public void put(String clientId, int messageId, DupPublishMessageStore messageStore) {

    }

    public boolean containKey(String clientId) {
        return false;
    }

    public ConcurrentHashMap<Integer, DupPublishMessageStore> get(String clientId) {
        return null;
    }

    public void remove(String clientId, int messageId) {

    }

    public void remove(String clientId) {

    }
}
