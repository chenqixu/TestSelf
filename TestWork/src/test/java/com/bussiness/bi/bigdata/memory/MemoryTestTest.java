package com.bussiness.bi.bigdata.memory;

import com.bussiness.bi.bigdata.memory.MemoryTest;
import org.junit.Test;

public class MemoryTestTest {

    @Test
    public void readHdfs() throws Exception {
        MemoryTest memoryTest = MemoryTest.builder();
        memoryTest.readHdfs();
    }
}