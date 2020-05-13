package com.github.netty.mqtt.broker.store;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.experimental.UtilityClass;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 17:11
 */
@UtilityClass
public class ChannelGroupStore {

    private final static ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup("ChannelGroupStore", GlobalEventExecutor.INSTANCE);

    public static void add(Channel channel) {
        CHANNEL_GROUP.add(channel);
    }

    public static Channel find(ChannelId channelId) {
        return CHANNEL_GROUP.find(channelId);
    }

    public static void discard(Channel channel) {
        CHANNEL_GROUP.remove(channel);
    }

    public static void broadcast(Object message, ChannelMatcher channelMatcher) {
        CHANNEL_GROUP.writeAndFlush(message, channelMatcher);
    }
}
