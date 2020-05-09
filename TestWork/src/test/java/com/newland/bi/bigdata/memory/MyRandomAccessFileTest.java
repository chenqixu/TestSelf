package com.newland.bi.bigdata.memory;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.cqx.common.utils.string.StringUtil;
import com.cqx.common.utils.system.SleepUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

public class MyRandomAccessFileTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(MyRandomAccessFileTest.class);
    private static final byte[] NULL_BYTE = new byte[3];
    private static final String NULL_VALUE = new String(NULL_BYTE);
    private MyRandomAccessFile myRandomAccessFile;

    @Before
    public void setUp() throws Exception {
        String sm = "d:\\tmp\\data\\mccdr\\r.sm";
        FileUtil.del(sm);
        myRandomAccessFile = new MyRandomAccessFile(sm);
        myRandomAccessFile.setLock(true);
    }

    @After
    public void tearDown() throws Exception {
        if (myRandomAccessFile != null) myRandomAccessFile.close();
    }

    @Test
    public void write() throws IOException {
//        myRandomAccessFile.write("450009493554750");
//        myRandomAccessFile.write("450009493554771");
//        myRandomAccessFile.write(0L, "450009493554750".getBytes());
        myRandomAccessFile.write(15L, "450009493554771".getBytes());
    }

    @Test
    public void read() throws IOException {
        int len = 15;
        byte[] null_byte = new byte[15];
        String NULL_VALUE = new String(null_byte);
        logger.info("NULL_VALUE：{}", NULL_VALUE);
        for (int i = 0; i < 2; i++) {
            int off = i * len;
            String msg = myRandomAccessFile.read(off, len);
            if (!msg.equals(NULL_VALUE))
                logger.info("{}，{}", msg, msg.length());
        }
    }

    @Test
    public void threadTest() throws InterruptedException {
        Thread t1 = new Thread() {
            public void run() {
                int cnt = 0;
                while (cnt < 1000) {
                    try {
                        String msg = myRandomAccessFile.read(0, 3);
                        if (msg.equals(NULL_VALUE)) {
                            String write_msg = "123";
                            if (myRandomAccessFile.write(0, write_msg))
                                logger.info("{} write", write_msg);
                        } else {
                            logger.info("{} read!", msg);
                            break;
                        }
                    } catch (IOException e) {
                        logger.warn(e.getMessage());
                    }
                    cnt++;
                }
            }
        };
        Thread t2 = new Thread() {
            public void run() {
                int cnt = 0;
                while (cnt < 1000) {
                    try {
                        String msg = myRandomAccessFile.read(0, 3);
                        if (msg.equals(NULL_VALUE)) {
                            String write_msg = "456";
                            if (myRandomAccessFile.write(0, write_msg))
                                logger.info("{} write", write_msg);
                        } else {
                            logger.info("{} read!", msg);
                            break;
                        }
                    } catch (IOException e) {
                        logger.warn(e.getMessage());
                    }
                    cnt++;
                }
            }
        };
        Thread t3 = new Thread() {
            public void run() {
                int cnt = 0;
                while (cnt < 1000) {
                    try {
                        String msg = myRandomAccessFile.read(0, 3);
                        logger.info("{} read.", msg);
                    } catch (IOException e) {
                        logger.warn(e.getMessage());
                    }
                    cnt++;
                }
            }
        };
        t3.start();
        SleepUtil.sleepMilliSecond(1);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        t3.join();
    }

    @Test
    public void sync_os_test() throws Exception {
//        String msg = "abcd";
//        String header = StringUtil.fillZero(6, 3) + StringUtil.fillZero(msg.getBytes().length, 3);
//        logger.info("{}", header);
//        myRandomAccessFile.write(0, (header + msg).getBytes());
//
//        String read_header = myRandomAccessFile.read(0, 6);
//        String h1 = read_header.substring(0, 3);
//        String h2 = read_header.substring(3, 6);
//        logger.info("{} {}", h1, h2);

//        long read_header1 = Long.valueOf(myRandomAccessFile.read(0, 3));
//        int read_header2 = Integer.valueOf(myRandomAccessFile.read(3, 3));
//        String read_value = myRandomAccessFile.read(read_header1, read_header2);
//        logger.info("pos：{}，len：{}，value：{}", read_header1, read_header2, read_value);

        final int header_len = 20;
        final int header_half_len = header_len / 2;
        final int[] header_next = {0};
        //写线程
        Thread w = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                int header_start = 0;
                Random random = new Random();
                while (i < 10) {
                    i++;
                    //随机产生内容
                    String msg = "测试写入" + random.nextInt(10000);
                    String header = StringUtil.fillZero(header_start + header_len, header_half_len)
                            + StringUtil.fillZero(msg.getBytes().length, header_half_len);
                    try {
                        myRandomAccessFile.write(header_start, (header + msg).getBytes());
                        header_start = header_start + header_len + msg.getBytes().length;
                        logger.info("【write】msg：{}，header：{}，header_start：{}", msg, header, header_start);
                    } catch (IOException e) {
                    }
                }
            }
        });
        //读线程
        Thread r = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                int header_start = 0;
                byte[] null_byte = new byte[header_len];
                String NULL_VALUE = new String(null_byte);
                while (i < 10) {
                    i++;
                    //先读一个块
                    try {
                        String header = myRandomAccessFile.read(header_start, header_len);
                        if (NULL_VALUE.equals(header)) {
                            SleepUtil.sleepMilliSecond(50);
                            continue;
                        }
                        //把header分为2部分
                        String pos = header.substring(0, header_half_len);
                        String len = header.substring(header_half_len, header_len);
                        String msg = myRandomAccessFile.read(Long.valueOf(pos), Integer.valueOf(len));
                        header_start = header_start + header_len + Integer.valueOf(len);
                        header_next[0] = header_start;
                        logger.info("【read】header：{}，pos：{}，len：{}，msg：{}，header_start：{}", header, pos, len, msg, header_start);
                    } catch (IOException e) {
                    }
                }
            }
        });
        w.start();
        r.start();
        w.join();
        r.join();
        //移动到第9个进行读取
        //先读一个块
        try {
            String header = myRandomAccessFile.read(header_next[0], header_len);
            if (!NULL_VALUE.equals(header)) {
                //把header分为2部分
                String pos = header.substring(0, header_half_len);
                String len = header.substring(header_half_len, header_len);
                String msg = myRandomAccessFile.read(Long.valueOf(pos), Integer.valueOf(len));
                logger.info("【read】header：{}，pos：{}，len：{}，msg：{}", header, pos, len, msg);
            }
        } catch (IOException e) {
        }
    }
}