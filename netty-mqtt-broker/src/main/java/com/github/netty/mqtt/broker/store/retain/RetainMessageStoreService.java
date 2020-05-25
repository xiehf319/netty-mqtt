package com.github.netty.mqtt.broker.store.retain;

import com.github.netty.mqtt.broker.cache.RetainMessageCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/15 9:51
 */
@Service
public class RetainMessageStoreService implements IRetainMessageStoreService {

    @Autowired
    private RetainMessageCache retainMessageCache;

    @Override
    public void put(String topic, RetainMessageStore store) {
        retainMessageCache.put(topic, store);
    }

    @Override
    public RetainMessageStore get(String topic) {
        return retainMessageCache.get(topic);
    }

    @Override
    public void remove(String topic) {
        retainMessageCache.remove(topic);
    }

    @Override
    public boolean containKey(String topic) {
        return retainMessageCache.containKey(topic);
    }

    @Override
    public List<RetainMessageStore> search(String topicFilter) {
        List<RetainMessageStore> retainMessageStores = new ArrayList<>();
        if (!topicFilter.contains("#") && !topicFilter.contains("+")) {
            if (retainMessageCache.containKey(topicFilter)) {
                retainMessageStores.add(retainMessageCache.get(topicFilter));
            }
        } else {
            retainMessageCache.all().forEach((topic, val) -> {
                if (topic.split("/").length >= topicFilter.split("/").length) {
                    String[] splitTopics = topic.split("/");
                    String[] splitTopicFilters = topicFilter.split("/");
                }
            });
        }
        return retainMessageStores;
    }
}
