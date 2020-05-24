package com.github.netty.mqtt.broker.store.dup;

import com.github.netty.mqtt.broker.cache.DupPubRelMessageStoreCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 14:27
 */
@Service
public class DupPubRelMessageStoreService {

    @Autowired
    private DupPubRelMessageStoreCache dupPubRelMessageStoreCache;


    public void put(String clientId, DupPubRelMessageStore messageStore) {
        dupPubRelMessageStoreCache.put(clientId, messageStore.getMessageId(), messageStore);
    }

    public List<DupPubRelMessageStore> get(String clientId) {
        if (dupPubRelMessageStoreCache.containKey(clientId)) {
            ConcurrentHashMap<Integer, DupPubRelMessageStore> map = dupPubRelMessageStoreCache.get(clientId);
            if (map != null) {
                return new ArrayList<>(map.values());
            }
        }
        return new ArrayList<>();
    }

    public void remove(String clientId, int messageId) {
        dupPubRelMessageStoreCache.remove(clientId, messageId);
    }

    public void remove(String clientId) {
        dupPubRelMessageStoreCache.remove(clientId);
    }
}
