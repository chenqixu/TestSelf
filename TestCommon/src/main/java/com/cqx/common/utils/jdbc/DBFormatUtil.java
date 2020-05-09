package com.cqx.common.utils.jdbc;

/**
 * 时间格式工具，适配oracle和mysql
 * <pre>
 *     示例：
 *     to_char(last_login_time, 'YYYY-MM-DD HH24:MI:SS')
 *          改成：DBFormatUtil.to_char("last_login_time", DBFormatEnum.YYYYMMDDHH24MISS)
 *     to_char(last_login_time,'YYYY-MM-DD HH24:MI:SS') as inure_time
 *          改成：DBFormatUtil.to_char("last_login_time", "inure_time", DBFormatEnum.YYYYMMDDHH24MISS)
 *     sysdate
 *          改成：DBFormatUtil.sysdate()
 *     to_number(to_char(sysdate,'YYYYMM'))
 *          改成：DBFormatUtil.to_number(DBFormatUtil.to_char(DBFormatUtil.sysdate(), DBFormatEnum.YYYYMM))
 * </pre>
 *
 * @author chenqixu
 */
public class DBFormatUtil {
    public static String DB_TYPE = "mysql";

    /**
     * 时间格式化成字符串
     *
     * @param field        字段
     * @param alias        别名
     * @param DBFormatEnum 格式
     * @return
     */
    public static String to_char(String field, String alias, DBFormatEnum DBFormatEnum) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s(%s, '%s')", DBFormatEnum.TOCHAR, field, DBFormatEnum));
        if (alias != null && alias.length() > 0) {
            sb.append(String.format(" as %s ", alias));
        }
        return sb.toString();
    }

    /**
     * 时间格式化成字符串
     *
     * @param field        字段
     * @param DBFormatEnum 格式
     * @return
     */
    public static String to_char(String field, DBFormatEnum DBFormatEnum) {
        return to_char(field, null, DBFormatEnum);
    }

    /**
     * 字符串格式化成时间
     *
     * @param field        字段
     * @param alias        别名
     * @param DBFormatEnum 格式
     * @return
     */
    public static String to_date(String field, String alias, DBFormatEnum DBFormatEnum) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s(%s, '%s')", DBFormatEnum.TODATE, field, DBFormatEnum));
        if (alias != null && alias.length() > 0) {
            sb.append(String.format(" as %s ", alias));
        }
        return sb.toString();
    }

    /**
     * 字符串格式化成时间
     *
     * @param field        字段
     * @param DBFormatEnum 格式
     * @return
     */
    public static String to_date(String field, DBFormatEnum DBFormatEnum) {
        return to_date(field, null, DBFormatEnum);
    }

    /**
     * 返回系统时间
     *
     * @return
     */
    public static String sysdate() {
        return DBFormatEnum.SYSDATE.toString();
    }

    /**
     * 格式化成数值
     *
     * @param field 字段
     * @param alias 别名
     * @return
     */
    public static String to_number(String field, String alias) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(DBFormatEnum.TONUMBER_INT.toString(), field));
        if (alias != null && alias.length() > 0) {
            sb.append(String.format(" as %s ", alias));
        }
        return sb.toString();
    }

    /**
     * 格式化成数值
     *
     * @param field 字段
     * @return
     */
    public static String to_number(String field) {
        return to_number(field, null);
    }

    public static void setOracleDbType() {
        DB_TYPE = "oracle";
    }

    public static void setMysqlDbType() {
        DB_TYPE = "mysql";
    }
}
