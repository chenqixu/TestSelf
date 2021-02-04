package com.cqx.common.utils.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * IJDBCUtilCall
 *
 * @author chenqixu
 */
public interface IJDBCUtilCall {

    interface ICallBack {
        void call(ResultSet rs) throws SQLException;
    }

    interface IBeanCallBack<T> {
        void call(T t) throws Exception;
    }

    interface IQueryResultCallBack {
        void call(List<QueryResult> queryResults) throws Exception;
    }

    interface IQueryResultBean {
        String getOp_type();

        List<QueryResult> getQueryResults();
    }

    class ICallBackReturn {
        public <T> T call() throws Exception {
            return null;
        }

        public void callNotReturn() throws Exception {
        }
    }
}
