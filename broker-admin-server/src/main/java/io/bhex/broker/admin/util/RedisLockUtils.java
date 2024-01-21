package io.bhex.broker.admin.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisLockUtils {

    private static final long TRY_TIME = 100;

    /**
     * 一直尝试拿锁
     *
     * @param redisTemplate
     * @param key
     * @param lockExpireTime 毫秒
     * @param tryTimes       次数
     * @return
     */
    public static boolean tryLockAlways(RedisTemplate redisTemplate, String key, long lockExpireTime, long tryTimes) {
        long startTime = System.currentTimeMillis();
        int count = 0;
        while (true) {
            // 每次尝试是100毫秒，如果一直拿不到， 过了时间就结束得了
            if (count++ > tryTimes) {
                return false;
            }

            boolean lock = tryLock(redisTemplate, key, lockExpireTime);
            if (lock) {
                return true;
            }

            try {
                Thread.sleep(TRY_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 尝试拿锁
     *
     * @param redisTemplate
     * @param key
     * @param lockExpireTime 毫秒
     * @return
     */
    public static boolean tryLock(RedisTemplate redisTemplate, String key, long lockExpireTime) {
        try {
            long expireAt = System.currentTimeMillis() + lockExpireTime;
            boolean lock = redisTemplate.opsForValue().setIfAbsent(key, expireAt + "", lockExpireTime, TimeUnit.MILLISECONDS);
            if (lock) {
                return true;
            }
        } catch (Exception e) {
            log.error(" getLock exception:{}", key, e);
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param redisTemplate
     * @param key
     */
    public static void releaseLock(RedisTemplate redisTemplate, String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error(" releaseLock exception:{}", key, e);
        }
    }

}
