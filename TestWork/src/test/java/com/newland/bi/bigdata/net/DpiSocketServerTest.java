package com.newland.bi.bigdata.net;

import com.newland.bi.bigdata.utils.SleepUtils;
import org.junit.Before;
import org.junit.Test;

public class DpiSocketServerTest {

    public static final int SERVER_PORT = 10991;
    private DpiSocketServer dpiSocketServer;
    private int serverRunTime = 1000;

    @Before
    public void setUp() throws Exception {
        dpiSocketServer = new DpiSocketServer(SERVER_PORT);
    }

    @Test
    public void start() {
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dpiSocketServer.start();
            }
        });
        serverThread.start();
        SleepUtils.sleepSecond(serverRunTime);
        dpiSocketServer.stop();
        try {
            if (serverThread != null)
                serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void startByTimeout() {
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dpiSocketServer.startByTimeout(1000);
            }
        });
        serverThread.start();
        SleepUtils.sleepSecond(serverRunTime);
        dpiSocketServer.stopByTimeout();
        try {
            if (serverThread != null)
                serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}