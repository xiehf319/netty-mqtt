package com.github.netty.mqtt.broker.store.session;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/19 16:33
 */
public interface ISessionStoreService {

    void put(String clientId, SessionStore sessionStore, int expire);

    void expire(String clientId, int expire);

    SessionStore get(String clientId);

    boolean containKey(String clientId);

    void remove(String clientId);
}
