package com.kamjin.toolkit.lock.redission.client;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.kamjin.toolkit.base.util.StringUtil.format;
import static com.kamjin.toolkit.base.util.StringUtil.concat;

/**
 * redisson工具类
 */
@Component
public class RedissonLockClient {

    private static final Logger log = LoggerFactory.getLogger(RedissonLockClient.class);

    private static RedissonClient redissonClient;

    @Autowired
    public void RedivoidssonService(RedissonClient redissonClient) {
        RedissonLockClient.redissonClient = redissonClient;
    }

    /**
     * 加锁
     *
     * @param lockName 锁名称
     * @param appName  模块名称
     * @param time     锁时间
     * @param values   拼接value
     * @return
     */
    public static Boolean acquire(String lockName, String appName, Long time, String... values) {
        try {
            RLock mylock = redissonClient.getLock(format(concat(false, appName, lockName), values));
            mylock.lock(time, TimeUnit.SECONDS); // 自定义锁定时间
        } catch (Exception e) {
            log.error("\n【分布式锁】获取锁失败，[lockName:{} appName:{} time:{} valuse:{}] \n【分布式锁】错误信息：e:{}", lockName, appName,
                    time, values, e);
            return false;
        }
        return true;
    }

    /**
     * 加锁
     *
     * @param lockName 锁名称
     * @param appName  模块名称
     * @param values   拼接value
     * @return
     */
    public static Boolean acquire(String lockName, String appName, String... values) {
        try {
            redissonClient.getLock(format(concat(false, appName, lockName), values)).lock();
        } catch (Exception e) {
            log.error("\n【分布式锁】获取锁失败，[lockName:{} appName:{} valuse:{}] \n【分布式锁】异常信息：e:{}", lockName, appName, values, e);
            return false;
        }
        return true;
    }

    /**
     * 解锁
     *
     * @param lockName
     * @param appName
     * @param values
     * @return
     */
    public static Boolean release(String lockName, String appName, String... values) {
        try {
            redissonClient.getLock(format(concat(false, appName, lockName), values)).unlock();
        } catch (Exception e) {
            log.error("\n【分布式锁】释放锁失败，[lockName:{} appName:{} valuse:{}] \n【分布式锁】异常信息：e:{}", lockName, appName, values, e);
            return false;
        }
        return true;
    }

    /**
     * 根据用户ID加资源锁
     *
     * @param fun      函数
     * @param lockName 锁名称
     * @param funName  方法名称
     * @param appName  app名称
     * @param sec      自动释放秒数 必传
     * @param values   values
     * @return T
     */
    public static <T> T lock(Supplier<T> fun, String funName, String lockName, String appName, Long sec,
                             String... values) {
        T result;
        long begin = System.currentTimeMillis();
        log.info("【分布式锁】[lockName:{} appName:{} funName:{}] 1.获取分布式锁开始，values：{}", lockName, appName, funName, values);
        if (RedissonLockClient.acquire(lockName, appName, sec, values)) {
            try {
                long step1 = System.currentTimeMillis();
                log.info("【分布式锁】[lockName:{} appName:{} funName:{}] 2.获取分布式锁成功，等待时间: 【time】：{}ms，values：{}", lockName, appName,
                        funName, step1 - begin, values);
                result = fun.get();
                long step2 = System.currentTimeMillis();
                log.info("【分布式锁】[lockName:{} appName:{} funName:{}] 3.方法执行完成，执行时间:【time】：{}ms，values：{}", lockName, appName,
                        funName, step2 - step1, values);
            } finally {
                RedissonLockClient.release(lockName, appName, values);
                long end = System.currentTimeMillis();
                log.info("【分布式锁】[lockName:{} appName:{} funName:{}] 4.finally释放锁，总时间: 【time】：{}ms，values：{}", lockName,
                        appName, funName, end - begin, values);
            }
        } else {
            throw new RuntimeException("资源操作失败，请再次重试");
        }
        return result;
    }

    /**
     * 根据用户ID加资源锁
     *
     * @param fun      函数
     * @param lockName 锁名称
     * @param funName  方法名称
     * @param appName  app名称
     * @param sec      自动释放秒数 必传
     * @param values   values
     */
    public static void lock(Runnable fun, String funName, String lockName, String appName, Long sec, String... values) {
        long begin = System.currentTimeMillis();
        log.info("【分布式锁】[lockName:{} appName:{} funName:{}] 1.获取分布式锁开始，values：{}", lockName, appName, funName, values);
        if (RedissonLockClient.acquire(lockName, appName, sec, values)) {
            try {

                long step1 = System.currentTimeMillis();
                log.info("【分布式锁】[lockName:{} appName:{} funName:{}] 2.获取分布式锁成功，等待时间: 【time】：{}ms，values：{}", lockName, appName,
                        funName, step1 - begin, values);
                fun.run();
                long step2 = System.currentTimeMillis();
                log.info("【分布式锁】[lockName:{} appName:{} funName:{}] 3.方法执行完成，执行时间:【time】：{}ms，values：{}", lockName, appName,
                        funName, step2 - step1, values);
            } finally {
                RedissonLockClient.release(lockName, appName, values);
                long end = System.currentTimeMillis();
                log.info("【分布式锁】[lockName:{} appName:{} funName:{}] 4.finally释放锁，总时间: 【time】：{}ms，values：{}", lockName,
                        appName, funName, end - begin, values);
            }
        } else {
            throw new RuntimeException("资源操作失败，请再次重试");
        }
    }
}
