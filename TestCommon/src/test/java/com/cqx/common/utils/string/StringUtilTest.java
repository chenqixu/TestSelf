package com.cqx.common.utils.string;

import org.junit.Test;

public class StringUtilTest {

    @Test
    public void toCharArray() {
        String str = "%00DD";
        char[] chars = str.toCharArray();
        for (char c : chars) {
            System.out.println(c);
        }
    }
}