package com.cqx.common.utils.jdbc.declare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OracleDeclare<br>
 * <pre>
 *   merge into table_name t1
 *       using (select value1 as field1,value2 as field2,... from dual) t2 on (t1.field1 = t2.field1 and t1.field2 = t2.field2 and ...)
 *   when matched then
 *       update set %s
 *   when not matched then
 *       insert (%s) values(%s)
 * </pre>
 *
 * @author chenqixu
 */
public class OracleDeclare extends AbstractDeclare {

    /**
     * 写入合并
     *
     * @return
     */
    @Override
    protected String setPgdeclareInsertUpdate() {
        StringBuilder sb = new StringBuilder();
        sb.append("merge into " + TAG_TABLE_NAME + " t1")
                .append(" " + TAG_SELECT_SQL + " ")
                .append(" when matched then ")
                .append(" " + TAG_UPDATE_SQL + " ")
                .append(" when not matched then ")
                .append(" " + TAG_INSERT_SQL + " ");
        return sb.toString();
    }

    /**
     * 只写入不更新
     *
     * @return
     */
    @Override
    protected String setPgdeclareInsertOnly() {
        StringBuilder sb = new StringBuilder();
        sb.append("merge into " + TAG_TABLE_NAME + " t1")
                .append(" " + TAG_SELECT_SQL + " ")
                .append(" when not matched then ")
                .append(" " + TAG_INSERT_SQL + " ");
        return sb.toString();
    }

    /**
     * 查询语句
     *
     * @param tableName     表名
     * @param insert_fields 插入字段
     * @param insert_values 插入值
     * @param where_values  where条件
     * @param pks           主键
     * @return
     */
    @Override
    protected String formatSelectSql(String tableName, String insert_fields, String insert_values, String where_values, String[] pks) {
        selectSql = "using (select %s from dual) t2 on (%s)";
        String[] array_insert_fields = insert_fields.split(",", -1);
        String[] array_insert_values = insert_values.split(",", -1);
        StringBuilder dual = new StringBuilder();
        for (int i = 0; i < array_insert_fields.length; i++) {
            dual.append(array_insert_values[i])
                    .append(" as ")
                    .append(array_insert_fields[i]);
            if (i < array_insert_fields.length - 1) {
                dual.append(",");
            }
        }
        String dual_sql = dual.toString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pks.length; i++) {
            sb.append(" t1.")
                    .append(pks[i])
                    .append("=t2.")
                    .append(pks[i]);
            if (i < pks.length - 1) {
                sb.append(" and");
            }
        }
        String join_fields = sb.toString();
        return String.format(selectSql, dual_sql, join_fields);
    }

    /**
     * 更新语句
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param pks
     * @return
     */
    @Override
    protected String formatUpdateSql(String tableName, String insert_fields, String insert_values, String where_values, String[] pks) {
        updateSql = "update set %s";
        String[] _insertArray = insert_fields.split(",", -1);
        String[] _insert_valuesArray = insert_values.split(",", -1);
        Map<String, String> pksMap = new HashMap<>();
        for (String pk : pks) {
            pksMap.put(pk, pk);
        }
        List<String> sets = new ArrayList<>();
        for (int i = 0; i < _insertArray.length; i++) {
            // 更新不能带主键
            if (pksMap.get(_insertArray[i]) == null) {
                sets.add(_insertArray[i] + "=" + _insert_valuesArray[i]);
            }
        }
        StringBuilder set = new StringBuilder();
        for (int j = 0; j < sets.size(); j++) {
            set.append(sets.get(j));
            if (j < sets.size() - 1) {
                set.append(",");
            }
        }
        return String.format(updateSql, set.toString());
    }

    /**
     * 插入语句
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param pks
     * @return
     */
    @Override
    protected String formatInsertSql(String tableName, String insert_fields, String insert_values, String where_values, String[] pks) {
        insertSql = "insert (%s) values(%s)";
        return String.format(insertSql, insert_fields, insert_values);
    }
}
