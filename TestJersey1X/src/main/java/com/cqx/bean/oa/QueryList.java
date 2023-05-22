package com.cqx.bean.oa;

/**
 * QueryList
 *
 * @author chenqixu
 */
public class QueryList {
    private String respResult;
    private QueryListRespData[] respData;
    private boolean succ;

    public String getRespResult() {
        return respResult;
    }

    public void setRespResult(String respResult) {
        this.respResult = respResult;
    }

    public QueryListRespData[] getRespData() {
        return respData;
    }

    public void setRespData(QueryListRespData[] respData) {
        this.respData = respData;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }
}
