package com.cqx.bean.oa;

/**
 * AgileTeamMemberEmployee
 *
 * @author chenqixu
 */
public class AgileTeamMemberEmployee {
    private String respResult;
    private Employee[] respData;
    private boolean succ;

    public String getRespResult() {
        return respResult;
    }

    public void setRespResult(String respResult) {
        this.respResult = respResult;
    }

    public Employee[] getRespData() {
        return respData;
    }

    public void setRespData(Employee[] respData) {
        this.respData = respData;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }
}
