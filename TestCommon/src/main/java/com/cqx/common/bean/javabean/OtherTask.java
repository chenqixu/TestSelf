package com.cqx.common.bean.javabean;

/**
 * OtherTask
 *
 * @author chenqixu
 */
public class OtherTask implements ITask {

    public String getOther() {
        return "other";
    }

    @Override
    public int getTask_id() {
        return 0;
    }
}
