package com.bussiness.bi.bigdata.excel;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MergeExcelTest {
    private MergeExcel mergeExcel;
    private String readPath = "d:\\Work\\ETL\\SDTP\\5G.xlsx";
    private String writePath = "d:\\Work\\ETL\\SDTP\\5G-1.xlsx";

    @Before
    public void setUp() {
        mergeExcel = new MergeExcel();
    }

    @Test
    public void mergeN1N2() throws IOException {
        mergeExcel.merge(writePath, mergeExcel.read(readPath, "Sheet3", "Sheet3M"));
    }

    @Test
    public void mergeCommon() throws IOException {
        mergeExcel = new MergeExcel();
        mergeExcel.merge(writePath, mergeExcel.read(readPath, "公共信息", "公共信息M"));
    }

    @Test
    public void mergeN14() throws IOException {
        mergeExcel = new MergeExcel();
        mergeExcel.merge(writePath, mergeExcel.read(readPath, "N14-1", "N14-1M"));
    }
}