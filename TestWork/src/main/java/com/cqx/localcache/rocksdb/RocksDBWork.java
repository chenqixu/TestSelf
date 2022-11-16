package com.cqx.localcache.rocksdb;

import com.cqx.common.utils.localcache.rocksdb.RocksDBUtil;
import com.cqx.common.utils.system.SleepUtil;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * RocksDBWork
 *
 * @author chenqixu
 */
public class RocksDBWork {
    private static final Logger logger = LoggerFactory.getLogger(RocksDBWork.class);
    private String dbFilePath;

    public RocksDBWork(String dbFilePath) {
        this.dbFilePath = dbFilePath;
    }

    public Runnable getProducter() {
        return new Producter(dbFilePath);
    }

    public Runnable getConsumer() {
        return new Consumer(dbFilePath);
    }

    public TimerTask getTimerTask() {
        return new MyTimerTask(dbFilePath);
    }

    class Producter implements Runnable {
        private String dbFilePath;

        public Producter(String dbFilePath) {
            this.dbFilePath = dbFilePath;
        }

        @Override
        public void run() {
            logger.info("start producter.");
            RocksDBUtil rocksDBUtil = null;
//            Random random = new Random();
            try {
                rocksDBUtil = new RocksDBUtil(dbFilePath);
                int cnt = 0;
                int max = 100;
                String lastkey = rocksDBUtil.getLastKey();
                if (lastkey != null) {
                    cnt = Integer.valueOf(lastkey);
                    max += cnt;
                }
                logger.info("cnt：{}，max：{}", cnt, max);
                while (cnt < max) {
                    cnt++;
                    rocksDBUtil.putValue(String.valueOf(cnt), "123456");
                    SleepUtil.sleepMilliSecond(100);
//                rocksDBUtil.putValue("460023805718803", "15080572052");
//                rocksDBUtil.putValue("460023805718804", "15080585378");
//                rocksDBUtil.putValue("460023805718805", "15080565979");
//                rocksDBUtil.putValue("460023805718807", "15080556024");
//                rocksDBUtil.putValue("460023805718808", "15080567869");
//                rocksDBUtil.putValue("460023805718809", "15080576236");
//                rocksDBUtil.putValue("460023805718811", "15080582583");
//                rocksDBUtil.putValue("460023805718814", "15080591978");
//                rocksDBUtil.putValue("460023805718815", "15080556930");
//                rocksDBUtil.putValue("460023805718817", "15080557322");
                    if (cnt % 10 == 0) {
                        logger.info("producter getCount：{}", rocksDBUtil.getCount());
                    }
                }
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

        public Consumer(String dbFilePath) {
            this.dbFilePath = dbFilePath;
        }

        @Override
        public void run() {
            logger.info("start consumer.");
            RocksDBUtil rocksDBUtil = null;
            try {
                rocksDBUtil = new RocksDBUtil(dbFilePath, true);
                int cnt = 0;
                while (cnt < 250) {
                    cnt++;
                    SleepUtil.sleepMilliSecond(100);
                    if (cnt % 50 == 0) {
                        rocksDBUtil = new RocksDBUtil(dbFilePath, true);
                        logger.info("Consumer getCount：{}", rocksDBUtil.getCount());
                    }
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

        public MyTimerTask(String dbFilePath) {
            this.dbFilePath = dbFilePath;
        }

        @Override
        public void run() {
            logger.info("start clear.");
            RocksDBUtil rocksDBUtil = null;
            try {
                rocksDBUtil = new RocksDBUtil(dbFilePath);
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
