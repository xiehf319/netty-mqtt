package com.github.netty.mqtt.broker.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/25 18:37
 */
@Configuration
public class BroadcastConfig {

    /**
     * 默认为local 或者配置为local
     * @return
     */
    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "mqtt.publish.broadcast", name = "type", havingValue = "local", matchIfMissing = true)
    public AsyncThreadBroadcast asyncThread(@Autowired BroadcastPublish broadcastPublish) {
        System.out.println("local");
        return new AsyncThreadBroadcast(broadcastPublish);
    }

    /**
     * local不存在就创建kafka 或者配置为kafka
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(AsyncThreadBroadcast.class)
    @ConditionalOnProperty(prefix = "mqtt.publish.broadcast", name = "type", havingValue = "kafka", matchIfMissing = false)
    public KafkaBroadcast kafkaProducer() {
        System.out.println("kafka");
        return new KafkaBroadcast();
    }

}
