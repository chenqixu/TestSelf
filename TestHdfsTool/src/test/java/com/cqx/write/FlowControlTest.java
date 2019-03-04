package com.cqx.write;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class FlowControlTest {

    private FlowControl flowControl;

    @Before
    public void setUp() throws Exception {
        flowControl = FlowControl.builder();
    }

    @Test
    public void printlnStringLength() {
        flowControl.getMsgLength("123");
        flowControl.getMsgLength("你好");
        flowControl.getMsgLength("");
    }
}