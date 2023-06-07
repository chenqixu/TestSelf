package com.bussiness.bi.bigdata.excel;

import org.junit.Test;

public class CosmicExcelTest {

    @Test
    public void readCosmicExcel() {
        new CosmicExcel().readCosmicExcel("d:\\Work\\割接\\202212-迁移X9\\cosmic\\X4O域实时位置配置迁改大数据库-附件4、COSMIC工作量拆分.xlsx");
    }
}