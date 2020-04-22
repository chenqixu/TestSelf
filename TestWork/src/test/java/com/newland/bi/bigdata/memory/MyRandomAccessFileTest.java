package com.newland.bi.bigdata.memory;

import com.cqx.common.utils.file.FileUtil;
import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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
//                        logger.error(e.getMessage(), e);
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
//                        logger.error(e.getMessage(), e);
                    }
                    cnt++;
                }
            }
        };
        Thread t3 = new Thread() {
            public void run() {
                int cnt = 0;
                while (cnt < 100) {
                    try {
                        String msg = myRandomAccessFile.read(0, 3);
                        logger.info("{} read.", msg);
                    } catch (IOException e) {
                        logger.warn(e.getMessage());
//                        logger.error(e.getMessage(), e);
                    }
                    cnt++;
                }
            }
        };
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
    }

}