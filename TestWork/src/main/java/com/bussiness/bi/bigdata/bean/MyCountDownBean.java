package com.bussiness.bi.bigdata.bean;

import org.apache.hadoop.fs.Path;

/**
 * MyCountDownBean
 *
 * @author chenqixu
 */
public class MyCountDownBean {
    private Path mergerPath;
    private String localBackUpPath;

    public static MyCountDownBean newbuilder() {
        return new MyCountDownBean();
    }

    public Path getMergerPath() {
        return mergerPath;
    }

    public MyCountDownBean setMergerPath(Path mergerPath) {
        this.mergerPath = mergerPath;
        return this;
    }

    public String getLocalBackUpPath() {
        return localBackUpPath;
    }

    public MyCountDownBean setLocalBackUpPath(String localBackUpPath) {
        this.localBackUpPath = localBackUpPath;
        return this;
    }
}
