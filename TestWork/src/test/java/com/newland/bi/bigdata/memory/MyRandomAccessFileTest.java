package com.newland.bi.bigdata.memory;

import com.cqx.common.utils.file.MyRandomAccessFile;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MyRandomAccessFileTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(MyRandomAccessFileTest.class);
    private MyRandomAccessFile myRandomAccessFile;

    @Before
    public void setUp() throws Exception {
        myRandomAccessFile = new MyRandomAccessFile("d:\\tmp\\data\\mccdr\\r.sm");
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
}