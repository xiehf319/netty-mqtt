package com.github.netty.mqtt.broker.store.subscribe;

import java.util.List;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 17:21
 */
public interface ISubscribeStoreService {


    /**
     * 存储订阅信息
     *
     * @param topicFilter
     * @param subscribeStore
     */
    void put(String topicFilter, SubscribeStore subscribeStore);

    /**
     * 删除订阅
     *
     * @param topicFilter
     * @param clientId
     */
    void remove(String topicFilter, String clientId);

    /**
     * 删除clientId订阅的所有topic
     *
     * @param clientId
     */
    void removeForClient(String clientId);

    /**
     * 获取订阅存储集
     *
     * @param topic
     * @return
     */
    List<SubscribeStore> search(String topic);
}
