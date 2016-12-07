package com.newland.bi.bigdata.bean;

public class ASupper {
	protected String as;
	public String getAs() {
		return as;
	}
	public void setAs(String as) {
		this.as = as;
	}
	
	public static void main(String[] args) {
		Object a = new A1();
		((A1)a).setA1("bbbb");
		A2 b = new A2();
		ASupper s = null;
		s = (ASupper)a;
		System.out.println(s);
		System.out.println(((A1)s).getA1());
	}
}
