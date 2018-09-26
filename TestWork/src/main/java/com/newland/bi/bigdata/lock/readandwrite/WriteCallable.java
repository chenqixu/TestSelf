package com.newland.bi.bigdata.lock.readandwrite;

import com.newland.bi.bigdata.lock.bean.ReadWriteLock;

public class WriteCallable implements Runnable {
	
	private ReadWriteLock lrb;
	private int count = 0;
	
	public WriteCallable(ReadWriteLock lrb) {
		this.lrb = lrb;
	}

	@Override
	public void run() {
		for(;;){
			if(this.lrb != null){
//				this.lrb.increase();
				try {
					this.lrb.lockWrite();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// write deal
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					this.lrb.unlockWrite();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				count++;
//				System.out.println(this+"[this.count]"+count+"[lrb.count]"+this.lrb.getCount());
//				System.out.println(this+"[this.count]"+count);
			}
		}
	}

}
