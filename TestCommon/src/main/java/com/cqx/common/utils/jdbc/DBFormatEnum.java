package com.cqx.common.utils.jdbc;

/**
 * DateFormatEnum
 *
 * @author chenqixu
 */
public enum DBFormatEnum {
    YYYYMMDDHH24MISS(JDBCUtil.DB_TYPE, "YYYY-MM-DD HH24:MI:SS", "%Y-%m-%d %H:%i:%s"),
    YYYYMM(JDBCUtil.DB_TYPE, "YYYYMM", "%Y%m"),
    TOCHAR(JDBCUtil.DB_TYPE, "TO_CHAR", "DATE_FORMAT"),
    TODATE(JDBCUtil.DB_TYPE, "TO_DATE", "STR_TO_DATE"),
    TONUMBER_INT(JDBCUtil.DB_TYPE, "TO_NUMBER(%s)", "CAST(%s AS UNSIGNED INT)"),
    SYSDATE(JDBCUtil.DB_TYPE, "SYSDATE", "CURRENT_TIMESTAMP"),
    ;
    private String format;
    private String db_type;

    DBFormatEnum(String db_type, String oracle_format, String mysql_format) {
        this.db_type = db_type;
        if (db_type.equals("oracle")) {
            this.format = oracle_format;
        } else if (db_type.equals("mysql")) {
            this.format = mysql_format;
        } else {
            throw new NullPointerException("数据库类型不正确，请检查！");
        }
    }

    public String getFormat() {
        return format;
    }

    public String getDb_type() {
        return db_type;
    }

    public String toString() {
        return format;
    }
}
