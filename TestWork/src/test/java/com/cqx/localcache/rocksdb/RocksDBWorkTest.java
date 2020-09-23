package com.cqx.localcache.rocksdb;

import com.cqx.common.utils.rocksdb.RocksDBUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class RocksDBWorkTest {

    private static final Logger logger = LoggerFactory.getLogger(RocksDBWorkTest.class);
    private RocksDBWork rocksDBWork;
    private RocksDBUtil rocksDBUtil;

    @Before
    public void setUp() throws Exception {
        String dbFilePath = "d:\\tmp\\data\\rocksdb\\";
        String dbName = "1";
        String isThread = System.getenv("isThread");
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
        rocksDBUtil.putValue("13500000000", "123");
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
        Timer timer = new Timer(true);
//        timer.schedule(rocksDBWork.getTimerTask(), 2000);
        Thread producter = new Thread(rocksDBWork.getProducter());
        Thread consumer1 = new Thread(rocksDBWork.getConsumer());
        Thread consumer2 = new Thread(rocksDBWork.getConsumer());
        producter.start();
        producter.join();
        consumer1.start();
        consumer1.join();
//        consumer2.start();
//        consumer2.join();
    }
}