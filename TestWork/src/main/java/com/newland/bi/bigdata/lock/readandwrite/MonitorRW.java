package com.newland.bi.bigdata.lock.readandwrite;

import com.newland.bi.bigdata.lock.bean.ReadWriteLock;

public class MonitorRW implements Runnable {
	
	private ReadWriteLock lrb;
	
	public MonitorRW(ReadWriteLock lrb) {
		this.lrb = lrb;
	}

	@Override
	public void run() {
		for(;;){
			if(this.lrb != null){
				System.out.println(this+" "+this.lrb.toString());
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
