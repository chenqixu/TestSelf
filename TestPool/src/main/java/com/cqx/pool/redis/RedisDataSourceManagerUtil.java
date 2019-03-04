package com.cqx.pool.redis;

import com.cqx.redis.bean.RedisCfg;
import com.cqx.redis.jdbc.RedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * RedisDataSourceManagerUtil
 *
 * @author chenqixu
 */
public class RedisDataSourceManagerUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisDataSourceManagerUtil.class);
    private RedisConnection redisConnection;
    private RedisCfg redisCfg;
    private String borrower;

    /**
     * 获取一个redis连接
     *
     * @param redisCfg
     * @throws Exception
     */
    private RedisDataSourceManagerUtil(RedisCfg redisCfg) throws Exception {
        this(redisCfg, "default.Thread." + Thread.currentThread().getName());
    }

    /**
     * 获取一个redis连接
     *
     * @param redisCfg
     * @param borrower 借出者的标识。比如类名+方法名，用来跟踪连接线程被谁借出不还
     * @throws Exception
     */
    private RedisDataSourceManagerUtil(RedisCfg redisCfg, String borrower) throws Exception {
        this.redisCfg = redisCfg;
        this.borrower = borrower;
        RedisClientPool pool = RedisClientPool.getInstance();
        try {
            redisConnection = pool.borrowObject(redisCfg, RedisClientFactory.CONNECT_TIME_WAIT); // 借取一个消息
            RedisActivateClientMetrics.borrowMark(borrower, redisConnection);
            logger.info("获取新连接,clientUUid {}，线程池当前活跃active数:NumActive{} {}", redisConnection.toString(), pool.getNumActive(redisCfg));
        } catch (Exception e) {
            RedisActivateClientMetrics.printStatus();
            throw new IOException("无法获取redis连接.连接配置:" + redisCfg, e);
        }
    }

    /**
     * 关闭redis连接
     */
    public void close() {
        if (redisConnection == null) {
            logger.warn("redis连接对象{}已经归还过了", redisCfg);
        }
        try {
            logger.info("归还redis连接对象{}，当前key:{}", redisConnection.toString(), redisCfg);
            RedisClientPool.getInstance().returnObject(redisCfg, redisConnection);
        } catch (Exception e) {
            RedisActivateClientMetrics.printStatus();
            logger.warn("归还redis连接对象{}出现{}异常，当前key:{}", redisConnection.toString(), e.getMessage(), redisCfg, e);
            try {
                if (redisConnection != null) {
                    redisConnection.close();
                }
            } catch (Exception e1) {
                logger.warn("释放redis连接对象发生异常，当前key:" + redisCfg, e);
            }
        } finally {
            RedisActivateClientMetrics.returnMark(borrower, redisConnection);
        }
        redisConnection = null;
    }

    /**
     * 强制释放，清除指定的子池，删除所有池实例
     */
    public boolean clear() {
        logger.info("强制释放当前sub-pool，当前key：{}", redisCfg);
        try {
            RedisClientPool.getInstance().clear(redisCfg);
            return true;
        } catch (Exception e) {
            logger.error("强制释放当前sub-pool异常，当前key：" + redisCfg + "，错误信息：" + e.getMessage(), e);
        }
        return false;
    }

    public static RedisConnection getRedisConnection(RedisCfg redisCfg) throws Exception {
        return new RedisDataSourceManagerUtil(redisCfg).redisConnection;
    }
}
