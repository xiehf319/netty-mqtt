package com.github.netty.mqtt.client.controller;

import com.github.netty.mqtt.client.client.NettyMqttClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 16:42
 */
@RestController
@RequestMapping("/mqtt")
@Slf4j
public class MqttClientController {

    @Autowired
    NettyMqttClient nettyMqttClient;

    @RequestMapping("/publish")
    public String publish(String topic, String message, Integer qos) {
        log.info("publish topic: {}", topic);
        log.info("publish message: {}", message);
        log.info("publish qos: {}", qos);
        try {
            nettyMqttClient.publish(topic, message, qos);
        } catch (Exception e) {
            log.error("publish error ", e);
            return "error";
        }
        return "success";
    }

    @RequestMapping("/subscribe")
    public String subscribe(String topicFilter) {
        log.info("subscribe topic: {}", topicFilter);
        try {
            nettyMqttClient.subscribe(topicFilter);
        } catch (Exception e) {
            log.error("subscribe error ", e);
            return "error";
        }
        return "success";
    }
}
