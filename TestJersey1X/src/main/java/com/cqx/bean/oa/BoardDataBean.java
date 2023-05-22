package com.cqx.bean.oa;

/**
 * BoardDataBean
 *
 * @author chenqixu
 */
public class BoardDataBean {
    private String respResult;
    private BoardDataRespData[] respData;
    private boolean succ;

    public String getRespResult() {
        return respResult;
    }

    public void setRespResult(String respResult) {
        this.respResult = respResult;
    }

    public BoardDataRespData[] getRespData() {
        return respData;
    }

    public void setRespData(BoardDataRespData[] respData) {
        this.respData = respData;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }
}
