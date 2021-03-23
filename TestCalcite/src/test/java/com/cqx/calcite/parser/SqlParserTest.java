package com.cqx.calcite.parser;

import org.junit.Test;

import javax.naming.OperationNotSupportedException;

public class SqlParserTest {

    @Test
    public void parser() throws OperationNotSupportedException {
        SqlParser sqlParser = new SqlParser();
        String sql = "select msisdn, UPDATE_LAC_CI as lac_ci,TO_CHAR(UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS')as UPDATE_TIME from posi_scene_footprint where out_time is null and RECORD_DATE in (20201222,20201221) and msisdn in ('18850528303')";
//        sql = "op_type not in ('I','U')";
//        sql = "out_time is null and RECORD_DATE in (20201222,20201221) and msisdn in ('18850528303')";
        sql = "a =1 or b= 2 or c=3";
        sql = "a=1 and b=2 or c=3";
        sqlParser.parser(sql);
    }
}