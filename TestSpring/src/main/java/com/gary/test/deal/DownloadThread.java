package com.gary.test.deal;

public class DownloadThread extends Thread {
	public void run(){
		for(int i=0;i<10;i++){
			System.out.println("[i]"+i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
