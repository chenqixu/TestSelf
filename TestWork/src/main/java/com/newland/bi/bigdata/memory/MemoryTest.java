package com.newland.bi.bigdata.memory;

public class MemoryTest {
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        System.out.println(runtime.totalMemory());
        System.out.println(runtime.freeMemory());
    }
}
