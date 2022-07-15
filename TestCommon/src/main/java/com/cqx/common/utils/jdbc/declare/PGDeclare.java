package com.cqx.common.utils.jdbc.declare;

import com.cqx.common.utils.jdbc.FiledUtil;

/**
 * PGDeclare
 *
 * @author chenqixu
 */
public class PGDeclare extends AbstractDeclare {

    /**
     * 写入合并
     *
     * @return
     */
    @Override
    protected String setPgdeclareInsertUpdate() {
        StringBuilder sb = new StringBuilder();
        sb.append("do")
                .append(" $$")
                .append(" DECLARE")
                .append("  hasval numeric;")
                .append(" BEGIN")
                .append(" " + TAG_SELECT_SQL + ";")
                .append(" if hasval=0 THEN")
                .append(" " + TAG_INSERT_SQL + ";")
                .append(" else")
                .append(" " + TAG_UPDATE_SQL + ";")
                .append(" end if;")
                .append(" END;")
                .append(" $$");
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
        sb.append("do")
                .append(" $$")
                .append(" DECLARE")
                .append("  hasval numeric;")
                .append(" BEGIN")
                .append(" " + TAG_SELECT_SQL + ";")
                .append(" if hasval=0 THEN")
                .append(" " + TAG_INSERT_SQL + ";")
                .append(" end if;")
                .append(" END;")
                .append(" $$");
        return sb.toString();
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
    protected String formatInsertSql(String tableName, String insert_fields, FiledUtil insert_values, String where_values, String[] pks) {
        return String.format(insertSql, tableName, insert_fields, insert_values);
    }

    /**
     * 修改语句
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param pks
     * @return
     */
    @Override
    protected String formatUpdateSql(String tableName, String insert_fields, FiledUtil insert_values, String where_values, String[] pks) {
        String[] _insertArray = insert_fields.split(",", -1);
        String[] _insert_valuesArray = insert_values.getValsArray();
        StringBuilder set = new StringBuilder();
        for (int i = 0; i < _insertArray.length; i++) {
            set.append(_insertArray[i]).append("=").append(_insert_valuesArray[i]);
            if ((i + 1) < _insertArray.length) {
                set.append(",");
            }
        }
        return String.format(updateSql, tableName, set.toString(), where_values);
    }

    /**
     * 查询语句
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param pks
     * @return
     */
    @Override
    protected String formatSelectSql(String tableName, String insert_fields, FiledUtil insert_values, String where_values, String[] pks) {
        return String.format(selectSql, tableName, where_values);
    }
}
