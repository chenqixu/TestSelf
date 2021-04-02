package com.cqx.common.utils.jdbc;

import java.io.Closeable;
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

    abstract class IQueryResultCallBack extends ICallBackClose {
        public abstract void call(List<QueryResult> queryResults) throws Exception;
    }

    abstract class IBeanCallBack<T> extends ICallBackClose {
        public abstract void call(T t) throws Exception;
    }

    abstract class ICallBack extends ICallBackClose {
        public abstract void call(ResultSet rs) throws SQLException;
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
