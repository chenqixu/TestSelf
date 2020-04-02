package com.newland.bi.bigdata.memory;

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
        myRandomAccessFile.write("450009493554750");
        myRandomAccessFile.write("450009493554771");
    }

    @Test
    public void read() throws IOException {
        int len = 15;
        for (int i = 0; i < 2; i++) {
            int off = i * len;
            String msg = myRandomAccessFile.read(off, len);
            logger.info("{}", msg);
        }
    }
}