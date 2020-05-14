package com.github.netty.mqtt.broker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService implements IAuthService {

    /**
     * 校验用户名密码是否正确
     *
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    public boolean check(String userName, String password) {
        log.info("username: {}  password: {}", userName, password);
        return true;
    }
}
