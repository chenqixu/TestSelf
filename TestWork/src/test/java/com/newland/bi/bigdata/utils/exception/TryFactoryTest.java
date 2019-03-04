package com.newland.bi.bigdata.utils.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class TryFactoryTest {

    @Test
    public void builder() {
        TryFactory.builder(new ITry() {
            @Override
            public void run() throws Exception {
                throw new NullPointerException("aaa is null.");
            }
        }).start();
    }
}