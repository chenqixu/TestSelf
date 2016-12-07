package com.cqx.thinking;

public class Two {
	int i1;
	char c1;
	public int getI1() {
		return i1;
	}
	public void setI1(int i1) {
		this.i1 = i1;
	}
	public char getC1() {
		return c1;
	}
	public void setC1(char c1) {
		this.c1 = c1;
	}
	public static void main(String[] args) {
//		Two two = new Two();
//		System.out.println(two.getI1());
//		System.out.println(two.getC1());
		Object a = null;
		Object b = null;
		System.out.println(a==b);
		System.out.println(null==null);
	}
}
