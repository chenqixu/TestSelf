package com.cqx.thread;

import org.junit.Test;

public class BusThreadTest {

    @Test
    public void run() throws InterruptedException {
        BusThread b1 = new BusThread("1===");
        BusThread b2 = new BusThread("2===");
        BusThread b3 = new BusThread("3===");
        b1.start();
        b2.start();
        b3.start();
        b1.join();
        b2.join();
        b3.join();
        System.out.println("BusThreadTest========" + Thread.currentThread().getName());
        b1.println();

        String a1 = "Null_199_01_rtsp_session_60_20190411_080000_20190411_080059.csv";
        String a2 = "rtsp";
        System.out.println(a1.contains(a2));
    }
}