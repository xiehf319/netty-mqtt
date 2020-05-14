package com.github.netty.mqtt.client.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 14:11
 */
@ServerEndpoint("/netty/mqtt/{userId}")
@Component
@Slf4j
public class WebSocketServer {

    private static int onlineCount = 0;

    private static ConcurrentHashMap<String, WebSocketServer> websocketMap = new ConcurrentHashMap<>();

    private Session session;

    private String userId;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (websocketMap.contains(userId)) {
            websocketMap.remove(userId);
            websocketMap.put(userId, this);
        } else {
            websocketMap.put(userId, this);

            addOnlineCount();
        }
        log.info("用户连接: {} 当前在线人数: " + getOnlineCount());

        sendMessage("连接成功");

    }

    @OnClose
    public void onClose() {
        if (websocketMap.containsKey(userId)) {
            websocketMap.remove(userId);

            subOnlineCount();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户: {} 报文: {}", userId, message);

        if (!StringUtils.isEmpty(message)) {
            JSONObject jsonObject = JSON.parseObject(message);
            String toUserId = jsonObject.getString("toUserId");
            if (!StringUtils.isEmpty(toUserId) && websocketMap.containsKey(toUserId)) {
                websocketMap.get(toUserId).sendMessage(message);
            } else {
                //
                log.info("转发到其他的服务器: {}", message);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误: {}  原因:{}", userId, error.getMessage());
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendInfo(String message, @PathParam("userId") String userId) {

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
