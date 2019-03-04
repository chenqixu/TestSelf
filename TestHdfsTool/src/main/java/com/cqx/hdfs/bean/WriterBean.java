package com.cqx.hdfs.bean;

import com.alibaba.fastjson.JSON;

/**
 * WriterBean
 *
 * @author chenqixu
 */
public class WriterBean {
    // 文件数据周期时间，会持久化到zookeeper
    String fileDataDate;
    // 路径+名称，会持久化到zookeeper
    String pathAndFileName;

    private WriterBean() {
    }

    public static WriterBean builder() {
        return new WriterBean();
    }

    public String getPathAndFileName() {
        return pathAndFileName;
    }

    public WriterBean setPathAndFileName(String pathAndFileName) {
        this.pathAndFileName = pathAndFileName;
        return this;
    }

    public String getFileDataDate() {
        return fileDataDate;
    }

    public void setFileDataDate(String fileDataDate) {
        this.fileDataDate = fileDataDate;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}
