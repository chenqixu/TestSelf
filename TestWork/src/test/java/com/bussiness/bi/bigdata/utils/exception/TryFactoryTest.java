package com.bussiness.bi.bigdata.utils.exception;

import com.bussiness.bi.bigdata.utils.exception.ITry;
import com.bussiness.bi.bigdata.utils.exception.TryFactory;
import org.junit.Test;

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