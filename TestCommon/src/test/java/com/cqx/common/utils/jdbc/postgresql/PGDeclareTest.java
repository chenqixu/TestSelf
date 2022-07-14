package com.cqx.common.utils.jdbc.postgresql;

import com.cqx.common.test.TestBase;
import com.cqx.common.utils.jdbc.*;
import com.cqx.common.utils.jdbc.declare.AbstractDeclare;
import com.cqx.common.utils.jdbc.declare.DeclareHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PGDeclareTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(PGDeclareTest.class);

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

            sql = declare.declare(tableName, insert_fields, insert_values
                    , where_values, MergeEnum.MERGE_INTO_ONLY);
            System.out.println(sql);
        }
    }

    @Test
    public void declareOracle() {
        String tableName = "qry_sell_task";
        String insert_fields = "task_id,sell_place,sell_id";
        String insert_values = "'task_001','fuzhou','sell_001'";
        String where_values = "sell_id='sell_001'";
        String[] pks = {"sell_id"};
        AbstractDeclare declare = DeclareHelper.builder(DBType.ORACLE);
        if (declare != null) {
            String sql = declare.declare(tableName, insert_fields, insert_values
                    , where_values, pks, MergeEnum.MERGE_INTO_UPDATE);
            System.out.println(sql);

            sql = declare.declare(tableName, insert_fields, insert_values
                    , where_values, pks, MergeEnum.MERGE_INTO_ONLY);
            System.out.println(sql);
        }
    }

    @Test
    public void oracleInsertUpdate() throws IOException, SQLException {
        JDBCUtil jdbcUtil = null;
        try {
            Map params = getParam("jdbc.yaml");
            ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
            DBBean adbBean = paramsParserUtil.getBeanMap().get("oracle242mktbiBean");
            jdbcUtil = new JDBCUtil(adbBean);

            List<List<QueryResult>> list = QueryResultFactory.getInstance()
                    .buildQR("task_id", "java.lang.String", "task_002")
                    .buildQR("sell_place", "java.lang.String", "xiamen")
                    .buildQR("sell_id", "java.lang.String", "sell_001")
                    .toList()
                    .getData();

            List<String> op_types = new ArrayList<>();
            op_types.add("i");
            String table = "qry_sell_task";
            String[] fields = {"task_id", "sell_place"};
            String[] fields_type = {"java.lang.String", "java.lang.String"};
            String[] pks = {"sell_id"};
            String[] pks_type = {"java.lang.String"};
            List<Integer> rets = jdbcUtil.executeBatch(op_types, list, table, fields, fields_type
                    , pks, pks_type, false, MergeEnum.MERGE_INTO_UPDATE);
            for (int ret : rets) {
                logger.info("ret：{}", ret);
            }
        } finally {
            if (jdbcUtil != null) jdbcUtil.close();
        }
    }
}