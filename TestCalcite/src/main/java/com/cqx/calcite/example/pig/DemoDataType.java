package com.cqx.calcite.example.pig;

import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.pig.data.DataType;

/**
 * DemoDataType
 *
 * @author chenqixu
 */
public enum DemoDataType {
    CHARARRAY(DataType.CHARARRAY, SqlTypeName.VARCHAR);

    private byte pigType; // Pig defines types using bytes
    private SqlTypeName sqlType;

    DemoDataType(byte pigType, SqlTypeName sqlType) {
        this.pigType = pigType;
        this.sqlType = sqlType;
    }

    public static DemoDataType valueOf(byte pigType) {
        for (DemoDataType pigDataType : values()) {
            if (pigDataType.pigType == pigType) {
                return pigDataType;
            }
        }
        throw new IllegalArgumentException(
                "Pig data type " + DataType.findTypeName(pigType) + " is not supported");
    }

    public static DemoDataType valueOf(SqlTypeName sqlType) {
        for (DemoDataType pigDataType : values()) {
            if (pigDataType.sqlType == sqlType) {
                return pigDataType;
            }
        }
        throw new IllegalArgumentException("SQL data type " + sqlType + " is not supported");
    }

    public byte getPigType() {
        return pigType;
    }

    public SqlTypeName getSqlType() {
        return sqlType;
    }
}
