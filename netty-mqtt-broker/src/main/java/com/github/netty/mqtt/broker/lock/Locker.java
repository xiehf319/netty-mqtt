package com.github.netty.mqtt.broker.lock;

import io.lettuce.core.internal.LettuceLists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.*;

/**
 * 类描述:
 * https://blog.csdn.net/long2010110/article/details/82911168
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/21 14:44
 */
@Component
@Slf4j
public class Locker {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final Long SUCCESS = 1L;

    private static final String LOCK_PREFIX = "redis:lock:";

    private static final int LOCK_EXPIRE = 300;

    public void lock(String key, Perform perform) {
        try {
            if (lock(key, "1", LOCK_EXPIRE)) {
                perform.perform();
            }
        } catch (Exception e) {
            log.error("加锁发生异常");
        } finally {
            release(key);
        }
    }

    private boolean lock(String key, String value, int expire) {
        try {
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 " +
                    "then " +
                    "if redis.call('get',KEYS[1])==ARGV[1] " +
                    "then " +
                    "return redis.call('expire',KEYS[1],ARGV[2]) " +
                    "else " +
                    "return 0 " +
                    "end " +
                    "end";
            List<String> args = Arrays.asList(value, String.valueOf(expire));
            Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
                Object nativeConnection = connection.getNativeConnection();
                if (nativeConnection instanceof JedisCluster) {
                    return (Long) ((JedisCluster) nativeConnection).eval(script, Collections.singletonList(LOCK_PREFIX + key), args);
                } else if (nativeConnection instanceof Jedis) {
                    return (Long) ((Jedis) nativeConnection).eval(script, Collections.singletonList(LOCK_PREFIX + key), args);
                }
                return null;
            });
            log.info("LOCK -- key: {} result: {}", key, result);
            if (SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            log.error("locker exception", e);
        }
        return false;
    }

    private void release(String key) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        List<String> args = LettuceLists.newList();
        Long result = redisTemplate.execute((RedisCallback<Long>) connection -> {
            Object nativeConnection = connection.getNativeConnection();
            if (nativeConnection instanceof JedisCluster) {
                return (Long) ((JedisCluster) nativeConnection).eval(script, Collections.singletonList(LOCK_PREFIX + key), args);
            } else if (nativeConnection instanceof Jedis) {
                return (Long) ((Jedis) nativeConnection).eval(script, Collections.singletonList(LOCK_PREFIX + key), args);
            }
            return null;
        });
        log.info("RELEASE -- key: {} result: {}", key, result);
    }
}
