package com.github.netty.mqtt.broker.dispatch;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/25 18:33
 */
public interface Dispatcher {

    /**
     * 发布消息
     *
     * @param channelId  来源客户端
     * @param messageDTO 消息内容
     */
    void dispatch(String channelId, DispatchMessageDTO messageDTO);
}
