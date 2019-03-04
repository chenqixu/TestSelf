package com.cqx.pool.redis;

import com.cqx.redis.jdbc.RedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用来监控哪些借出不归还的
 *
 * @author chenqixu
 */
public class RedisActivateClientMetrics {

    private static final Logger logger = LoggerFactory.getLogger(RedisActivateClientMetrics.class);
    private static ConcurrentHashMap<String, String> activateRedisConnect = new ConcurrentHashMap<>();

    public static void borrowMark(String borrower, RedisConnection client) {
        String clientUUid = client.toString();
        String oldBorrower = activateRedisConnect.get(clientUUid);
        if (oldBorrower != null) // 如果已经存在这个UUID了
        {
            throw new RuntimeException("重复借出ftp连接！原借出者" + oldBorrower + " 新借出者" + borrower + "对象:" + client);
        }
        logger.info("由{}借出redis对象 {}", borrower, client);
        activateRedisConnect.put(clientUUid, borrower);
    }

    public static void returnMark(String borrower, RedisConnection client) {
        String clientUUid = client.toString();
        String oldBorrower = activateRedisConnect.remove(clientUUid);
        logger.info("由{}归还redis对象 {}", borrower, client);
        if (oldBorrower == null) // 如果已经不存在存在这个UUID了
        {
            throw new RuntimeException("借出对象已被归还过了！原借出者" + oldBorrower + " 对象:" + client);
        }
    }

    public static void printStatus() {
        RedisClientPool pool = RedisClientPool.getInstance();
        logger.info("连接池总借出对象:{}", activateRedisConnect.size());
        logger.info("连接池借出对象明细:{}", activateRedisConnect);
        printPoolStatus(pool);
    }

    private static void printPoolStatus(RedisClientPool pool) {
        try {
            logger.info("连接池当前状态:BorrowedCount{},ReturnedCount{},DestroyedCount{},CreatedCount{},NumIdle{},NumActive{}",
                    pool.getBorrowedCount(), pool.getReturnedCount(), pool.getDestroyedCount(),
                    pool.getCreatedCount(), pool.getNumIdle(), pool.getNumActive());
            logger.info("连接池当前redis的连接状态:getNumActivePerKey{},getNumWaitersByKey{}",
                    pool.getNumActivePerKey(), pool.getNumWaitersByKey());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
