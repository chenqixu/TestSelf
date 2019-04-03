package com.cqx.pool.util;

import com.cqx.pool.ftp.FtpClientFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class LinkedBlockingDequeTest {

    private LinkedBlockingDeque linkedBlockingDeque;

    @Before
    public void setUp() throws Exception {
        linkedBlockingDeque = new LinkedBlockingDeque();
    }

    @Test
    public void pollFirst() throws Exception {
        linkedBlockingDeque.pollFirst(FtpClientFactory.CONNECT_TIME_WAIT, TimeUnit.MILLISECONDS);
    }

    @Test
    public void nanoTime() throws Exception {
        linkedBlockingDeque.nanoTime();
    }
}