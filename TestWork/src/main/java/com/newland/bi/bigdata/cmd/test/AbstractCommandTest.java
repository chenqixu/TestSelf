package com.newland.bi.bigdata.cmd.test;

public abstract class AbstractCommandTest {
	/**
	 * init
	 * */
	protected void init() {
		System.out.println(this.getClass());
		System.out.println(this);
	}
}
