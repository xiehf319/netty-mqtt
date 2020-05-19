package com.github.netty.mqtt.broker.store.channel;

import io.netty.channel.ChannelId;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/19 16:42
 */
public class ChannelIdStore {

    private static final ConcurrentHashMap<String, ChannelId> CHANNELID_MAP = new ConcurrentHashMap<>();

    /**
     *
     * @param id  brokerId  channelId
     * @param channelId
     */
    public static void add(String id, ChannelId channelId) {
        CHANNELID_MAP.put(id, channelId);
    }

    public static void remove(String id) {
        CHANNELID_MAP.remove(id);
    }

    public static Optional<ChannelId> get(String id) {
        return Optional.of(CHANNELID_MAP.get(id));
    }

}
