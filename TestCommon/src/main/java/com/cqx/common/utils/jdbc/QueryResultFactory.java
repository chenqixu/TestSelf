package com.cqx.common.utils.jdbc;

import com.cqx.common.utils.jdbc.IJDBCUtilCall.IQueryResultBean;
import com.cqx.common.utils.jdbc.IJDBCUtilCall.IQueryResultV2Bean;

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
        qr.setValue(value);
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
}
