package com.cqx.common.utils.mapreduce;

import org.junit.Test;

public class JobBuilderTest {

    @Test
    public void ascii() {
        String tab = "\t";
        String tab_s = "\\t";
        System.out.println("a" + tab + "b" + tab_s + "c");
    }

}