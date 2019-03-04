package com.cqx.write;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.*;

public class WriteDataCallableTest {

    private WriteDataCallable writeDataCallable;

    @Before
    public void setUp() throws Exception {
        writeDataCallable = new WriteDataCallable();
    }

    @Test
    public void call() throws ExecutionException, InterruptedException {
        writeDataCallable.setBatch_cnt(50);
        FutureTask<Integer> futureTask = new FutureTask(writeDataCallable);
        new Thread(futureTask).start();
        int result = futureTask.get();
    }
}