package com.cqx.common.utils.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JDBC间隔重试，不影响上游数据下发（主要是导致jstorm开启反压）
 *
 * @author chenqixu
 */
public class JDBCRetryUtil extends JDBCUtil {
    private static final Logger logger = LoggerFactory.getLogger(JDBCRetryUtil.class);
    private long checkTime;//检查时间
    private AtomicLong errorCnt = new AtomicLong(0L);//错误计数器
    private long checkTimeInterval;//检查时间间隔
    private long evaluationCnt;//允许的错误次数
    private AtomicBoolean first = new AtomicBoolean(true);
    private AtomicBoolean lock = new AtomicBoolean(true);
    private ICallBackReturnProxy iCallBackReturnProxy;

    /**
     * 默认间隔10分钟，允许累加10次错误
     *
     * @param dbBean
     */
    public JDBCRetryUtil(DBBean dbBean) {
        this(dbBean, 10 * 60 * 1000L, 10L);
    }

    public JDBCRetryUtil(DBBean dbBean, long checkTimeInterval, long evaluationCnt) {
        super(dbBean);
        this.checkTimeInterval = checkTimeInterval;
        this.evaluationCnt = evaluationCnt;
        this.iCallBackReturnProxy = new ICallBackReturnProxy();
    }

    /**
     * 重载抛出，在这里进行异常计数
     *
     * @return
     */
    @Override
    public boolean isThrow() {
        logger.warn("errorCnt++：{}", errorCnt.incrementAndGet());
        return super.isThrow();
    }

    /**
     * 主要实现
     * <p>判断：错误计数 大于等于 允许的错误次数
     * <ul>
     * <li>真
     * <ul>
     * <li>跳过执行</li>
     * <li>保证同时只有一个线程操作</li>
     * <li>判断：(当前时间 - 上次检查时间) 是否大于等于 检查时间间隔
     * <ul>
     * <li>真：重置检查时间，重置错误计数</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * <li>假：正常执行</li>
     * </ul>
     *
     * @return
     */
    private boolean checkAndReset() {
        //检查时间初始化
        firstSet();
        //保证同时只有一个线程操作
        if (lock.getAndSet(false)) {
            //判断 (当前时间 - 上次检查时间) 是否大于等于 检查时间间隔
            boolean flag = System.currentTimeMillis() - checkTime >= checkTimeInterval;
            if (flag) {
                //重置检查时间
                setCurrentTime();
                //重置错误计数
                errorCnt.set(0L);
                logger.info("Reset errorCnt");
            }
            lock.set(true);
        }
        //错误计数 大于等于 允许的错误次数
        if (errorCnt.get() >= evaluationCnt) {
            //跳过执行
            logger.warn("Skip execute，because errorCnt：{}", errorCnt.get());
            return false;
        }
        return true;
    }

    /**
     * 设置检查时间为当前
     */
    private void setCurrentTime() {
        checkTime = System.currentTimeMillis();
    }

    /**
     * 检查时间初始化
     */
    private void firstSet() {
        if (first.getAndSet(false)) setCurrentTime();
    }

    @Override
    public List<List<QueryResult>> executeQuery(String sql) {
        return iCallBackReturnProxy.done(new ICallBackReturn() {
            @Override
            public List<List<QueryResult>> call() throws Exception {
                return JDBCRetryUtil.super.executeQuery(sql);
            }
        });
    }

    @Override
    public void executeQuery(String sql, ICallBack iCallBack) {
        iCallBackReturnProxy.doneNotReturn(new ICallBackReturn() {
            @Override
            public void callNotReturn() throws Exception {
                JDBCRetryUtil.super.executeQuery(sql, iCallBack);
            }
        });
    }

    @Override
    public List<Integer> executeBatch(List<String> sqls) {
        return iCallBackReturnProxy.done(new ICallBackReturn() {
            @Override
            public List<Integer> call() throws Exception {
                return JDBCRetryUtil.super.executeBatch(sqls);
            }
        });
    }

    private class ICallBackReturnProxy {
        /**
         * 有返回值
         *
         * @param iCallBackReturn
         * @param <T>
         * @return
         */
        <T> T done(ICallBackReturn iCallBackReturn) {
            T t = null;
            try {
                if (checkAndReset()) t = iCallBackReturn.call();
            } catch (Exception e) {
                //在super中已经进行error打印，这里就不做输出
            }
            return t;
        }

        /**
         * 无返回值
         *
         * @param iCallBackReturn
         */
        void doneNotReturn(ICallBackReturn iCallBackReturn) {
            try {
                if (checkAndReset()) iCallBackReturn.callNotReturn();
            } catch (Exception e) {
                //在super中已经进行error打印，这里就不做输出
            }
        }
    }
}
