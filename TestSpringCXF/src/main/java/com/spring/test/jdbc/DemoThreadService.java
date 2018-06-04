package com.spring.test.jdbc;

import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service 
public class DemoThreadService {
	@Resource(name = "getThreadPool")
	private Executor executor;
	private TimestenTest1 tt1;
	
	public void setTt1(TimestenTest1 tt1) {
		this.tt1 = tt1;
	}

	public void executeAsyncQueryTask(Map<String, String> _params){
		if(tt1!=null)
			tt1.queryUserbyStation(_params);
	}
	
	@Async
	// 通过@Async注解方法表名这个方法是一个异步方法，如果注解在类级别，则表名该类的所有方法都是异步的，
	// 而这里的方法自动被注入使用ThreadPoolTaskExecutor作为TaskExecutor
	public void executeAsyncTask(Integer i) {
		System.out.println("执行异步任务：" + i);
	}
	
	public void executeAsyncTaskPlus(final Integer i) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				System.out.println("执行异步任务+1：" + i);
			}
		});
	}
}
