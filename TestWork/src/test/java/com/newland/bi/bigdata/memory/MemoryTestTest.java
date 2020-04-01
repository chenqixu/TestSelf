package com.newland.bi.bigdata.memory;

import org.junit.Test;

import static org.junit.Assert.*;

public class MemoryTestTest {

    @Test
    public void readHdfs() throws Exception {
        MemoryTest memoryTest = MemoryTest.builder();
        memoryTest.readHdfs();
    }
}