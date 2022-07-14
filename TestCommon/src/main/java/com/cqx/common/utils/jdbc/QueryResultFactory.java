package com.cqx.common.utils.jdbc;

import com.cqx.common.utils.Utils;
import com.cqx.common.utils.jdbc.IJDBCUtilCall.IQueryResultBean;
import com.cqx.common.utils.jdbc.IJDBCUtilCall.IQueryResultV2Bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * QueryResultFactory
 *
 * @author chenqixu
 */
public class QueryResultFactory {
    private List<IQueryResultBean> iQueryResultBeanList = new ArrayList<>();
    private List<List<QueryResult>> tList = new ArrayList<>();
    private List<QueryResult> queryResults = new ArrayList<>();
    private List<QueryResult> oldPksResults = new ArrayList<>();
    private List<String> dstFieldsType = new ArrayList<>();
    private List<String> dstFieldsTypeResult = new ArrayList<>();
    private Class<? extends IQueryResultBean> tClass;

    private QueryResultFactory() {
    }

    private QueryResultFactory(Class<? extends IQueryResultBean> tClass) {
        this.tClass = tClass;
    }

    public static QueryResultFactory getInstance() {
        return new QueryResultFactory();
    }

    public static QueryResultFactory getInstance(Class<? extends IQueryResultBean> tClass) {
        return new QueryResultFactory(tClass);
    }

    public QueryResultFactory buildQR(String ColumnName, String ColumnClassName, Object value) {
        QueryResult qr = new QueryResult();
        qr.setColumnName(ColumnName);
        qr.setColumnLabel(ColumnName);
        qr.setColumnClassName(ColumnClassName);
        qr.setValue(stringToObj(ColumnClassName, value));
        dstFieldsType.add(ColumnClassName);
        queryResults.add(qr);
        return this;
    }

    public QueryResultFactory buildOldPks(String ColumnName, String ColumnClassName, Object value) {
        QueryResult qr = new QueryResult();
        qr.setColumnName(ColumnName);
        qr.setColumnLabel(ColumnName);
        qr.setColumnClassName(ColumnClassName);
        qr.setValue(value);
        oldPksResults.add(qr);
        return this;
    }

    public QueryResultFactory toList() {
        tList.add(queryResults);
        dstFieldsTypeResult = new ArrayList<>(dstFieldsType);
        queryResults = new ArrayList<>();
        dstFieldsType = new ArrayList<>();
        return this;
    }

    public QueryResultFactory toQRBeanList(String op_type) {
        try {
            IQueryResultV2Bean t = (IQueryResultV2Bean) tClass.newInstance();
            t.setOp_type(op_type);
            t.setQueryResults(queryResults);
            t.setOldPksResults(oldPksResults);
            iQueryResultBeanList.add(t);
            dstFieldsTypeResult = new ArrayList<>(dstFieldsType);
            queryResults = new ArrayList<>();
            oldPksResults = new ArrayList<>();
            dstFieldsType = new ArrayList<>();
            return this;
        } catch (InstantiationException | IllegalAccessException e) {
            // 内部类就无法初始化，因为要传父类，就不通用了
            throw new RuntimeException(e);
        }
    }

    public List<List<QueryResult>> getData() {
        return tList;
    }

    public List<IQueryResultBean> getQRBeanData() {
        return iQueryResultBeanList;
    }

    public List<String> getDstFieldsType() {
        return dstFieldsTypeResult;
    }

    /**
     * 如果数据实际值类型和传入的类型不一致，尝试toString后进行类型转换
     *
     * @param fieldType 传入的字段类型
     * @param value     数据实际值
     * @return 数据实际值真实类型的对象
     */
    private Object stringToObj(String fieldType, Object value) {
        Object ret = value;
        if (value != null) {
            String valueStr = value.toString();
            String valueType = value.getClass().getName();
            if (!valueType.equals(fieldType) && valueStr.length() > 0) {
                try {
                    switch (fieldType) {
                        case "java.lang.Integer":
                        case "int":
                            ret = Integer.valueOf(valueStr);
                            break;
                        case "java.lang.Long":
                        case "long":
                            ret = Long.valueOf(valueStr);
                            break;
                        case "java.math.BigDecimal":
                            ret = new BigDecimal(valueStr);
                            break;
                        case "java.sql.Timestamp":
                            ret = new Timestamp(Utils.getTime(valueStr));
                            break;
                        case "java.sql.Time":
                            ret = new Time(Utils.getTime(valueStr));
                            break;
                        case "java.sql.Date":
                        case "java.util.Date":
                            ret = new Date(Utils.getTime(valueStr));
                            break;
                        case "java.lang.String":
                        case "java.sql.Clob":
                        default:
                            ret = valueStr;
                            break;
                    }
                } catch (ParseException e) {
                    throw new NullPointerException(e.getMessage());
                }
            }
        }
        return ret;
    }
}
