package com.cqx.localcache.rocksdb;

import com.cqx.common.utils.localcache.rocksdb.RocksDBUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocksDBWorkTest {

    private static final Logger logger = LoggerFactory.getLogger(RocksDBWorkTest.class);
    private RocksDBWork rocksDBWork;
    private RocksDBUtil rocksDBUtil;
    private String isThread;

    @Before
    public void setUp() throws Exception {
        String dbFilePath = "d:\\tmp\\data\\rocksdb\\";
        String dbName = "1";
        isThread = System.getenv("isThread");
        logger.info("isThread {}", isThread);
        //初始化工具
        if (isThread == null || !isThread.equals("1")) rocksDBUtil = new RocksDBUtil(dbFilePath, dbName);
        //初始化RocksDBWork
        rocksDBWork = new RocksDBWork(dbFilePath, dbName);
    }

    @After
    public void tearDown() throws Exception {
        if (rocksDBUtil != null) rocksDBUtil.release();
    }

    @Test
    public void printAllValue() throws Exception {
        assert rocksDBUtil != null;
        rocksDBUtil.printAllValue();
    }

    @Test
    public void getValue() throws Exception {
        assert rocksDBUtil != null;
        logger.info("value : {}", rocksDBUtil.getValue("13500000000"));
    }

    @Test
    public void putValue() throws Exception {
        assert rocksDBUtil != null;
        String key = "13500000000" + "1";
//        rocksDBUtil.putValue(key, "123");
        rocksDBUtil.delete(key);
        logger.info("keyMayExist {}", rocksDBUtil.keyMayExist(key));
    }

    @Test
    public void delete() throws Exception {
        assert rocksDBUtil != null;
        rocksDBUtil.delete("13500000000");
    }

    @Test
    public void deleteFile() throws Exception {
        assert rocksDBUtil != null;
        rocksDBUtil.deleteFile();
    }

    @Test
    public void threadTest() throws Exception {
//        Timer timer = new Timer(true);
//        timer.schedule(rocksDBWork.getTimerTask(), 2000);
        Thread producter = new Thread(rocksDBWork.getProducter());
        Thread consumer1 = new Thread(rocksDBWork.getConsumer());
//        Thread consumer2 = new Thread(rocksDBWork.getConsumer());
        producter.start();
        producter.join();
        consumer1.start();
        consumer1.join();
//        consumer2.start();
//        consumer2.join();
    }

    @Test
    public void startProducter() throws Exception {
        if (isThread.equals("1")) {
            Thread producter = new Thread(rocksDBWork.getProducter());
            producter.start();
            producter.join();
        }
    }

    @Test
    public void startConsumer() throws Exception {
        if (isThread.equals("1")) {
            Thread consumer1 = new Thread(rocksDBWork.getConsumer());
            consumer1.start();
            consumer1.join();
        }
    }
}