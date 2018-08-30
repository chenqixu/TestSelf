package com.cqx.jmx.threadpoolstatus;

import java.util.Random;

public class FetchTask implements Runnable {
    private final String name;
    private int SleepTime = 0;
    private FetchTaskResult ftr;

	public int getRadomSeconds() {
    	return new Random().nextInt(3000);
    }

    public FetchTask(String name, FetchTaskResult _ftr) {
        this.name = name;
        this.ftr = _ftr;
    }

    public String toString() {
        return "FetchTask: " + name + " SleepTime:" + SleepTime;
    }

    public void run() {
    	/* Fetch remote resource */
    	this.ftr.addResult(name);
    	SleepTime = getRadomSeconds();
    	try {
			Thread.sleep(SleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
