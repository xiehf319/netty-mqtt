package com.github.netty.mqtt.broker.internal;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/25 18:33
 */
public interface Broadcast {

    void internalSend(String channelId, BroadcastMessageDTO messageDTO);
}
