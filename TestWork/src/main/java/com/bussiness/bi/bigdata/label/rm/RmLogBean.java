package com.bussiness.bi.bigdata.label.rm;

import com.cqx.common.utils.system.TimeUtil;

import java.text.ParseException;

/**
 * TODO
 *
 * @author chenqixu
 */
public class RmLogBean {
    private Long time;
    private String logContent;
    private String tag;

    public RmLogBean() {
    }

    public RmLogBean(String tag, String time, String logContent) {
        try {
            this.tag = tag;
            this.time = TimeUtil.formatTime(time);
            this.logContent = logContent;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public RmLogBean(String tag, Long time, String logContent) {
        this.tag = tag;
        this.time = time;
        this.logContent = logContent;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
