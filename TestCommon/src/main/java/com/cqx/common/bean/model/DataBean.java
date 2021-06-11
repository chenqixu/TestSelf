package com.cqx.common.bean.model;

import com.cqx.common.utils.Utils;
import com.cqx.common.utils.jdbc.QueryResultETL;

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
    private long current_ts_Milli;
    private long current_ts_Micro;
    private long formatSecond_time;
    private String formatSecond;
    private String distKey;

    public DataBean(String op_type, String current_ts, List<QueryResultETL> queryResults) throws ParseException {
        this(null, op_type, current_ts, queryResults);
    }

    public DataBean(String pks, String op_type, String current_ts, List<QueryResultETL> queryResults) throws ParseException {
        this.pks = pks;
        this.op_type = op_type;
        this.current_ts = current_ts;
        this.queryResults = queryResults;
        this.current_ts_Milli = Utils.getTime(current_ts.substring(0, current_ts.length() - 3));
        this.current_ts_Micro = Utils.formatTimeByJDK8(current_ts);
        this.formatSecond = Utils.formatTime(getCurrent_ts_Milli());
        this.formatSecond_time = Utils.formatTime(getFormatSecond());
        this.distKey = pks + op_type;
    }

    @Override
    public String toString() {
        return "pks：" + getPks()
                + "，op_type：" + getOp_type()
                + "，current_ts：" + getCurrent_ts()
                + "，queryResults：" + getQueryResults()
                + "，formatSecond_time：" + getFormatSecond_time();
    }

    @Override
    public String getFormatSecond() {
        return this.formatSecond;
    }

    @Override
    public long getFormatSecond_time() {
        return this.formatSecond_time;
    }

    private long getCurrent_ts_Milli() {
        return this.current_ts_Milli;
    }

    @Override
    public long getCurrent_ts_Micro() {
        return this.current_ts_Micro;
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

    @Override
    public String getDistKey() {
        return distKey;
    }

    public void setDistKey(String distKey) {
        this.distKey = distKey;
    }
}
