package com.cqx.pierce.task;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.pierce.bean.ClipBoardValue;

import java.util.HashMap;
import java.util.Map;

/**
 * PLSQLTask
 *
 * @author chenqixu
 */
public class PLSQLTask implements ITask {
    private ClipBoardValue clipBoardValue;

    @Override
    public String getTaskName() {
        return null;
    }

    @Override
    public String getTaskType() {
        return this.clipBoardValue.getType();
    }

    @Override
    public Map<String, String> getTaskParams() {
        return this.clipBoardValue.getParams();
    }

    @Override
    public ClipBoardValue run(ClipBoardValue clipBoardValue) {
        this.clipBoardValue = clipBoardValue;
        //get params
        String tns = clipBoardValue.getParams().get("tns");
        String user_name = clipBoardValue.getParams().get("user_name");
        String pass_word = clipBoardValue.getParams().get("pass_word");
        String sql = clipBoardValue.getParams().get("sql");
        //connect
        //exec sql
        //disconnect
        SleepUtil.sleepMilliSecond(500);
        ClipBoardValue result = new ClipBoardValue();
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        result.setParams(map);
        return result;
    }
}
