package com.github.netty.mqtt.broker.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 类描述:
 *  https://blog.csdn.net/long2010110/article/details/82911168
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 14:44
 */
@Component
@Slf4j
public class Locker {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void lock(String key, Perform perform) {
        try {
            if (lock(key)) {
                perform.perform();
            }
        } catch (Exception e) {
            log.error("加锁发生异常");
        } finally {
            delete(key);
        }
    }

    private static final String LOCK_PREFIX = "redis:lock:";
    private static final int LOCK_EXPIRE = 300;
    private boolean lock(String key) {
        String lock = LOCK_PREFIX + key;
        return (Boolean) redisTemplate.execute((RedisCallback) connection -> {
            long expireAt = System.currentTimeMillis() + LOCK_EXPIRE;
            Boolean acquire = connection.setNX(lock.getBytes(), String.valueOf(expireAt).getBytes());
            if (acquire) {
                return true;
            } else {
                byte[] value = connection.get(lock.getBytes());
                if (Objects.nonNull(value) && value.length > 0) {
                    long expireTime = Long.parseLong(new String(value));
                    // 如果锁已经过期
                    if (expireTime < System.currentTimeMillis()) {
                        // 重新加锁
                        byte[] oldValue = connection.getSet(lock.getBytes(), String.valueOf(System.currentTimeMillis() + LOCK_EXPIRE).getBytes());
                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }
            }
            return false;
        });
    }

    private void delete(String key) {
        redisTemplate.delete(key);
    }
}
