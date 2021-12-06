package com.cqx.common.utils.jdbc.postgresql;

import com.cqx.common.utils.jdbc.DBType;
import com.cqx.common.utils.jdbc.MergeEnum;
import com.cqx.common.utils.jdbc.declare.AbstractDeclare;
import com.cqx.common.utils.jdbc.declare.DeclareHelper;
import org.junit.Test;

public class PGDeclareTest {

    @Test
    public void declare() {
        String tableName = "qry_sell_task";
        String insert_fields = "sell_id,task_id";
        String insert_values = "'sell_001','task_001'";
        String where_values = "sell_id='sell_001'";
        AbstractDeclare declare = DeclareHelper.builder(DBType.POSTGRESQL);
        if (declare != null) {
            String sql = declare.declare(tableName, insert_fields, insert_values
                    , where_values, MergeEnum.MERGE_INTO_UPDATE);
            System.out.println(sql);
        }
    }
}