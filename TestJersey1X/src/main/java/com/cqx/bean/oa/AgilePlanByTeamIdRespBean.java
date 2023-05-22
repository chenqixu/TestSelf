package com.cqx.bean.oa;

/**
 * AgilePlanByTeamIdRespBean
 *
 * @author chenqixu
 */
public class AgilePlanByTeamIdRespBean {
    private String respResult;
    private AgilePlanByTeamIdRespData[] respData;
    private boolean succ;

    public String getRespResult() {
        return respResult;
    }

    public void setRespResult(String respResult) {
        this.respResult = respResult;
    }

    public AgilePlanByTeamIdRespData[] getRespData() {
        return respData;
    }

    public void setRespData(AgilePlanByTeamIdRespData[] respData) {
        this.respData = respData;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }
}
