package com.github.netty.mqtt.broker.service;

public interface IAuthService {

    /**
     * 校验用户名密码是否正确
     *
     * @param userName  用户名
     * @param password  密码
     * @return
     */
    boolean check(String userName, String password);
}
