package com.bussiness.bi.jkreport.bean;

import com.cqx.common.utils.system.TimeUtil;

/**
 * HdfsLSBean
 *
 * @author chenqixu
 */
public class HdfsLSBean implements Comparable<HdfsLSBean>, HdfsLSResult.FastFailureTask {
    private String date;
    private String type = "default";
    private String content;
    private String dateFormat = "yyyyMMddHHmm";
    private HdfsLSCheck hdfsLSCheck;

    public HdfsLSBean() {
    }

    public HdfsLSBean(String type, String date, String content) {
        this.type = type;
        this.date = date;
        this.content = content;
    }

    public HdfsLSBean(String date, String content) {
        this("default", date, content);
    }

    public void check() {
        if (hdfsLSCheck != null) hdfsLSCheck.check();
    }

    public String toString() {
        return "[date]" + date + ",[type]" + type + ",[content]" + content + ",[dateFormat]" + dateFormat;
    }

    @Override
    public int compareTo(HdfsLSBean o) {
        //比较规则
        return TimeUtil.timeComparison(this.date, o.date, this.dateFormat);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public HdfsLSCheck getHdfsLSCheck() {
        return hdfsLSCheck;
    }

    public void setHdfsLSCheck(HdfsLSCheck hdfsLSCheck) {
        this.hdfsLSCheck = hdfsLSCheck;
    }

    @Override
    public String getTaskName() {
        return getContent();
    }
}
