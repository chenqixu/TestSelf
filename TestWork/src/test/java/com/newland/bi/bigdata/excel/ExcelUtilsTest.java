package com.newland.bi.bigdata.excel;

import com.newland.bi.bigdata.bean.ExcelSheetList;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ExcelUtilsTest {

    private ExcelUtils excelUtils;

    @Before
    public void setUp() {
        excelUtils = new ExcelUtils();
    }

    @Test
    public void readExcel() {
    }

    @Test
    public void writeExcel() throws IOException {
        String read_path = "d:\\tmp\\data\\zip\\设计.xlsx";
        String write_path = "d:\\tmp\\data\\zip\\copy.xlsx";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        excelUtils.writeExcel(write_path, excelSheetLists);
    }
}