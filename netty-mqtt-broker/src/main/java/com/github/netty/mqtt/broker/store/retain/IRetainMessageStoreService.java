package com.github.netty.mqtt.broker.store.retain;

import java.util.List;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/15 9:51
 */
public interface IRetainMessageStoreService {

    void put(String topic, RetainMessageStore store);

    RetainMessageStore get(String topic);

    void remove(String topic);

    boolean containKey(String topic);

    List<RetainMessageStore> search(String topicFilter);

}
