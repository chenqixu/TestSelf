package com.cqx.bean.oa;

/**
 * MoveCardRespBean
 *
 * @author chenqixu
 */
public class MoveCardRespBean {
    private String respResult;
    private TaskCard respData;
    private boolean succ;

    public String getRespResult() {
        return respResult;
    }

    public void setRespResult(String respResult) {
        this.respResult = respResult;
    }

    public boolean isSucc() {
        return succ;
    }

    public void setSucc(boolean succ) {
        this.succ = succ;
    }

    public TaskCard getRespData() {
        return respData;
    }

    public void setRespData(TaskCard respData) {
        this.respData = respData;
    }
}
