package com.github.netty.mqtt.broker.store.subscribe;

import com.github.netty.mqtt.broker.cache.SubscribeNotWildcardCache;
import com.github.netty.mqtt.broker.cache.SubscribeWildcardCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 17:22
 */
@Service
public class SubscribeStoreRedisService implements ISubscribeStoreService {
    @Autowired
    private SubscribeWildcardCache subscribeWildcardCache;
    @Autowired
    private SubscribeNotWildcardCache subscribeNotWildcardCache;

    @Override
    public void put(String topicFilter, SubscribeStore subscribeStore) {
        if (topicFilter.contains("#") || topicFilter.contains("+")) {
            subscribeWildcardCache.put(topicFilter, subscribeStore.getClientId(), subscribeStore);
        } else {
            subscribeNotWildcardCache.put(topicFilter, subscribeStore.getClientId(), subscribeStore);
        }
    }

    @Override
    public void remove(String topicFilter, String clientId) {
        if (topicFilter.contains("#") || topicFilter.contains("+")) {
            subscribeWildcardCache.remove(topicFilter, clientId);
        } else {
            subscribeNotWildcardCache.remove(topicFilter, clientId);
        }
    }

    @Override
    public void removeForClient(String clientId) {
        subscribeNotWildcardCache.removeByClientId(clientId);
        subscribeWildcardCache.removeByClientId(clientId);
    }

    @Override
    public List<SubscribeStore> search(String topic) {
        List<SubscribeStore> subscribeStores = new ArrayList<SubscribeStore>();
        List<SubscribeStore> list = subscribeNotWildcardCache.all(topic);
        if (list.size() > 0) {
            subscribeStores.addAll(list);
        }
        subscribeWildcardCache.all().forEach((topicFilter, map) -> {
            if (topic.split("/").length >= topicFilter.split("/").length) {
                String[] splitTopics = topic.split( "/");
                String[] splitTopicFilters = topicFilter.split("/");
                StringBuilder newTopicFilter = new StringBuilder();
                for (int i = 0; i < splitTopicFilters.length; i++) {
                    String value = splitTopicFilters[i];
                    if ("+".equals(value)) {
                        newTopicFilter.append("+/");
                    } else if ("#".equals(value)) {
                        newTopicFilter.append("#/");
                        break;
                    } else {
                        newTopicFilter.append(splitTopics[i]).append("/");
                    }
                }
                newTopicFilter.deleteCharAt(newTopicFilter.length() - 1);
                if (topicFilter.equals(newTopicFilter.toString())) {
                    Collection<SubscribeStore> collection = map.values();
                    List<SubscribeStore> subscribeStoreList = new ArrayList<SubscribeStore>(collection);
                    subscribeStores.addAll(subscribeStoreList);
                }
            }
        });
        return subscribeStores;
    }
}
