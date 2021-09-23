package com.cqx.common.utils.chm;

import org.junit.Test;

import java.io.IOException;

public class Chm4jUtilTest {

    @Test
    public void read() throws IOException {
        new Chm4jUtil().read("d:\\tmp\\chm\\01.chm");
    }
}