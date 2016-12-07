package com.cqx;

import java.util.Queue;
import java.util.Random;

import org.apache.log4j.Logger;

public class TestShareThread extends Thread {
	// ��־��¼��
	private static Logger logger = Logger.getLogger(TestShareThread.class);
	
	//�̰߳�ȫ���У���ŵ�ǰҪ���ص������ļ���
	private Queue<String> fileNameQueue;
	private String threadName;
	private String fileName ;
	
	public TestShareThread(Queue<String> fileNameQueue){
		this.fileNameQueue = fileNameQueue;
	}
	
	public void run(){
		//��ȡ��ǰ�߳�����
		threadName = Thread.currentThread().getName();
		while((fileName = fileNameQueue.poll()) != null){
			// �ж��Ƿ���Ҫ�˳�
			if(InitCollectorFile.exit()){
				logger.info("[TestShareThread]��鵽�˳��ļ�,�߳��˳�.");
				break;
			}
			logger.info(threadName+" [poll]"+fileName);
			try {
				// �������1��9��
				int sleep = randomSleep()*1000;
				logger.info(threadName+" �������"+sleep+"����");
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				logger.error(e.toString(), e);
			}
		}
	}
	
	/**
	 * ���1������
	 * */
	public int randomSleep() {
		String s = "123456789";
		char[] c = s.toCharArray();
		Random random = new Random();
		return Integer.valueOf(String.valueOf(c[random.nextInt(c.length)]));
	}
}
