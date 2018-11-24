package com.cqx.jmx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HelloWorldStatus implements HelloWorldStatusMBean, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> datalist;
	
	public HelloWorldStatus() {
		datalist = new ArrayList<String>();
	}
	
	public void add(String str) {
		datalist.add(str);
	}

	@Override
	public List<String> getList() {
		return datalist;
	}

}
