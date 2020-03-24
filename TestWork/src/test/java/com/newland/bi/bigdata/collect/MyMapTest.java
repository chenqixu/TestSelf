package com.newland.bi.bigdata.collect;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MyMapTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void printInfo() {
        // 10
        int number = -10;
        MyMap.print("10的二进制");
        MyMap.printInfo(number);
        // 15
        int number1 = 15;
        MyMap.print("15的二进制");
        MyMap.printInfo(number1);
        // << 左移
        MyMap.print("10【左移】1位");
        MyMap.printInfo(number << 1);
        // >> 右移
        MyMap.print("10【右移】1位");
        MyMap.printInfo(number >> 1);
        // <<< 无符号左移

        // <<< 无符号右移

        // & 与：两个操作数中位都为1，结果才为1，否则结果为0。
        MyMap.print("10和15进行【与】运算");
        MyMap.printInfo(number & number1);
        // | 或：两个位只要有一个为1，那么结果就是1，否则就为0。
        MyMap.print("10和15进行【或】运算");
        MyMap.printInfo(number | number1);
        // ~ 非：如果位为0，结果是1，如果位为1，结果是0。
        MyMap.print("10进行【非】运算");
        MyMap.printInfo(~number);
        // ^ 异或：两个操作数的位中，相同则结果为0，不同则结果为1。
        MyMap.print("10和15进行【异或】运算");
        MyMap.printInfo(number ^ number1);
    }

    @Test
    public void hashmapTest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("1", "2");
        map.put("11111", "22222");
        map.get("1");
        String[] arr = new String[16];
    }

    @Test
    public void ModTest() {
        MyMap myMap = new MyMap();
        myMap.Mod();
    }
}