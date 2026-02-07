package com.bussiness.bi.bigdata.file.logfile;

import java.util.List;

/**
 * ExcelFileBean
 *
 * @author chenqixu
 */
public class ExcelFileBean extends LogFileBean {
    private int seqNum;

    ExcelFileBean() {
    }

    ExcelFileBean(String content) {
        super(content);
    }

    ExcelFileBean(List<String> contents) {
        // 任务分类 任务名称 任务状态 负责人
        super(contents.get(1), contents.get(2), contents.get(3), contents.get(4), contents.get(5), contents.get(6));
        this.seqNum = Integer.valueOf(contents.get(0));
    }

    public String toExcel() {
        String split = "\t";
        return getSeqNum()
                + split + getTaskType()
                + split + getRealTaskName()
                + split + getTaskStatus()
                + split + getTaskPIC()
                + split + getStartTime()
                + split + getEndTime()
                ;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }
}
