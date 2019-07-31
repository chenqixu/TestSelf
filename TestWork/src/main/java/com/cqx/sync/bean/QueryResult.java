package com.cqx.sync.bean;

/**
 * QueryResult
 *
 * @author chenqixu
 */
public class QueryResult {
    String ColumnName;
    String ColumnLabel;
    int ColumnType;
    String ColumnClassName;
    String ColumnTypeName;
    Object value;

    @Override
    public String toString() {
        return "ColumnName：" + ColumnName +
                "，ColumnLabel：" + ColumnLabel +
                "，ColumnType：" + ColumnType +
                "，ColumnClassName：" + ColumnClassName +
                "，ColumnTypeName：" + ColumnTypeName +
                "，value：" + value;
    }

    public String getColumnName() {
        return ColumnName;
    }

    public void setColumnName(String columnName) {
        ColumnName = columnName;
    }

    public String getColumnLabel() {
        return ColumnLabel;
    }

    public void setColumnLabel(String columnLabel) {
        ColumnLabel = columnLabel;
    }

    public int getColumnType() {
        return ColumnType;
    }

    public void setColumnType(int columnType) {
        ColumnType = columnType;
    }

    public String getColumnClassName() {
        return ColumnClassName;
    }

    public void setColumnClassName(String columnClassName) {
        ColumnClassName = columnClassName;
    }

    public String getColumnTypeName() {
        return ColumnTypeName;
    }

    public void setColumnTypeName(String columnTypeName) {
        ColumnTypeName = columnTypeName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
