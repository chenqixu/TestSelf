package com.cqx.spout;

import com.cqx.zookeeper.ZkInfo;

public class MainThread extends Thread {
	private String zkserver = "";
	public MainThread(String _zkserver){
		this.zkserver = _zkserver;
	}
	public void run(){		
		ZkInfo zkInfo = new ZkInfo(zkserver);
		StatusSpout ss = new StatusSpout(zkInfo);
		ss.open();
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		if(args.length!=1){
			System.out.println("you need input zkserver.");
			System.exit(-1);
		}
		String zkserver = args[0];
		MainThread mt = new MainThread(zkserver);
		mt.start();
	}
}
