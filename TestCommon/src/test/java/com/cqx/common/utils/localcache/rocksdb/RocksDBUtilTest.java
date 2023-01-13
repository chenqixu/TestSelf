package com.cqx.common.utils.localcache.rocksdb;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.system.SleepUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RocksDBUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(RocksDBUtilTest.class);
    private RocksDBUtil rocksDBUtil;
    private String dbFilePath = "d:\\tmp\\data\\rocksdb\\monitor";

    @Before
    public void setUp() throws Exception {
        rocksDBUtil = new RocksDBUtil(dbFilePath);
        logger.info("HasColumnFamily : {}", rocksDBUtil.isHasColumnFamily());
    }

    @After
    public void tearDown() throws Exception {
        if (rocksDBUtil != null) rocksDBUtil.release();
    }

    @Test
    public void printAllValue() {
        assert rocksDBUtil != null;
        // 打印默认列族的所有值
        rocksDBUtil.printAllValue();
    }

    @Test
    public void putValue() throws Exception {
        assert rocksDBUtil != null;
        String key = "13500000001";
        rocksDBUtil.putValue(key, "123");
        logger.info("put...");
        printAllValue();
        rocksDBUtil.delete(key);
        logger.info("delete...");
        printAllValue();
    }

    @Test
    public void mergeTest() throws RocksDBException, UnsupportedEncodingException {
        // 使用列族构造的情况下不支持merge操作
        assert rocksDBUtil != null;
        String key = "13500000001";
        rocksDBUtil.merge(key, "-456");
        rocksDBUtil.merge(key, "-789");
        logger.info("merge: {}", rocksDBUtil.getValue(key));
    }

    @Test
    public void printAllColumnFamily() throws RocksDBException {
        // 打印所有的列族
        for (String _cf : RocksDBUtil.listColumnFamilies(dbFilePath)) {
            logger.info("列族: {}", _cf);
        }
    }

    @Test
    public void printColumnFamilyAllValue() throws RocksDBException {
        assert rocksDBUtil != null;
        // 打印所有的列族
        printAllColumnFamily();
        // 打印所有列族的所有值
        rocksDBUtil.printAllColumnFamilyAllValue();
    }

    @Test
    public void dropColumnFamily() throws RocksDBException {
        assert rocksDBUtil != null;
        String cf = "20221113";
        // 删掉cf列族
        rocksDBUtil.dropColumnFamily(cf);
        // 打印所有列族
        // 打印所有列族的所有值
        printColumnFamilyAllValue();
    }

    @Test
    public void dropAllColumnFamily() throws RocksDBException {
        // 删除所有的列族
        rocksDBUtil.dropAllColumnFamily();
        // 打印所有列族
        // 打印所有列族的所有值
        printColumnFamilyAllValue();
    }

    @Test
    public void putColumnFamilyValue() throws RocksDBException {
        assert rocksDBUtil != null;
        String cf1 = "20221111";
        String cf2 = "20221112";
        // 创建cf1列族
        rocksDBUtil.createColumnFamilyHandle(cf1);
        // 创建cf2列族
        rocksDBUtil.createColumnFamilyHandle(cf2);
        // 往cf1，cf2列族写入数据
        rocksDBUtil.putColumnFamilyValue(cf1, "t1", "t123");
        rocksDBUtil.putColumnFamilyValue(cf1, "t2", "t234");
        rocksDBUtil.putColumnFamilyValue(cf2, "t3", "t345");
        // 打印所有列族
        // 打印所有列族的所有值
        printColumnFamilyAllValue();
    }

    @Test
    public void createColumnFamilyHandle() throws RocksDBException {
        assert rocksDBUtil != null;
        String cf3 = "20221113";
        // 创建cf3列族
        rocksDBUtil.createColumnFamilyHandle(cf3);
        // 往cf3列族写入数据
        rocksDBUtil.putColumnFamilyValue(cf3, "t4", "t456");
        // 打印所有列族
        // 打印所有列族的所有值
        printColumnFamilyAllValue();
    }

    @Test
    public void checkColumnFamily() throws RocksDBException {
        assert rocksDBUtil != null;
        String cf4 = "20221114";
        Object obj = null;
        try {
            // 列族校验
            obj = rocksDBUtil.checkColumnFamily(cf4);
        } catch (RocksDBException e) {
            logger.warn(e.getMessage(), e);
            // 创建列族
            rocksDBUtil.createColumnFamilyHandle(cf4);
            // 列族校验
            obj = rocksDBUtil.checkColumnFamily(cf4);
        }
        if (obj != null) {
            rocksDBUtil.putColumnFamilyValue(cf4, "cf4", "cf4-123456");
            // 打印所有列族
            // 打印所有列族的所有值
            printColumnFamilyAllValue();
        }
    }

    @Test
    public void sort() {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        list.add(1);
        list.add(4);
        list.add(5);
        list.add(3);
        Collections.sort(list);
        logger.info("顺序，从小到到，最大={}", list.get(list.size() - 1));
        for (int i : list) {
            logger.info("{}", i);
        }
        logger.info("倒序，从大到小");
        Collections.reverse(list);
        for (int i : list) {
            logger.info("{}", i);
        }
    }

    /**
     * 切换数据库到sdtp路径下
     *
     * @throws Exception
     */
    private void changeSdtpDB() throws Exception {
        tearDown();
        dbFilePath = "d:\\tmp\\data\\rocksdb\\sdtpclient";
        setUp();
    }

    @Test
    public void printSdtpClientMonitorData() throws Exception {
        changeSdtpDB();
        printColumnFamilyAllValue();
    }

    @Test
    public void dropSdtpClientAllColumnFamily() throws Exception {
        changeSdtpDB();
        assert rocksDBUtil != null;
        // 删除所有的列族
        rocksDBUtil.dropAllColumnFamily();
        // 打印所有列族
        // 打印所有列族的所有值
        printColumnFamilyAllValue();
    }

    @Test
    public void deleteSdtpClientFile() throws Exception {
        changeSdtpDB();
        assert rocksDBUtil != null;
        // 删除文件
        rocksDBUtil.deleteFile();
    }

    @Test
    public void buildSdtpClientMonitorDataBatch() throws Exception {
        changeSdtpDB();
        assert rocksDBUtil != null;
        // 创建列族
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyyMMdd");
        String sum_date = sdfDay.format(new Date());
        rocksDBUtil.createColumnFamilyHandle(sum_date);
        // 写入测试数据
        long currentTimeMillis = System.currentTimeMillis();
        // 间隔15 * 1000秒，次数20次
        Random random = new Random();
        for (long start = currentTimeMillis - 15 * 1000 * 20; start < currentTimeMillis; start += 15 * 1000) {
            Map<String, String> map = new HashMap<>();
            map.put("scan_cnt", String.valueOf(random.nextInt(100)));
            map.put("scan_add_queue_cnt", String.valueOf(random.nextInt(100)));
            rocksDBUtil.putColumnFamilyValue(sum_date, String.valueOf(start), JSON.toJSONString(map));
        }
    }

    @Test
    public void buildSdtpClientMonitorDataRealTime() throws Exception {
        changeSdtpDB();
        assert rocksDBUtil != null;
        // 创建列族
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyyMMdd");
        String sum_date = sdfDay.format(new Date());
        rocksDBUtil.createColumnFamilyHandle(sum_date);
        // 写入测试数据
        long currentTimeMillis = System.currentTimeMillis();
        // 间隔15 * 1000秒，次数20次
        Random random = new Random();
        for (long start = currentTimeMillis - 15 * 1000 * 20; start < currentTimeMillis; start += 15 * 1000) {
            // 每5秒写入一次
            SleepUtil.sleepSecond(5);
            logger.info("写入: {}", start);
            Map<String, String> map = new HashMap<>();
            map.put("scan_cnt", String.valueOf(random.nextInt(100)));
            map.put("scan_add_queue_cnt", String.valueOf(random.nextInt(100)));
            rocksDBUtil.putColumnFamilyValue(sum_date, String.valueOf(start), JSON.toJSONString(map));
        }
    }

    @Test
    public void dropSdtpClientColumnFamily() throws Exception {
        changeSdtpDB();
        assert rocksDBUtil != null;
        String cf = "20221116";
        // 删掉cf列族
        rocksDBUtil.dropColumnFamily(cf);
        // 打印所有列族
        // 打印所有列族的所有值
        printColumnFamilyAllValue();
    }

    /**
     * 切换数据库到sdtp manager路径下
     *
     * @throws Exception
     */
    private void changeSdtpManagerDB() throws Exception {
        tearDown();
        dbFilePath = "d:\\tmp\\data\\rocksdb\\sdtpmanager";
        setUp();
    }

    @Test
    public void printSdtpManagerData() throws Exception {
        changeSdtpManagerDB();
        printColumnFamilyAllValue();
    }

    @Test
    public void dropSdtpManagerAllColumnFamily() throws Exception {
        changeSdtpManagerDB();
        assert rocksDBUtil != null;
        // 删除所有的列族
        rocksDBUtil.dropAllColumnFamily();
        // 打印所有列族
        // 打印所有列族的所有值
        printColumnFamilyAllValue();
    }

    @Test
    public void dropSdtpManagerColumnFamily() throws Exception {
        changeSdtpManagerDB();
        assert rocksDBUtil != null;
        // 删掉cf列族
        rocksDBUtil.dropColumnFamily("20221124");
        // 打印所有列族
        // 打印所有列族的所有值
        printColumnFamilyAllValue();
    }

    /**
     * 构造类型、主机
     *
     * @throws Exception
     */
    @Test
    public void buildSdtpClientType() throws Exception {
        changeSdtpManagerDB();
        assert rocksDBUtil != null;
        // 往默认列族写入数据
        // 类型
//        rocksDBUtil.putValue("type", "MC");
        // 主机
        rocksDBUtil.putValue("host_MC", "192.168.1.51:19090,192.168.1.52:19090");
        // 删除
        rocksDBUtil.delete("fetch_MC_127.0.0.1:19090");
        rocksDBUtil.delete("fetch_MC_127.0.0.2:19090");
        // 删掉列族
        rocksDBUtil.dropColumnFamily("20221124");
        // 打印所有列族
        // 打印所有列族的所有值
        printColumnFamilyAllValue();
        // 获取MC监控最后一条记录
//        String lastValue = rocksDBUtil.getColumnFamilyLastValue("20221121", "monitor_MC_127.0.0.1:19090");
//        logger.info("lastValue: {}", lastValue);
    }
}