package com.cqx.bean.oa;

/**
 * CasTokenBean
 *
 * @author chenqixu
 */
public class CasTokenBean {
    private int respResult;
    private CasTokenRespData respData;
    private boolean succ;

    public int getRespResult() {
        return respResult;
    }

    public void setRespResult(int respResult) {
        this.respResult = respResult;
    }

    public CasTokenRespData getRespData() {
        return respData;
    }

    public void setRespData(CasTokenRespData respData) {
        this.respData = respData;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }
}
