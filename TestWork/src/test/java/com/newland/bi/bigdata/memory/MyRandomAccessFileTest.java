package com.newland.bi.bigdata.memory;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.file.RAFFileMangerCenter;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
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
    private String sm = "d:\\tmp\\data\\mccdr\\r.sm";

    @Before
    public void setUp() throws Exception {
        FileUtil.del(sm);
//        myRandomAccessFile = new MyRandomAccessFile(sm);
//        myRandomAccessFile.setLock(true);
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

        final int[] random_header_pos = {0};
        //写线程
        Thread w = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 0;
                    Random random = new Random();
                    RAFFileMangerCenter rafFileMangerCenter = new RAFFileMangerCenter(sm);
                    while (i < 10) {
                        i++;
                        //随机产生内容
                        String msg = "测试写入" + random.nextInt(10000);
                        rafFileMangerCenter.write(msg);
                        if (i == 5) {
                            //测试一下header_pos是否有用
                            random_header_pos[0] = rafFileMangerCenter.getHeader_pos_next();
                        }
                        if (i == 10) {
                            //写入结束符
                            rafFileMangerCenter.write(RAFFileMangerCenter.END_TAG);
                        }
                    }
                    rafFileMangerCenter.close();
                } catch (Exception e) {
                }
            }
        });
        //读线程
        Thread r = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RAFFileMangerCenter rafFileMangerCenter = new RAFFileMangerCenter(sm);
                    while (true) {
                        String msg = rafFileMangerCenter.read();
                        if (msg == null) {
                            SleepUtil.sleepMilliSecond(5);
                        } else if (msg.equals(RAFFileMangerCenter.END_TAG)) {
                            break;
                        }
                    }
                    rafFileMangerCenter.close();
                } catch (Exception e) {
                }
            }
        });
        w.start();
//        r.start();
        w.join();
//        r.join();
        //从测试的header_pos读起
        RAFFileMangerCenter rafFileMangerCenter = new RAFFileMangerCenter(sm);
        rafFileMangerCenter.setHeader_pos(random_header_pos[0]);
        rafFileMangerCenter.read();
    }
}