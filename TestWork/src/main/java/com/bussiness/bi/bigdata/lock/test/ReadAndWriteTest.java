package com.bussiness.bi.bigdata.lock.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bussiness.bi.bigdata.lock.bean.ReadWriteLock;
import com.bussiness.bi.bigdata.lock.readandwrite.MonitorRW;
import com.bussiness.bi.bigdata.lock.readandwrite.ReadCallable;
import com.bussiness.bi.bigdata.lock.readandwrite.WriteCallable;

public class ReadAndWriteTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		int taskSize = 3;
		ReadWriteLock lrb = new ReadWriteLock();
		ExecutorService pool = Executors.newFixedThreadPool(taskSize);
		pool.submit(new WriteCallable(lrb));
//		pool.submit(new WriteCallable(lrb));
		pool.submit(new ReadCallable(lrb));
		pool.submit(new MonitorRW(lrb));
//		pool.shutdown();
	}
}
