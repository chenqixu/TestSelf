package com.newland.bi.bigdata;

public class MemeryTest {
	public static void main(String[] args) {
		Runtime runtime = Runtime.getRuntime();
        System.out.println(runtime.totalMemory());
        System.out.println(runtime.freeMemory());
        
        
	}
}
