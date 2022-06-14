package com.bussiness.bi.bigdata.bean;

import com.alibaba.fastjson.JSON;

/**
 * 校验文件
 *
 * @author chenqixu
 */
public class CheckFile {
    private long size;
    private int rowNumber;

    public CheckFile() {
    }

    public CheckFile(long size, int rowNumber) {
        this.size = size;
        this.rowNumber = rowNumber;
    }

    public static CheckFile jsonToBean(String json) {
        return JSON.parseObject(json, CheckFile.class);
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
