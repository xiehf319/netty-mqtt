package com.github.netty.mqtt.broker.store.dup;

import com.github.netty.mqtt.broker.cache.DupPublishMessageCache;
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
public class DupPublishMessageStoreService {

    @Autowired
    private DupPublishMessageCache dupPublishMessageCache;

    public void put(String clientId, DupPublishMessageStore messageStore) {

        dupPublishMessageCache.put(clientId, messageStore.getMessageId(), messageStore);
    }

    public List<DupPublishMessageStore> get(String clientId) {
        if (dupPublishMessageCache.containKey(clientId)) {
            ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
            if (map != null) {
                return new ArrayList<>(map.values());
            }
        }
        return new ArrayList<>();
    }

    public void remove(String clientId, int messageId) {
        dupPublishMessageCache.remove(clientId, messageId);
    }

    public void remove(String clientId) {
        dupPublishMessageCache.remove(clientId);
    }
}
