package com.newland.bi.bigdata.parser.stream;

import org.junit.Before;
import org.junit.Test;

public class StreamParserUtilTest {

    StreamParserUtil streamParserUtil;
    String path;

    @Before
    public void setUp() throws Exception {
        streamParserUtil = new StreamParserUtil();
        path = "d:\\tmp\\data\\dpi\\dpi_s1mme\\LTE_S1MME_028470789002_20190603110100.txt";
    }

    @Test
    public void dynamicDeal() throws Exception {
        for (int i = 0; i < 20; i++)
            streamParserUtil.dynamicDeal(path);
    }

    @Test
    public void staticDeal() throws Exception {
        for (int i = 0; i < 20; i++)
            streamParserUtil.staticDeal(path);
    }

    @Test
    public void printField() throws Exception {
        streamParserUtil.printField(path, 1);
    }

    @Test
    public void printFieldStatic() throws Exception {
        streamParserUtil.printFieldStatic(path, 1);
    }
}