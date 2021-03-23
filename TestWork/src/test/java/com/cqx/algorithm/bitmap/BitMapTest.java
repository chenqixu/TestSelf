package com.cqx.algorithm.bitmap;

import org.junit.Test;

import static org.junit.Assert.*;

public class BitMapTest {

    @Test
    public void add() {
        BitMap bitmap = new BitMap(2000000000);
        int num = 171;
        bitmap.add(num);
        System.out.println("插入"+num+"成功");

        boolean isexsit = bitmap.contain(num);
        System.out.println(num+"是否存在:"+isexsit);

        bitmap.clear(num);
        isexsit = bitmap.contain(num);
        System.out.println(num+"是否存在:"+isexsit);

        System.out.println(bitmap.getBits());
    }
}