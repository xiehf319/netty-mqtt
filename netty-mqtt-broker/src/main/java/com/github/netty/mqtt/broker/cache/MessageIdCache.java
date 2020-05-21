package com.github.netty.mqtt.broker.cache;

import org.springframework.stereotype.Repository;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 14:31
 */
@Repository
public class MessageIdCache {

    public void put(int messageId) {

    }

    public void remove(int messageId) {

    }

    public boolean containKey(int messageId) {
        return false;
    }
}
