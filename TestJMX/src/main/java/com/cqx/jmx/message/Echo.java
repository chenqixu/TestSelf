package com.cqx.jmx.message;


public class Echo {
	public static Message msg = new Message();
	public static boolean running = true;
	public static boolean pause = false;
	
	public static void main(String[] args) {
        // 开启JMX Agent。如果不需要JMX,只是单独运行程序，请屏蔽掉下面这行代码
        new MessageEngineAgent().start();
        while(running) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!pause) msg.echo();
        }
    }
}
