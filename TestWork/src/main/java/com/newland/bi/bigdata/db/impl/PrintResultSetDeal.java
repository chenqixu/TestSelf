package com.newland.bi.bigdata.db.impl;

/**
 * 打印处理
 *
 * @author chenqixu
 */
public class PrintResultSetDeal implements IResultSetDeal {

    private static final String valueSplit = "|";

    @Override
    public void execValue(String msg) {
        System.out.print(msg);
    }

    @Override
    public void execValueSplit() {
        System.out.print(valueSplit);
    }

    @Override
    public void execValueEnd() {
        System.out.println();
    }
}
