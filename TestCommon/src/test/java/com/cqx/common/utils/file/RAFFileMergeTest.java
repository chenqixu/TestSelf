package com.cqx.common.utils.file;

import com.cqx.common.bean.model.DataBean;
import com.cqx.common.bean.model.IDataFilterBean;
import com.cqx.common.utils.Utils;
import com.cqx.common.utils.jdbc.QueryResultETL;
import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.impl.KryoSerializationImpl;
import com.cqx.common.utils.serialize.impl.ProtoStuffSerializationImpl;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeUtil;
import com.cqx.common.utils.thread.BaseRunable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;

public class RAFFileMergeTest {
    private static final Logger logger = LoggerFactory.getLogger(RAFFileMergeTest.class);
    private RAFFileMerge<DataBean> rafFileMergeRead;
    private RAFFileMerge<DataBean> rafFileMergeWrite;
    private String raf_path;
    private String read_name;
    private String write_name;
    private ISerialization<DataBean> iSerialization =
//            new ProtoStuffSerializationImpl();
            new KryoSerializationImpl();

    @Before
    public void setUp() throws Exception {
        iSerialization.setTClass(DataBean.class);
        raf_path = System.getProperty("raf.path");
        read_name = System.getProperty("read.name");
        write_name = System.getProperty("write.name");
        if (raf_path != null && read_name != null) {
            rafFileMergeRead = new RAFFileMerge<>(iSerialization, raf_path, read_name, 200, true);
        }
        if (raf_path != null && write_name != null) {
            rafFileMergeWrite = new RAFFileMerge<>(iSerialization, raf_path, write_name, 1073741824);
        }
        logger.warn("raf_path：{}，read_name：{}，write_name：{}", raf_path, read_name, write_name);
    }

    @After
    public void tearDown() throws Exception {
        if (rafFileMergeRead != null) rafFileMergeRead.close();
        if (rafFileMergeWrite != null) rafFileMergeWrite.close();
    }

