package com.github.netty.mqtt.broker.cache;

import com.github.netty.mqtt.broker.store.dup.DupPubRelMessageStore;
import org.springframework.stereotype.Repository;

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
    public void put(String clientId, int messageId, DupPubRelMessageStore messageStore) {

    }

    public boolean containKey(String clientId) {
        return false;
    }

    public void remove(String clientId, int messageId) {

    }

    public void remove(String clientId) {

    }

    public ConcurrentHashMap<Integer, DupPubRelMessageStore> get(String clientId) {
        return null;
    }
}
