package com.newland.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ExcelUtilsTest {

    private ExcelUtils excelUtils;

    @Before
    public void setUp() {
        excelUtils = new ExcelUtils(true);
    }

    @Test
    public void readExcel() throws IOException {
        String read_path = "d:\\tmp\\data\\zip\\read.xlsx";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        for (ExcelSheetList sheet : excelSheetLists) {
            System.out.println(sheet.getSheetName());
            if (sheet.getSheetName().equals("Sheet1")) {
                for (List<String> content : sheet.getSheetList()) {
                    System.out.println(content);
                }
            }
        }
    }

    @Test
    public void readExcelXLS() throws IOException {
        String read_path = "d:\\tmp\\data\\zip\\设计.xls";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        for (ExcelSheetList sheet : excelSheetLists) {
            System.out.println(sheet.getSheetName());
            if (sheet.getSheetName().equals("Sheet1")) {
                for (List<String> content : sheet.getSheetList()) {
                    System.out.println(content);
                }
            }
        }
    }

    @Test
    public void writeExcel() throws IOException {
        String read_path = "d:\\tmp\\data\\zip\\设计.xlsx";
        String write_path = "d:\\tmp\\data\\zip\\copy.xlsx";
        List<ExcelSheetList> excelSheetLists = excelUtils.readExcel(read_path);
        excelUtils.writeExcel(write_path, excelSheetLists);
    }
}