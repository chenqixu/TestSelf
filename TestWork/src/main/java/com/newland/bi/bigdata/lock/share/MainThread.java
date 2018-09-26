package com.newland.bi.bigdata.lock.share;

public class MainThread {
	private static int number;
	private static boolean ready;
	
	private static class ReadThread extends Thread {
		@Override
		public void run(){
			System.out.println(ready);
			while(!ready){
//				System.out.println("wait...");
				Thread.yield();
			}
			System.out.println(number);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new ReadThread().start();
//		Thread.sleep(5);
		number = 42;
		ready = true;
	}
}
