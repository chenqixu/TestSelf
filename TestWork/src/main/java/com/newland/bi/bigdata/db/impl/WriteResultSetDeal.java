package com.newland.bi.bigdata.db.impl;

import com.newland.bi.bigdata.changecode.FileUtil;

/**
 * 写入处理
 *
 * @author chenqixu
 */
public class WriteResultSetDeal implements IResultSetDeal {

    private static final String valueSplit = "|";
    public static final String newLine = System.getProperty("line.separator");
    private FileUtil fileUtil;

    public WriteResultSetDeal(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @Override
    public void execValue(String msg) {
        fileUtil.write(msg);
    }

    @Override
    public void execValueSplit() {
        fileUtil.write(valueSplit);
    }

    @Override
    public void execValueEnd() {
        fileUtil.write(newLine);
    }
}
