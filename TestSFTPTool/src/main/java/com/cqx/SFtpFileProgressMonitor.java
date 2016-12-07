package com.cqx;


import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.jcraft.jsch.SftpProgressMonitor;

public class SFtpFileProgressMonitor extends TimerTask implements
		SftpProgressMonitor {
	private long progressInterval = 5 * 1000; // Ĭ�ϼ��ʱ��Ϊ5��
	private boolean isEnd = false; // ��¼�����Ƿ����
	private long transfered; // ��¼�Ѵ���������ܴ�С
	private long fileSize; // ��¼�ļ��ܴ�С
	private Timer timer; // ��ʱ������
	private boolean isScheduled = false; // ��¼�Ƿ�������timer��ʱ��

	public SFtpFileProgressMonitor(long fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public void run() {
		if (!isEnd()) { // �жϴ����Ƿ��ѽ���
			System.out.println("Transfering is in progress.");
			long transfered = getTransfered();
			if (transfered != fileSize) { // �жϵ�ǰ�Ѵ������ݴ�С�Ƿ�����ļ��ܴ�С
				System.out.println("Current transfered: " + transfered
						+ " bytes");
				sendProgressMessage(transfered);
			} else {
				System.out.println("Sending progress message: 100%");
				System.out.println("File transfering is done.");
				setEnd(true); // �����ǰ�Ѵ������ݴ�С�����ļ��ܴ�С��˵������ɣ�����end
			}
		} else {
			System.out.println("Sending progress message: 100%");
			System.out.println("Transfering done. Cancel timer.");
			stop(); // ������������ֹͣtimer��ʱ��
			return;
		}
	}

	public void stop() {
		System.out.println("Try to stop progress monitor.");
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
			isScheduled = false;
		}
		System.out.println("Progress monitor stoped.");
	}

	public void start() {
		System.out.println("Try to start progress monitor.");
		if (timer == null) {
			timer = new Timer();
		}
		timer.schedule(this, 1000, progressInterval);
		isScheduled = true;
		System.out.println("Progress monitor started.");
	}

	/**
	 * ��ӡprogress��Ϣ
	 * 
	 * @param transfered
	 */
	private void sendProgressMessage(long transfered) {
		if (fileSize != 0) {
			double d = ((double) transfered * 100) / (double) fileSize;
			DecimalFormat df = new DecimalFormat("#.##");
			System.out.println("Sending progress message: " + df.format(d)
					+ "%");
		} else {
			System.out.println("Sending progress message: " + transfered);
		}
	}

	/**
	 * ʵ����SftpProgressMonitor�ӿڵ�count����
	 */
	public boolean count(long count) {
		if (isEnd())
			return false;
		if (!isScheduled) {
			start();
		}
		add(count);
		return true;
	}

	/**
	 * ʵ����SftpProgressMonitor�ӿڵ�end����
	 */
	public void end() {
		setEnd(true);
		System.out.println("transfering end.");
	}

	private synchronized void add(long count) {
		transfered = transfered + count;
	}

	private synchronized long getTransfered() {
		return transfered;
	}

	public synchronized void setTransfered(long transfered) {
		this.transfered = transfered;
	}

	private synchronized void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	public synchronized boolean isEnd() {
		return isEnd;
	}

	public void init(int op, String src, String dest, long max) {
		// Not used for putting InputStream
	}
}

