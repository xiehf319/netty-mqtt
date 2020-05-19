package com.github.netty.mqtt.broker.store.retain;

import org.springframework.stereotype.Service;

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


    @Override
    public void put(String topic, RetainMessageStore store) {

    }

    @Override
    public RetainMessageStore get(String topic) {
        return null;
    }

    @Override
    public void remove(String topic) {

    }

    @Override
    public boolean containKey(String topic) {
        return false;
    }

    @Override
    public List<RetainMessageStore> search(String topicFilter) {
        return null;
    }
}
