package com.cqx.common.utils.jdbc;

/**
 * QueryResult<br>
 * &nbsp;&nbsp;ColumnName：名称<br>
 * &nbsp;&nbsp;ColumnLabel：标签<br>
 * &nbsp;&nbsp;ColumnType：类型<br>
 * &nbsp;&nbsp;ColumnClassName：java类型名称<br>
 * &nbsp;&nbsp;ColumnTypeName：类型名称<br>
 * &nbsp;&nbsp;value：值<br>
 * &nbsp;&nbsp;ColumnDisplaySize：显示大小<br>
 * &nbsp;&nbsp;Precision：精度<br>
 * &nbsp;&nbsp;Scale：比例<br>
 * &nbsp;&nbsp;SchemaName：模式名称<br>
 * &nbsp;&nbsp;CatalogName：目录名称<br>
 * &nbsp;&nbsp;REMARKS：备注<br>
 *
 * @author chenqixu
 */
public class QueryResult {
    private String ColumnName;
    private String ColumnLabel;
    private int ColumnType;
    private String ColumnClassName;
    private String ColumnTypeName;
    private Object value;
    private int ColumnDisplaySize;
    private int Precision;
    private int Scale;
    private String SchemaName;
    private String CatalogName;
    private String REMARKS;

    @Override
    public String toString() {
        return "ColumnName：" + ColumnName +
                "，ColumnLabel：" + ColumnLabel +
                "，ColumnType：" + ColumnType +
                "，ColumnClassName：" + ColumnClassName +
                "，ColumnTypeName：" + ColumnTypeName +
                "，ColumnDisplaySize：" + ColumnDisplaySize +
                "，Precision：" + Precision +
                "，Scale：" + Scale +
                "，SchemaName：" + SchemaName +
                "，CatalogName：" + CatalogName +
                "，REMARKS：" + REMARKS +
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

    public int getColumnDisplaySize() {
        return ColumnDisplaySize;
    }

    public void setColumnDisplaySize(int columnDisplaySize) {
        ColumnDisplaySize = columnDisplaySize;
    }

    public int getPrecision() {
        return Precision;
    }

    public void setPrecision(int precision) {
        Precision = precision;
    }

    public int getScale() {
        return Scale;
    }

    public void setScale(int scale) {
        Scale = scale;
    }

    public String getSchemaName() {
        return SchemaName;
    }

    public void setSchemaName(String schemaName) {
        SchemaName = schemaName;
    }

    public String getCatalogName() {
        return CatalogName;
    }

    public void setCatalogName(String catalogName) {
        CatalogName = catalogName;
    }

    public String getREMARKS() {
        return REMARKS;
    }

    public void setREMARKS(String REMARKS) {
        this.REMARKS = REMARKS;
    }
}
