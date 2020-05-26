package com.github.netty.mqtt.broker.dispatch;

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
public class DispatchConfig {

    /**
     * 默认为local 或者配置为local
     * @return
     */
    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "mqtt.publish.broadcast", name = "type", havingValue = "local", matchIfMissing = true)
    public ThreadDispatcher asyncThread(@Autowired PublishDispatchExecutor broadcastPublish) {
        System.out.println("local");
        return new ThreadDispatcher(broadcastPublish);
    }

    /**
     * local不存在就创建kafka 或者配置为kafka
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ThreadDispatcher.class)
    @ConditionalOnProperty(prefix = "mqtt.publish.broadcast", name = "type", havingValue = "kafka", matchIfMissing = false)
    public KafkaDispatcher kafkaProducer() {
        System.out.println("kafka");
        return new KafkaDispatcher();
    }

}
