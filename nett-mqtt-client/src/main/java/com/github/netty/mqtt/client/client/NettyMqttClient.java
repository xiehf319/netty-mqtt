package com.github.netty.mqtt.client.client;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/13 15:08
 */
@Component
@Slf4j
public class NettyMqttClient {

    MqttClient mqttClient;

    MemoryPersistence persistence;

    String broker = "tcp://10.204.247.103:10002";

    String clientId = "hello_client";

    @PostConstruct
    public void init() throws Exception {

        persistence = new MemoryPersistence();
        mqttClient = new MqttClient(broker, clientId, persistence);
        nettyClient();
    }

    public void nettyClient() throws Exception {

        connect();

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                try {
                    connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                log.info("topic: {} message: {}", topic, mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                try {
                    log.info("deliveryComplete: {}", iMqttDeliveryToken.getMessage().toString());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void connect() throws Exception {
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        connectOptions.setCleanSession(true);
        mqttClient.connect(connectOptions);
    }

    public void publish(String topic, String message, Integer qos) throws Exception {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(qos);
        mqttClient.publish(topic, mqttMessage);
    }

    public void subscribe(String topicFilter) throws Exception {
        mqttClient.subscribe(topicFilter);
    }

    public void unsubscribe(String topicFilter) throws Exception {
        mqttClient.unsubscribe(topicFilter);
    }

    public void disConnect() throws Exception {
        mqttClient.disconnect();
    }
}
