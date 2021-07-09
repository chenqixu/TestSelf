package com.cqx.common.utils.jdbc.postgresql;

import com.cqx.common.utils.jdbc.MergeEnum;
import org.junit.Test;

public class PGDeclareTest {

    @Test
    public void declare() {
        String tableName = "qry_sell_task";
        String insert_fields = "sell_id,task_id";
        String insert_values = "'sell_001','task_001'";
        String where_values = "sell_id='sell_001'";
        String sql = PGDeclare.builder().declare(tableName, insert_fields, insert_values
                , where_values, MergeEnum.MERGE_INTO_UPDATE);
        System.out.println(sql);
    }
}