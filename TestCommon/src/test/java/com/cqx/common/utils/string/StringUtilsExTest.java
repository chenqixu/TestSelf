package com.cqx.common.utils.string;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class StringUtilsExTest {

    @Test
    public void md5() {
        System.out.println(DigestUtils.md5Hex("hello"));
        System.out.println(StringUtilsEx.Md5("hello"));
    }
}