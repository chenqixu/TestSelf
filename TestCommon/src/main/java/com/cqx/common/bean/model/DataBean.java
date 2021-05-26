package com.cqx.common.bean.model;

import com.alibaba.fastjson.JSON;
import com.cqx.common.utils.jdbc.QueryResultETL;
import com.cqx.common.utils.system.TimeUtil;

import java.text.ParseException;
import java.util.List;

/**
 * DataBean
 *
 * @author chenqixu
 */
public class DataBean implements IDataFilterBean {
    private String pks;
    private String op_type;
    private String current_ts;
    private List<QueryResultETL> queryResults;

    public DataBean(String op_type, String current_ts, List<QueryResultETL> queryResults) {
        this.op_type = op_type;
        this.current_ts = current_ts;
        this.queryResults = queryResults;
    }

    public DataBean(String pks, String op_type, String current_ts, List<QueryResultETL> queryResults) {
        this.pks = pks;
        this.op_type = op_type;
        this.current_ts = current_ts;
        this.queryResults = queryResults;
    }

    public static DataBean jsonToBean(String json) {
        return JSON.parseObject(json, DataBean.class);
    }

    @Override
    public String toJson() {
        return JSON.toJSONString(this);
    }

    @Override
    public String toString() {
        long formatSecond_time = 0L;
        try {
            formatSecond_time = getFormatSecond_time();
        } catch (ParseException e) {
            //
        }
        return "op_type：" + op_type + "，current_ts：" + current_ts + "，queryResults：" + queryResults
                + "，formatSecond_time：" + formatSecond_time;
    }

    @Override
    public String getFormatSecond() throws ParseException {
        return TimeUtil.formatTime(getCurrent_ts_Milli());
    }

    @Override
    public long getFormatSecond_time() throws ParseException {
        return TimeUtil.formatTime(getFormatSecond());
    }

    public long getCurrent_ts_Milli() throws ParseException {
        return TimeUtil.getTime(current_ts.substring(0, current_ts.length() - 3));
    }

    @Override
    public long getCurrent_ts_Micro() {
        return TimeUtil.formatTimeByJDK8(current_ts);
    }

    @Override
    public int compareTo(IDataFilterBean dst) {
        return Long.compare(this.getCurrent_ts_Micro(), dst.getCurrent_ts_Micro());
    }

    public String getOp_type() {
        return op_type;
    }

    public List<QueryResultETL> getQueryResults() {
        return queryResults;
    }

    public String getPks() {
        return pks;
    }

    public String getCurrent_ts() {
        return current_ts;
    }

    public void setCurrent_ts(String current_ts) {
        this.current_ts = current_ts;
    }
}
