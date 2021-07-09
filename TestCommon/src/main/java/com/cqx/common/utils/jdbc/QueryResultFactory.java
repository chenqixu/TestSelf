package com.cqx.common.utils.jdbc;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryResultFactory
 *
 * @author chenqixu
 */
public class QueryResultFactory {
    private List<List<QueryResult>> tList = new ArrayList<>();
    private List<QueryResult> queryResults = new ArrayList<>();
    private List<String> dstFieldsType = new ArrayList<>();
    private List<String> dstFieldsTypeResult = new ArrayList<>();

    private QueryResultFactory() {
    }

    public static QueryResultFactory getInstance() {
        return new QueryResultFactory();
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

    public QueryResultFactory toList() {
        tList.add(queryResults);
        dstFieldsTypeResult = new ArrayList<>(dstFieldsType);
        queryResults = new ArrayList<>();
        dstFieldsType = new ArrayList<>();
        return this;
    }

    public List<List<QueryResult>> getData() {
        return tList;
    }

    public List<String> getDstFieldsType() {
        return dstFieldsTypeResult;
    }
}
