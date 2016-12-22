package client;

public class ThreadT2 implements Runnable {
	/*
	 * �߳��ñ���
	 */
	private boolean running = false;

	private boolean waiting = false;

	private Thread thread;

	/*
	 * Business ����
	 */
	private String name;

	public ThreadT2(String name) {
		this.name = name;
		this.thread = new Thread(this);
	}

	/**
	 * �����߳�
	 */
	public void start() {
		running = true;
		thread.start();
	}

	/**
	 * �����߳�
	 */
	public void suspend() {
		if (waiting) { // �ǹ���״̬��ֱ�ӷ���
			return;
		}
		synchronized (this) {
			this.waiting = true;
		}
	}

	/**
	 * �ָ��߳�
	 */
	public void resume() {
		if (!waiting) { // û�й�����ֱ�ӷ���
			return;
		}
		synchronized (this) {
			this.waiting = false;
			this.notifyAll();
		}
	}

	/**
	 * ֹͣ�߳�
	 */
	public void stop() {
		if (!running) { // û��������ֱ�ӷ���
			return;
		}
		synchronized (this) {
			running = false;
		}
	}

	public void run() {
		for (;;) {
			try {
				// �̹߳�����˳�����
				synchronized (this) {
					if (!running) {
						break;
					}
					if (waiting) {
						this.wait();
					}
				}

				// Ӧ����������
				cry();

				// ����ȴ�״̬
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void cry() {
		System.out.println(name + ":woo!");
	}

}
