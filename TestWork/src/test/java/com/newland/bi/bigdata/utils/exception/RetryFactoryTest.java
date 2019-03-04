package com.newland.bi.bigdata.utils.exception;

import org.junit.Test;

import java.io.FileNotFoundException;

public class RetryFactoryTest {

    @Test
    public void start() {
        RetryFactory.builder(new IDeal() {
            @Override
            public void dealEvent() throws Exception {
                throw new FileNotFoundException("test.log not find!");
            }
        }, new ICatch() {
            @Override
            public void catchEvent(Exception e) {
                if (e instanceof FileNotFoundException) {
                    System.out.println("catch FileNotFoundException!");
                }
            }

            @Override
            public void release() {
                System.out.println("relase...");
            }
        }).setRetryCnt(3).start();
    }
}