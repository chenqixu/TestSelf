package com.cqx.common.utils.excel;

import java.util.ArrayList;
import java.util.List;

public class ExcelSheetList {
    private String sheetName;// sheetName
    private List<List<String>> sheetList; // sheet内容,多行组成一个list,每行单独一个list
    private List<String> lineObj;

    public ExcelSheetList() {
        sheetList = new ArrayList<>();
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<List<String>> getSheetList() {
        return sheetList;
    }

    public void setSheetList(List<List<String>> sheetList) {
        this.sheetList = sheetList;
    }

    public ExcelSheetList newLine() {
        lineObj = new ArrayList<>();
        sheetList.add(lineObj);
        return this;
    }

    public ExcelSheetList addColumn(String value) {
        if (lineObj != null) lineObj.add(value);
        return this;
    }
}
