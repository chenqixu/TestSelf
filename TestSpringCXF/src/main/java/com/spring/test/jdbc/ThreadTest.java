package com.spring.test.jdbc;

import java.util.ArrayList;
import java.util.List;

public class ThreadTest {
	class A {
		private int a = 0;
		public int add(){
			a++;
			return a;
		}
		public int getA(){
			return a;
		}
	}
	class TA extends Thread{
		private A a;
		public TA(A _a){
			this.a = _a;
		}
		public void run(){
			a.add();
			System.out.println(this+" "+a.getA());
		}
	}
	public static void main(String[] args) throws Exception {
		List<Thread> tl = new ArrayList<Thread>();
		for(int i=0;i<50;i++){
			tl.add(new ThreadTest().new TA(new ThreadTest().new A()));
			tl.get(i).start();
		}
		for(Thread t: tl){
			t.join();
		}
	}
}
