package com.cqx.work.stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExecutorServiceDemoTest {
    private ExecutorServiceDemo executorServiceDemo;

    @Before
    public void setUp() throws Exception {
        executorServiceDemo = new ExecutorServiceDemo();
    }

    @After
    public void tearDown() throws Exception {
        if (executorServiceDemo != null) executorServiceDemo.close();
    }

    @Test
    public void demo1() throws Exception {
        executorServiceDemo.demo1();
    }

    @Test
    public void demo2() throws Exception {
        executorServiceDemo.demo2();
    }

    @Test
    public void demo3() throws Exception {
        executorServiceDemo.demo3();
    }

    @Test
    public void demo4() throws Exception {
        executorServiceDemo.demo4();
    }

    @Test
    public void demo5() throws Exception {
        executorServiceDemo.demo5();
    }

    @Test
    public void demo6() throws Exception {
        executorServiceDemo.demo6();
    }

    @Test
    public void demo7AbortPolicy() throws Exception {
        executorServiceDemo.demo7AbortPolicy();
    }

    @Test
    public void demo7CallerRunsPolicy() throws Exception {
        executorServiceDemo.demo7CallerRunsPolicy();
    }

    @Test
    public void demo7DiscardPolicy() throws Exception {
        executorServiceDemo.demo7DiscardPolicy();
    }
}