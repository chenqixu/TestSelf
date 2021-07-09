package com.cqx.common.utils.jdbc.postgresql;

import com.cqx.common.utils.jdbc.MergeEnum;

/**
 * PGDeclare
 *
 * @author chenqixu
 */
public class PGDeclare {
    private final String insertSql = "insert into %s(%s) values(%s)";
    private final String selectSql = "select count(1) into hasval from %s where %s";
    private final String updateSql = "update %s set %s where %s";
    private String pgdeclareInsertOnly;
    private String pgdeclareInsertUpdate;

    private PGDeclare() {
        StringBuilder sb = new StringBuilder();
        sb.append("do")
                .append(" $$")
                .append(" DECLARE")
                .append("  hasval numeric;")
                .append(" BEGIN")
                .append(" %s;")
                .append(" if hasval=0 THEN")
                .append(" %s;")
                .append(" else")
                .append(" %s;")
                .append(" end if;")
                .append(" END;")
                .append(" $$");
        pgdeclareInsertUpdate = sb.toString();

        sb.delete(0, sb.length());
        sb.append("do")
                .append(" $$")
                .append(" DECLARE")
                .append("  hasval numeric;")
                .append(" BEGIN")
                .append(" %s;")
                .append(" if hasval=0 THEN")
                .append(" %s;")
                .append(" end if;")
                .append(" END;")
                .append(" $$");
        pgdeclareInsertOnly = sb.toString();
    }

    public static PGDeclare builder() {
        return new PGDeclare();
    }

    /**
     * 默认只插不更新
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @return
     */
    public String declare(String tableName, String insert_fields, String insert_values, String where_values) {
        return declare(tableName, insert_fields, insert_values, where_values, MergeEnum.MERGE_INTO_ONLY);
    }

    /**
     * 写入合并
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param mergeEnum
     * @return
     */
    public String declare(String tableName, String insert_fields, String insert_values
            , String where_values, MergeEnum mergeEnum) {
        String _insertSql = String.format(insertSql, tableName, insert_fields, insert_values);
        String _selectSql = String.format(selectSql, tableName, where_values);
        String[] _insertArray = insert_fields.split(",", -1);
        String[] _insert_valuesArray = insert_values.split(",", -1);
        StringBuilder set = new StringBuilder();
        for (int i = 0; i < _insertArray.length; i++) {
            set.append(_insertArray[i]).append("=").append(_insert_valuesArray[i]);
            if ((i + 1) < _insertArray.length) {
                set.append(",");
            }
        }
        String _updateSql = String.format(updateSql, tableName, set.toString(), where_values);
        switch (mergeEnum) {
            case MERGE_INTO_UPDATE:
                return String.format(pgdeclareInsertUpdate, _selectSql, _insertSql, _updateSql);
            case MERGE_INTO_ONLY:
            default:
                return String.format(pgdeclareInsertOnly, _selectSql, _insertSql);
        }
    }
}
