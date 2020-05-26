package com.github.netty.mqtt.broker.dispatch;

import lombok.extern.slf4j.Slf4j;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/25 18:26
 */
@Slf4j
public class KafkaDispatcher implements Dispatcher {

    @Override
    public void internalSend(String channelId, DispatchMessageDTO messageDTO) {
        log.info("广播PUBLISH，来源channelId: {} , 消息内容: {}", channelId, messageDTO.toString());
    }
}
