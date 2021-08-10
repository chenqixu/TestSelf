package com.cqx.common.utils.jdbc;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * IJDBCUtilCall
 *
 * @author chenqixu
 */
public interface IJDBCUtilCall extends Closeable {

    interface IQueryResultBean {
        String getOp_type();

        List<QueryResult> getQueryResults();
    }

    interface IQueryResultV2Bean extends IQueryResultBean {
        List<QueryResult> getOldPksResults();

        void setOp_type(String op_type);

        void setQueryResults(List<QueryResult> queryResults);

        void setOldPksResults(List<QueryResult> oldPksResults);
    }

    abstract class ICallBack extends ICallBackClose {
        public abstract void call(ResultSet rs) throws SQLException;
    }

    abstract class IBeanCallBack<T> extends ICallBackClose {
        public abstract void call(T t) throws Exception;
    }

    abstract class IQueryResultCallBack extends ICallBackClose {
        public abstract void call(List<QueryResult> queryResults) throws Exception;
    }

    abstract class IConnCallBack {
        public abstract void call(Connection conn) throws Exception;
    }

    class ICallBackClose {
        public void close() {
        }
    }

    class ICallBackReturn {
        public <T> T call() throws Exception {
            return null;
        }

        public void callNotReturn() throws Exception {
        }
    }
}
