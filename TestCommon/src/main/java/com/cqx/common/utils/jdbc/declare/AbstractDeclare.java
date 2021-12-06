package com.cqx.common.utils.jdbc.declare;

import com.cqx.common.utils.jdbc.MergeEnum;

/**
 * AbstractDeclare
 *
 * @author chenqixu
 */
public abstract class AbstractDeclare {
    protected final String insertSql = "insert into %s(%s) values(%s)";
    protected final String selectSql = "select count(1) into hasval from %s where %s";
    protected final String updateSql = "update %s set %s where %s";
    protected String pgdeclareInsertOnly;
    protected String pgdeclareInsertUpdate;

    public AbstractDeclare() {
        pgdeclareInsertUpdate = setPgdeclareInsertUpdate();
        pgdeclareInsertOnly = setPgdeclareInsertOnly();
    }

    /**
     * 没有值就写入，有值就更新
     *
     * @return
     */
    protected abstract String setPgdeclareInsertUpdate();

    /**
     * 没有值就写入，有值不做处理
     *
     * @return
     */
    protected abstract String setPgdeclareInsertOnly();

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
