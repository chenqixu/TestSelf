package com.cqx.common.utils.exception;

import org.junit.Test;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ExceptionTest
 *
 * @author chenqixu
 */
public class ExceptionTest {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionTest.class);
    private static final long NO_CURRENT_THREAD = -1L;
    private final AtomicLong currentThread = new AtomicLong(NO_CURRENT_THREAD);

    @Test
    public void printErr() {
        PgJDBCTest pgJDBCTest = new PgJDBCTest();
        JDBCTest jdbcTest = new JDBCTest(pgJDBCTest);
        try {
            jdbcTest.executeBatch();
        } catch (SQLException e) {
            errorPrint("入ADB执行", e);
//            logger.error("入ADB执行异常，异常数据pks和current_ts");
        }
    }

    @Test
    public void kafkaClose() {
        long threadId = Thread.currentThread().getId();
        logger.info("threadId：{}，currentThread.get()：{}，equal：{}"
                , threadId, currentThread.get(), threadId != currentThread.get());
        logger.info("!compareAndSet：{}", !currentThread.compareAndSet(NO_CURRENT_THREAD, threadId));
        if (threadId != currentThread.get() && !currentThread.compareAndSet(NO_CURRENT_THREAD, threadId)) {
            logger.error("KafkaConsumer is not safe for multi-threaded access");
        }
    }

    @Test
    public void classNotFindTest() {
        String className = "com.t.t.t.t.t.t1";
        try {
            try {
                Class headerCls = Class.forName(className);
            } catch (ClassNotFoundException e) {
//                Exception e1 = new Exception(e);
//                throw e1;
//                RuntimeException npe = new RuntimeException(String.format("header构造异常！使用的构造类：%s", className));
//                npe.addSuppressed(e);
//                throw npe;
                RuntimeExceptionTest ret = new RuntimeExceptionTest(
                        String.format("header构造异常！使用的构造类：%s", className), e);
//                ret.addSuppressed(e);
                throw ret;
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
//            errorPrint("[test]", ex);
        }
    }

    private void errorPrint(String msg, Exception e) {
//        logger.warn("{}-异常，cause：{}", msg, e.getCause().toString());
        logger.warn("{}-异常，message：{}", msg, e.getMessage());
        StringWriter stack = new StringWriter();
        e.printStackTrace(new PrintWriter(stack));
        logger.warn("{}-异常，堆栈信息：{}", msg, stack.toString());
    }

    class JDBCTest {
        private PgJDBCTest pgJDBCTest;

        public JDBCTest(PgJDBCTest pgJDBCTest) {
            this.pgJDBCTest = pgJDBCTest;
        }

        public void executeBatch() throws SQLException {
            // get connection
            try {
                logger.info("execute-before");
                // execute
                pgJDBCTest.execute();
                logger.info("execute-end");
            } catch (Exception e) {
                logger.info("catchException-before");
                logger.error("JDBCUtilException：executeBatch异常，" + e.getMessage(), e);
                // rollback
                logger.info("rollback-before");
                try {
                    pgJDBCTest.rollback();
                } catch (PSQLException pe) {
                    e.addSuppressed(pe);
                }
                logger.info("rollback-end");
                logger.info("catchException-end");
                throw e;
            } finally {
                // close connection
                logger.info("closeConn-before");
                closeConn();
                logger.info("closeConn-end");
            }
        }

        public void closeConn() {
            try {
                pgJDBCTest.close();
            } catch (SQLException e) {
                logger.error("JDBCUtilException：关闭Connection异常，" + e.getMessage(), e);
            }
        }
    }

    class PgJDBCTest {

        public void execute() throws PSQLException {
            throw new PSQLException("An I/O error occurred while sending to the backend."
                    , null
                    , new EOFException("null"));
        }

        public void rollback() throws PSQLException {
            throw new PSQLException("This connection has been closed."
                    , null
                    , null);
        }

        public void close() throws SQLException {
            throw new SQLException("Already closed.");
        }
    }
}
