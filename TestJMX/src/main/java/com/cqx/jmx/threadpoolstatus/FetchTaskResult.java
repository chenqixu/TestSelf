package com.cqx.jmx.threadpoolstatus;

import java.util.ArrayList;
import java.util.List;

public class FetchTaskResult {
	public List<String> result = new ArrayList<String>();

	public List<String> getResult() {
		return result;
	}
	
	public String toString() {
		return result.toString();
	}

	public void addResult(String str) {
		synchronized (result) {
			this.result.add(str);
		}
	}
}
