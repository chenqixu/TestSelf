package com.bussiness.bi.jkreport.bean;

/**
 * 投诉告警工单
 *
 * @author chenqixu
 */
public class Alarm {
    private String order_id;//单号
    private String alarm_content;//内容
    private String order_time;//告警时间

    public Alarm() {
    }

    public Alarm(String order_id, String alarm_content, String order_time) {
        this.order_id = order_id;
        this.alarm_content = alarm_content;
        this.order_time = order_time;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getAlarm_content() {
        return alarm_content;
    }

    public void setAlarm_content(String alarm_content) {
        this.alarm_content = alarm_content;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }
}