    @Test
    public void merge() throws Exception {
        Thread read = new Thread(new Runnable() {
            @Override
            public void run() {
                int read_num = 0;
                while (read_num < 100) {
                    try {
                        List<DataBean> contents = rafFileMergeWrite.read(10L);
                        logger.info("====read，size：{}，contents：{}", contents.size(), contents);
                        read_num++;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        read.start();
        int all_ret = 0;
        for (int j = 0; j < 10; j++) {
            try (RAFFileMangerCenter<DataBean> raf = new RAFFileMangerCenter<>(
                    iSerialization, raf_path + "sm" + j)) {
                for (int i = 0; i < 10; i++) {
                    int ret = raf.write(generator());
                    logger.info("j：{}，i：{}，写入结果：{}", j, i, ret);
                    all_ret += ret;
                }
                raf.writeEndTag();
                rafFileMergeWrite.merge(raf, true);
            }
        }
        SleepUtil.sleepSecond(3);
        logger.info("all_ret：{}", all_ret);
    }

    @Test
    public void readEndTag() throws Exception {
        try (RAFFileMangerCenter raf = new RAFFileMangerCenter(raf_path + "sm")) {
            for (int i = 0; i < 10; i++) {
                String msg = raf.read();
                if (RAFFileMangerCenter.END_TAG.equals(msg)) {
                    raf.seekToEndTag();
                }
            }
        }
    }

    @Test
    public void readKryo() throws Exception {
        try (RAFFileMangerCenter<DataBean> raf = new RAFFileMangerCenter<>(iSerialization,
                raf_path + "sm0")) {
            int num = 0;
            RAFBean rafBean;
            while ((rafBean = raf.readDeserialize()) != null) {
                if (rafBean.isEnd()) {
                    logger.info("is end.");
                    break;
                } else {
//                    logger.info("read：{}", rafBean.getT());
                    num++;
//                    if (num > 5) break;
                }
            }
            logger.info("num：{}", num);
        }
    }

    @Test
    public void writeKryo() throws Exception {
        try (RAFFileMangerCenter raf = new RAFFileMangerCenter<>(iSerialization,
                raf_path + "sm0")) {
            for (int i = 0; i < 1; i++) {
                IDataFilterBean dataBean = generator();
                raf.write(dataBean);
            }
            raf.writeEndTag();
        }
    }

    @Test
    public void atoTest() throws Exception {
        AtomicBoolean ato = new AtomicBoolean(true);
        // 线程1锁，休眠
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                if (ato.getAndSet(false)) {
                    logger.info("t1 get.");
                    SleepUtil.sleepMilliSecond(500);
                    logger.info("t1 release.");
                    ato.set(true);
                } else {
                    logger.info("t1 not get");
                }
            }
        });
        // 试试线程2会不会等待
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                SleepUtil.sleepMilliSecond(200);
                while (!ato.getAndSet(false)) {
                    //
                }
                logger.info("t2 get and release.");
                ato.set(true);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    private List<DataBean> poll(RAFFileMerge read, long timeOut) throws IOException {
        List<byte[]> tmps = read != null ? read.read(timeOut) : null;
        if (tmps != null && tmps.size() > 0) {
            List<DataBean> results = new ArrayList<>();
            for (byte[] tmp : tmps) results.add(iSerialization.deserialize(tmp));
            return results;
        }
        return null;
    }

    private List<DataBean> pollString(RAFFileMerge read, long timeOut) {
        List<byte[]> tmps = read != null ? read.read(timeOut) : null;
        if (tmps != null && tmps.size() > 0) {
            List<DataBean> results = new ArrayList<>();
            for (byte[] tmp : tmps) results.add(DataBean.jsonToBean(new String(tmp)));
            return results;
        }
        return null;
    }

    @Test
    public void readTest() throws Exception {
        BaseRunable br = new BaseRunable() {
            @Override
            public void exec() throws Exception {
                try {
                    List<DataBean> contents = poll(rafFileMergeWrite, 5L);
                    if (contents != null) {
//                        for (DataBean dataBean : contents) {
//                            for (QueryResultETL queryResultETL : dataBean.getQueryResults()) {
//                                if ("insert_time".equals(queryResultETL.getColumnName())) {
//                                    logger.info("merge_{}", queryResultETL.getValue());
//                                }
//                            }
//                        }
                        logger.info("====read：{}", contents.size());
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };
        Thread read = new Thread(br);
//        read.start();

        for (int i = 0; i < 100; i++) {
            List<DataBean> contents = pollString(rafFileMergeRead, 5L);
//            logger.info("====read：{}", contents != null ? contents.get(0) : null);
            logger.info("sm{}：{}", i, contents != null ? contents.size() : 0);
            if (contents != null)
                try (RAFFileMangerCenter raf = new RAFFileMangerCenter(raf_path + "sm" + i)) {
                    for (DataBean dataBean : contents) {
//                        for (QueryResultETL queryResultETL : dataBean.getQueryResults()) {
//                            if ("insert_time".equals(queryResultETL.getColumnName())) {
//                                logger.info("sm_{}", queryResultETL.getValue());
//                            }
//                        }
//                        raf.write(dataBean.toJson());
                        byte[] c = iSerialization.serialize(dataBean);
                        logger.info("kryo：{}，json：{}", c.length, dataBean.toJson().length());
                        raf.write(c);
                    }
                    rafFileMergeWrite.merge(raf, true);
                }
        }

        SleepUtil.sleepSecond(3);
        br.stop();
        read.join();
    }

    private DataBean generator() {
        try {
            long current = System.currentTimeMillis() - (10 * 1000L);
            Random random = new Random();
            int randomMicro = random.nextInt(999);
            String randomMicroStr;
            if (randomMicro < 10) randomMicroStr = "00" + randomMicro;
            else if (randomMicro < 100) randomMicroStr = "0" + randomMicro;
            else randomMicroStr = randomMicro + "";
            String newTime = TimeUtil.formatTime(current + (1 * 1000L), "yyyy-MM-dd'T'HH:mm:ss.SSS")
                    + randomMicroStr;
            List<QueryResultETL> queryResults = new ArrayList<>();
            QueryResultETL queryResult = new QueryResultETL();
            queryResult.setValue(new Timestamp(new Date().getTime()));
            queryResults.add(queryResult);
            return new DataBean("i", newTime, queryResults);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Test
    public void testByte() {
        byte[] end_byte = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        byte[] end_byte1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        logger.info("end_byte[0]：{}", end_byte[0]);
        // crc32是8位16进制
        CRC32 crc32 = new CRC32();
        crc32.update(end_byte1);
        byte[] end_crc32_b1 = Utils.longToBytes(crc32.getValue());
        long b1_l = Utils.bytesToLong(end_crc32_b1);
        logger.info("crc32：{}，16bit：{}，long.byte.len：{}，toLong：{}",
                crc32.getValue(), Long.toHexString(crc32.getValue()), end_crc32_b1.length, b1_l);
        crc32.reset();
        crc32.update(end_byte);
        byte[] end_crc32_b = Utils.longToBytes(crc32.getValue());
        long b_l = Utils.bytesToLong(end_crc32_b);
        logger.info("crc32：{}，16bit：{}，long.byte.len：{}，toLong：{}",
                crc32.getValue(), Long.toHexString(crc32.getValue()), end_crc32_b.length, b_l);

        logger.info("bytes eq：{}", Arrays.equals(end_byte, end_byte1));
    }
}