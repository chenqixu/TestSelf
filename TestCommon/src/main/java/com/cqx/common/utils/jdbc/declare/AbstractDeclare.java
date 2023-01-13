package com.cqx.common.utils.jdbc.declare;

import com.cqx.common.utils.jdbc.FiledUtil;
import com.cqx.common.utils.jdbc.MergeEnum;

/**
 * AbstractDeclare
 *
 * @author chenqixu
 */
public abstract class AbstractDeclare {
    protected final String TAG_TABLE_NAME = "[table_name]";
    protected final String TAG_SELECT_SQL = "[select_sql]";
    protected final String TAG_UPDATE_SQL = "[update_sql]";
    protected final String TAG_INSERT_SQL = "[insert_sql]";

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
     * 更新插入语句
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param pks
     * @return
     */
    protected abstract String formatInsertSql(String tableName, String insert_fields, FiledUtil insert_values, String where_values, String[] pks);

    /**
     * 更新修改语句
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param pks
     * @return
     */
    protected abstract String formatUpdateSql(String tableName, String insert_fields, FiledUtil insert_values, String where_values, String[] pks);

    /**
     * 更新查询语句
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param pks
     * @return
     */
    protected abstract String formatSelectSql(String tableName, String insert_fields, FiledUtil insert_values, String where_values, String[] pks);

    /**
     * 默认只插不更新
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @return
     */
    public String declare(String tableName, String insert_fields, FiledUtil insert_values, String where_values) {
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
    public String declare(String tableName, String insert_fields, FiledUtil insert_values
            , String where_values, MergeEnum mergeEnum) {
        return declare(tableName, insert_fields, insert_values, where_values, null, mergeEnum);
    }

    /**
     * 默认只插不更新，带pks
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param pks
     * @return
     */
    public String declare(String tableName, String insert_fields, FiledUtil insert_values, String where_values, String[] pks) {
        return declare(tableName, insert_fields, insert_values, where_values, pks, MergeEnum.MERGE_INTO_ONLY);
    }

    /**
     * 写入合并，带pks
     *
     * @param tableName
     * @param insert_fields
     * @param insert_values
     * @param where_values
     * @param pks
     * @param mergeEnum
     * @return
     */
    public String declare(String tableName, String insert_fields, FiledUtil insert_values
            , String where_values, String[] pks, MergeEnum mergeEnum) {
        String pgdeclareInsertUpdate = setPgdeclareInsertUpdate();
        String pgdeclareInsertOnly = setPgdeclareInsertOnly();
        String _insertSql = formatInsertSql(tableName, insert_fields, insert_values, where_values, pks);
        String _selectSql = formatSelectSql(tableName, insert_fields, insert_values, where_values, pks);
        String _updateSql = formatUpdateSql(tableName, insert_fields, insert_values, where_values, pks);
        switch (mergeEnum) {
            case MERGE_INTO_UPDATE:
                pgdeclareInsertUpdate = pgdeclareInsertUpdate.replace(TAG_TABLE_NAME, tableName);
                pgdeclareInsertUpdate = pgdeclareInsertUpdate.replace(TAG_SELECT_SQL, _selectSql);
                pgdeclareInsertUpdate = pgdeclareInsertUpdate.replace(TAG_INSERT_SQL, _insertSql);
                pgdeclareInsertUpdate = pgdeclareInsertUpdate.replace(TAG_UPDATE_SQL, _updateSql);
                return pgdeclareInsertUpdate;
            case MERGE_INTO_ONLY:
            default:
                pgdeclareInsertOnly = pgdeclareInsertOnly.replace(TAG_TABLE_NAME, tableName);
                pgdeclareInsertOnly = pgdeclareInsertOnly.replace(TAG_SELECT_SQL, _selectSql);
                pgdeclareInsertOnly = pgdeclareInsertOnly.replace(TAG_INSERT_SQL, _insertSql);
                return pgdeclareInsertOnly;
        }
    }
}
