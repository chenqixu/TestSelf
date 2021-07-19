package com.cqx.common.utils.system;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ArraysUtilTest {

    @Test
    public void setToStr() {
        List<String> list = new ArrayList<>();
        list.add("t1");
        list.add("t2");
        list.add("t3");
        System.out.println(ArraysUtil.collectionToStr(list, ','));
    }
}