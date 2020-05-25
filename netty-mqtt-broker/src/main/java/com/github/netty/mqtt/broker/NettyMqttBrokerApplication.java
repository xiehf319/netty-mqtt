package com.github.netty.mqtt.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NettyMqttBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyMqttBrokerApplication.class, args);
    }

}
