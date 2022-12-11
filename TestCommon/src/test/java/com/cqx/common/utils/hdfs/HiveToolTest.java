package com.cqx.common.utils.hdfs;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HiveToolTest {
    private HiveTool hiveTool;

    @Before
    public void setUp() throws Exception {
        hiveTool = new HiveTool();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void metaStoreDemo() throws TException {
        hiveTool.metaStoreDemo();
    }
}