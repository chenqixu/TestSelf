package client;

public class ThreadT2 implements Runnable {
	/*
	 * 线程用变量
	 */
	private boolean running = false;

	private boolean waiting = false;

	private Thread thread;

	/*
	 * Business 变量
	 */
	private String name;

	public ThreadT2(String name) {
		this.name = name;
		this.thread = new Thread(this);
	}

	/**
	 * 启动线程
	 */
	public void start() {
		running = true;
		thread.start();
	}

	/**
	 * 挂起线程
	 */
	public void suspend() {
		if (waiting) { // 是挂起状态则直接返回
			return;
		}
		synchronized (this) {
			this.waiting = true;
		}
	}

	/**
	 * 恢复线程
	 */
	public void resume() {
		if (!waiting) { // 没有挂起则直接返回
			return;
		}
		synchronized (this) {
			this.waiting = false;
			this.notifyAll();
		}
	}

	/**
	 * 停止线程
	 */
	public void stop() {
		if (!running) { // 没有运行则直接返回
			return;
		}
		synchronized (this) {
			running = false;
		}
	}

	public void run() {
		for (;;) {
			try {
				// 线程挂起和退出处理
				synchronized (this) {
					if (!running) {
						break;
					}
					if (waiting) {
						this.wait();
					}
				}

				// 应该做的事情
				cry();

				// 进入等待状态
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
