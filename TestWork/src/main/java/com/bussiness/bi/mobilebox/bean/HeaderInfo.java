package com.bussiness.bi.mobilebox.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 信息头
 *
 * @author chenqixu
 */
public class HeaderInfo {
    //是否4.0探针
    boolean is4Probe = false;
    //事件编码
    protected int code;
    //设备ID
    protected String deviceid;
    //时间
    protected String time;

    public boolean isIs4Probe() {
        return is4Probe;
    }

    public void setIs4Probe(boolean is4Probe) {
        this.is4Probe = is4Probe;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
