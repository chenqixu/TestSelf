package com.cqx.localcache.rocksdb;

import com.cqx.common.utils.rocksdb.RocksDBUtil;
import com.cqx.common.utils.system.SleepUtil;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.TimerTask;

/**
 * RocksDBWork
 *
 * @author chenqixu
 */
public class RocksDBWork {

    private static final Logger logger = LoggerFactory.getLogger(RocksDBWork.class);

    private String dbFilePath;
    private String dbName;

    public RocksDBWork(String dbFilePath, String dbName) {
        this.dbFilePath = dbFilePath;
        this.dbName = dbName;
    }

    public Runnable getProducter() {
        return new Producter(dbFilePath, dbName);
    }

    public Runnable getConsumer() {
        return new Consumer(dbFilePath, dbName);
    }

    public TimerTask getTimerTask() {
        return new MyTimerTask(dbFilePath, dbName);
    }

    class Producter implements Runnable {

        private String dbFilePath;
        private String dbName;

        public Producter(String dbFilePath, String dbName) {
            this.dbFilePath = dbFilePath;
            this.dbName = dbName;
        }

        @Override
        public void run() {
            logger.info("start producter.");
            RocksDBUtil rocksDBUtil = null;
            Random random = new Random();
            try {
                rocksDBUtil = new RocksDBUtil(dbFilePath, dbName);
//                int cnt = 0;
//                while (cnt < 30) {
//                    cnt++;
//                    rocksDBUtil.putValue(String.valueOf(System.currentTimeMillis()) + random.nextInt(1000), "123456");
//                        SleepUtil.sleepMilliSecond(100);
                rocksDBUtil.putValue("460023805718803", "15080572052");
                rocksDBUtil.putValue("460023805718804", "15080585378");
                rocksDBUtil.putValue("460023805718805", "15080565979");
                rocksDBUtil.putValue("460023805718807", "15080556024");
                rocksDBUtil.putValue("460023805718808", "15080567869");
                rocksDBUtil.putValue("460023805718809", "15080576236");
                rocksDBUtil.putValue("460023805718811", "15080582583");
                rocksDBUtil.putValue("460023805718814", "15080591978");
                rocksDBUtil.putValue("460023805718815", "15080556930");
                rocksDBUtil.putValue("460023805718817", "15080557322");
//                }
                rocksDBUtil.flush();
            } catch (RocksDBException e) {
                e.printStackTrace();
            } finally {
                if (rocksDBUtil != null) rocksDBUtil.release();
            }
            logger.info("end producter.");
        }
    }

    class Consumer implements Runnable {

        private String dbFilePath;
        private String dbName;

        public Consumer(String dbFilePath, String dbName) {
            this.dbFilePath = dbFilePath;
            this.dbName = dbName;
        }

        @Override
        public void run() {
            logger.info("start consumer.");
            RocksDBUtil rocksDBUtil = null;
            try {
                rocksDBUtil = new RocksDBUtil(dbFilePath, dbName, true);
                int cnt = 0;
                while (cnt < 10) {
                    cnt++;
                    logger.info("getCount {}", rocksDBUtil.getCount());
                    SleepUtil.sleepMilliSecond(500);
                }
            } catch (RocksDBException e) {
                e.printStackTrace();
            } finally {
                if (rocksDBUtil != null) rocksDBUtil.release();
            }
            logger.info("end consumer.");
        }
    }

    class MyTimerTask extends TimerTask {

        private String dbFilePath;
        private String dbName;

        public MyTimerTask(String dbFilePath, String dbName) {
            this.dbFilePath = dbFilePath;
            this.dbName = dbName;
        }

        @Override
        public void run() {
            logger.info("start clear.");
            RocksDBUtil rocksDBUtil = null;
            try {
                rocksDBUtil = new RocksDBUtil(dbFilePath, dbName);
                rocksDBUtil.deleteFile();
            } catch (RocksDBException e) {
                e.printStackTrace();
            } finally {
                if (rocksDBUtil != null) rocksDBUtil.release();
            }
            logger.info("end clear.");
        }
    }
}