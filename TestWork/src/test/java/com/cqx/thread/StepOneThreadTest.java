package com.cqx.thread;

import org.junit.Test;

public class StepOneThreadTest {

    @Test
    public void run() throws InterruptedException {
        StepOneThread stepOneThread = new StepOneThread();
        stepOneThread.start();
        stepOneThread.waitForAndReStart();
//        stepOneThread.waitFor();
//        System.out.println("isTerminated：" + stepOneThread.isTerminated());
//        System.out.println("isError：" + stepOneThread.isError());
//        System.out.println("getErrorMsg：" + stepOneThread.getErrorMsg());
    }
}