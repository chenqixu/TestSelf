package com.cqx.common.utils.jdbc;

import java.util.List;

/**
 * 内部类无法通过newInstance来构造，不方便
 *
 * @author chenqixu
 */
public class MergeBean implements IJDBCUtilCall.IQueryResultV2Bean {
    private String op_type;
    private List<QueryResult> queryResults;
    private List<QueryResult> oldPksResults;

    public MergeBean() {
    }

    @Override
    public String getOp_type() {
        return op_type;
    }

    @Override
    public void setOp_type(String op_type) {
        this.op_type = op_type;
    }

    @Override
    public List<QueryResult> getQueryResults() {
        return queryResults;
    }

    @Override
    public void setQueryResults(List<QueryResult> queryResults) {
        this.queryResults = queryResults;
    }

    @Override
    public List<QueryResult> getOldPksResults() {
        return oldPksResults;
    }

    @Override
    public void setOldPksResults(List<QueryResult> oldPksResults) {
        this.oldPksResults = oldPksResults;
    }
}
