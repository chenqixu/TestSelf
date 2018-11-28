package com.cqx.pool.ftp;

import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenqixu
 * @description 用来监控哪些借出不归还的
 * @date 2018/11/28 23:38
 */
public class FtpActivateClientMetrics {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(FtpActivateClientMetrics.class);

    private static ConcurrentHashMap<String, String> activateFtpConnect = new ConcurrentHashMap<>();

    public static void borrowMark(String borrower, NlFtpClient client) {
        String clientUUid = client.getClientUUid();
        String oldBorrower = activateFtpConnect.get(clientUUid);
        if (oldBorrower != null) // 如果已经存在这个UUID了
        {
            throw new RuntimeException("重复借出ftp连接！原借出者" + oldBorrower + " 新借出者" + borrower + "对象:" + client);
        }
        logger.info("由{}借出FTP对象 {}", borrower, client);
        activateFtpConnect.put(clientUUid, borrower);
    }

    public static void returnMark(String borrower, NlFtpClient client) {
        String clientUUid = client.getClientUUid();
        String oldBorrower = activateFtpConnect.remove(clientUUid);
        logger.info("由{}归还FTP对象 {}", borrower, client);
        if (oldBorrower == null) // 如果已经不存在存在这个UUID了
        {
            throw new RuntimeException("借出对象已被归还过了！原借出者" + oldBorrower + " 对象:" + client);
        }
    }

    public static void printStatus() {
        FtpClientPool pool = FtpClientPool.getInstance();
        logger.info("连接池总借出对象:{}", activateFtpConnect.size());
        logger.info("连接池借出对象明细:{}", activateFtpConnect);
        printPoolStatus(pool);
    }

    private static void printPoolStatus(FtpClientPool pool) {
        try {
            logger.info("连接池当前状态:BorrowedCount{},ReturnedCount{},DestroyedCount{},CreatedCount{},NumIdle{},NumActive{}", pool.getBorrowedCount(), pool.getReturnedCount(), pool.getDestroyedCount(),
                    pool.getCreatedCount(), pool.getNumIdle(), pool.getNumActive());
            logger.info("连接池当前FTP的连接状态:getNumActivePerKey{},getNumWaitersByKey{}", pool.getNumActivePerKey(), pool.getNumWaitersByKey());
        } catch (Exception e2) {
        }
    }
}
