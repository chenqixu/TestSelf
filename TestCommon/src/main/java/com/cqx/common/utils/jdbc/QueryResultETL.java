package com.cqx.common.utils.jdbc;

import com.cqx.common.utils.system.TimeUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * QueryResultETL
 *
 * @author chenqixu
 */
public class QueryResultETL {
    private static final String PREFIX = "【";
    private static final String SUFFIX = "】";
    private static final String FIELD_SPLIT = "，";
    private static final String FIELD_STATEMENT = "：";
    String ColumnName;// 字段的名称
    String ColumnLabel;// 字段的标签
    int ColumnType = -1;// 字段的java类型id
    String ColumnClassName;// 字段的java类型
    String ColumnTypeName;// 字段的db类型
    Object value;// 字段的具体值
    String REMARKS;// 字段的备注

    /**
     * 把toString的字符串反序列成javabean
     *
     * @param str
     * @return
     */
    public static QueryResult strToBean(String str) {
        QueryResult queryResult = null;
        if (str != null && str.length() > 0) {
            queryResult = new QueryResult();
            String tmp = str;
            //替换掉前缀、后缀
            tmp = tmp.replace(PREFIX, "");
            tmp = tmp.replace(SUFFIX, "");
            String[] qr_array = tmp.split(FIELD_SPLIT, -1);
            for (String qrs : qr_array) {
                String[] field_array = qrs.split(FIELD_STATEMENT, -1);
                if (field_array.length == 2) {
                    String value = field_array[1];
                    switch (field_array[0]) {
                        case "ColumnName":
                            queryResult.setColumnName(value);
                            break;
                        case "ColumnLabel":
                            queryResult.setColumnLabel(value);
                            break;
                        case "ColumnType":
                            queryResult.setColumnType(Integer.valueOf(value));
                            break;
                        case "ColumnClassName":
                            queryResult.setColumnClassName(value);
                            break;
                        case "ColumnTypeName":
                            queryResult.setColumnTypeName(value);
                            break;
                        case "value":
                            switch (queryResult.getColumnClassName()) {
                                case "java.lang.Boolean":
                                    queryResult.setValue(Boolean.valueOf(value));
                                    break;
                                case "int":
                                    queryResult.setValue(Integer.valueOf(value));
                                    break;
                                case "long":
                                    queryResult.setValue(Long.valueOf(value));
                                    break;
                                case "java.sql.Timestamp":
                                    try {
                                        queryResult.setValue(new Timestamp(TimeUtil.getTime(value)));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "java.lang.String":
                                default:
                                    queryResult.setValue(value);
                            }
                            break;
                    }
                }
            }
        }
        return queryResult;
    }

    /**
     * 把List&lt;toString&gt;的list反序列成List&lt;javabean&gt;
     *
     * @param str
     * @return
     */
    public static List<QueryResult> strToBeanList(String str) {
        List<QueryResult> resultList = new ArrayList<>();
        String[] str_array = str.split(",", -1);
        for (String s : str_array) {
            QueryResult qr = strToBean(s.trim());
            if (qr != null) resultList.add(qr);
        }
        return resultList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(PREFIX);
        if (ColumnName != null) sb.append("ColumnName：" + ColumnName);
        if (ColumnLabel != null) {
            if (sb.length() > 0) sb.append(FIELD_SPLIT);
            sb.append("ColumnLabel：" + ColumnLabel);
        }
        if (ColumnType != -1) {
            if (sb.length() > 0) sb.append(FIELD_SPLIT);
            sb.append("ColumnType：" + ColumnType);
        }
        if (ColumnClassName != null) {
            if (sb.length() > 0) sb.append(FIELD_SPLIT);
            sb.append("ColumnClassName：" + ColumnClassName);
        }
        if (ColumnTypeName != null) {
            if (sb.length() > 0) sb.append(FIELD_SPLIT);
            sb.append("ColumnTypeName：" + ColumnTypeName);
        }
        if (REMARKS != null) {
            if (sb.length() > 0) sb.append(FIELD_SPLIT);
            sb.append("REMARKS：" + REMARKS);
        }
        if (value != null) {
            if (sb.length() > 0) sb.append(FIELD_SPLIT);
            sb.append("value：" + value);
        }
        sb.append(SUFFIX);
        return sb.toString();
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

    public String getREMARKS() {
        return REMARKS;
    }

    public void setREMARKS(String REMARKS) {
        this.REMARKS = REMARKS;
    }
}
