package com.github.netty.mqtt.broker.store.id;

import com.github.netty.mqtt.broker.cache.MessageIdCache;
import com.github.netty.mqtt.broker.lock.Locker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 14:30
 */
@Service
public class MessageIdService {

    private final int MIN_MSG_ID = 1;
    private int nextMsgId = MIN_MSG_ID - 1;
    private final int MAX_MSG_ID = 65535;
    private final String LOCK_KEY = "NETTY:MQTT:MESSAGE_ID";

    @Autowired
    private MessageIdCache messageIdCache;


    @Autowired
    private Locker locker;

    public int getNextMessageId() {
        locker.lock(LOCK_KEY, () -> {
            do {
                nextMsgId++;
                if (nextMsgId > MAX_MSG_ID) {
                    nextMsgId = MIN_MSG_ID;
                }
            } while (messageIdCache.containKey(nextMsgId));
        });
        return nextMsgId;
    }

    public void releaseMessageId(int messageId) {
        locker.lock(LOCK_KEY, () -> {
            messageIdCache.remove(messageId);
        });
    }
}
