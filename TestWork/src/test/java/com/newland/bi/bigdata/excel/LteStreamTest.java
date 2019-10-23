package com.newland.bi.bigdata.excel;

import org.junit.Test;

import static org.junit.Assert.*;

public class LteStreamTest {

    private LteStream lteStream = new LteStream();

    @Test
    public void run() {
        lteStream.run("d:\\Work\\实时\\流前流后合并\\类型关系.xlsx");
    }

    @Test
    public void cvsinfo() {
        lteStream.cvsinfo("d:\\Work\\WEB\\资源服务化\\自动化编译部署.xlsx");
    }
}