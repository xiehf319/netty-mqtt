package com.github.netty.mqtt.broker.store.subscribe;

import java.util.List;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 17:23
 */
@Deprecated
public class SubscribeStoreJdbcService implements ISubscribeStoreService{

    @Override
    public void put(String topicFilter, SubscribeStore subscribeStore) {

    }

    @Override
    public void remove(String topicFilter, String clientId) {

    }

    @Override
    public void removeForClient(String clientId) {

    }

    @Override
    public List<SubscribeStore> search(String topic) {
        return null;
    }
}
