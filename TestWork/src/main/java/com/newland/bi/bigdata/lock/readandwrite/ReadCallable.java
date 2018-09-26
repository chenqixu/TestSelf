package com.newland.bi.bigdata.lock.readandwrite;

import com.newland.bi.bigdata.lock.bean.ReadWriteLock;

public class ReadCallable implements Runnable {
	
	private ReadWriteLock lrb;
	private int count = 0;
	
	public ReadCallable(ReadWriteLock lrb) {
		this.lrb = lrb;
	}

	@Override
	public void run() {
		for(;;){
			if(this.lrb != null){
//				this.lrb.getCount();
				try {
					this.lrb.lockRead();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// read deal
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.lrb.unlockRead();
				count++;
//				System.out.println(this+"[this.count]"+count+"[lrb.count]"+this.lrb.getCount());
//				System.out.println(this+"[this.count]"+count);
			}
		}
	}

}
