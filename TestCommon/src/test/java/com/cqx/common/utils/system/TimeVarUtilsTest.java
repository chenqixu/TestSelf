package com.cqx.common.utils.system;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TimeVarUtilsTest {

    @Test
    public void parserTaskID() {
        TimeVarUtils timeVarUtils = new TimeVarUtils();

        String task_id = "101067676513@2021062415000000";
        Map params = new HashMap<>();
        params.put("sql1", "select * from table1 where create_time bewteen to_timestamp('[_data_pre_hour]0000','yyyymmddhh24miss') and to_timestamp('[_data_pre_hour]5959','yyyymmddhh24miss')");
        params.put("sql2", "select * from table1 where create_time>='[_data_time]'");
        timeVarUtils.parserTaskID(task_id, params);
        System.out.println(params);
    }
}