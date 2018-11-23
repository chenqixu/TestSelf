package client;

public class ThreadT1 {
	public static void main(String[] args) {
		try {
			ThreadT2 dog = new ThreadT2("狗狗");
			ThreadT2 cat = new ThreadT2("喵喵");
			ThreadT2 pig = new ThreadT2("猪猪");

			System.out.println("--- start sprites");
			dog.start();
			cat.start();
			pig.start();
			Thread.sleep(500);
			System.out.println("--- suspend dog");
			dog.suspend();
			System.out.println("--- main thread do something");
			Thread.sleep(500);
			System.out.println("--- resume dog");
			dog.resume();
			Thread.sleep(500);
			System.out.println("--- end dog");
			dog.stop();
			System.out.println("--- main thread do something");
			Thread.sleep(500);
			System.out.println("--- end other sprites");
			cat.stop();
			pig.stop();
			Thread.sleep(100);
			System.out.println("--- exit programe.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
