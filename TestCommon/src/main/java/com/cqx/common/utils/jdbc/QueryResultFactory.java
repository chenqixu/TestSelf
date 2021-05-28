package com.cqx.common.utils.jdbc;

import java.util.ArrayList;
import java.util.List;

/**
 * QueryResultFactory
 *
 * @author chenqixu
 */
public class QueryResultFactory {
    private static QueryResultFactory queryResultFactory = new QueryResultFactory();
    private List<List<QueryResult>> tList = new ArrayList<>();
    private List<QueryResult> queryResults = new ArrayList<>();
    private List<String> dstFieldsType = new ArrayList<>();

    private QueryResultFactory() {
    }

    public static QueryResultFactory getInstance() {
        return new QueryResultFactory();
    }

    public QueryResultFactory buildQR(String ColumnName, String ColumnClassName, Object value) {
        QueryResult qr = new QueryResult();
        qr.setColumnName(ColumnName);
        qr.setColumnClassName(ColumnClassName);
        qr.setValue(value);
        dstFieldsType.add(ColumnClassName);
        queryResults.add(qr);
        return this;
    }

    public QueryResultFactory toList() {
        tList.add(queryResults);
        return this;
    }

    public List<List<QueryResult>> getData() {
        return tList;
    }

    public List<String> getDstFieldsType() {
        return dstFieldsType;
    }
}
