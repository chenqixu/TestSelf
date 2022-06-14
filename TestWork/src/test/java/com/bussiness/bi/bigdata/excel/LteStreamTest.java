package com.bussiness.bi.bigdata.excel;

import com.bussiness.bi.bigdata.excel.LteStream;
import org.junit.Test;

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