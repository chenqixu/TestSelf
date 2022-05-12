package com.cqx.common.utils.jdbc;

import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.BaseRunable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * StandbyJDBCUtil
 *
 * @author chenqixu
 */
public class StandbyJDBCUtil {
    private static final Logger logger = LoggerFactory.getLogger(StandbyJDBCUtil.class);
    private final Object lock = new Object();
    private LinkedBlockingQueue<StandbyBean> standbyBeanQueue = new LinkedBlockingQueue<>(100);
    private DBBean mainDbBean;
    private DBBean standbyDbBean;
    private JDBCUtil main;
    private JDBCUtil standby;
    private AtomicBoolean mainIsOver = new AtomicBoolean(false);
    private AtomicBoolean standbyIsOver = new AtomicBoolean(false);
    private Thread standbyExecThread;
    private ExecStandbyRunable execStandbyRunable;

    private StandbyJDBCUtil() {
        execStandbyRunable = new ExecStandbyRunable();
        standbyExecThread = new Thread(execStandbyRunable);
        standbyExecThread.start();
    }

    public static StandbyJDBCUtil builder() {
        return new StandbyJDBCUtil();
    }

    public StandbyJDBCUtil setMain(DBBean dbBean, int MaxActive, int MinIdle, int MaxIdle) {
        main = new JDBCUtil(dbBean, MaxActive, MinIdle, MaxIdle);
        mainDbBean = dbBean;
        return this;
    }

    public StandbyJDBCUtil setStandby(DBBean dbBean, int MaxActive, int MinIdle, int MaxIdle) {
        standby = new JDBCUtil(dbBean, MaxActive, MinIdle, MaxIdle);
        standbyDbBean = dbBean;
        return this;
    }

    private void checkCfg() {
        if (main == null) throw new NullPointerException("主库配置为空，请检查！");
        if (standby == null) throw new NullPointerException("备库配置为空，请检查！");
    }

    /**
     * 通过Validation_query检查数据库是否可用<br>
     * 数据库不可用返回false
     *
     * @param isOver
     * @param jdbcUtil
     * @param dbBean
     * @return
     */
    private boolean checkDB(AtomicBoolean isOver, JDBCUtil jdbcUtil, DBBean dbBean) {
        checkCfg();
        boolean flag = true;
        synchronized (lock) {
            if (!isOver.get()) {
                try {
                    jdbcUtil.executeQuery(dbBean.getDbType().getValidation_query());
                } catch (SQLException e) {
                    logger.error("JDBCUtilException：checkDB异常，" + e.getMessage() + "，报错的SQL：" + dbBean.getDbType().getValidation_query(), e);
                    isOver.set(true);
                    flag = false;
                    if (dbBean.equals(mainDbBean)) {
                        logger.warn("主库异常，请联系DBA！");
                    } else {
                        logger.warn("备库异常，请联系DBA！");
                    }
                }
            }
        }
        return flag;
    }

    private boolean checkMain() {
        return checkDB(mainIsOver, main, mainDbBean);
    }

    private boolean checkStandby() {
        return checkDB(standbyIsOver, standby, standbyDbBean);
    }

    public void executeQuery(String sql, JDBCUtil.ICallBack iCallBack) throws SQLException {
        checkCfg();
        List<JDBCUtil> jdbcUtils = mainStandbySwitch(0);
        if (jdbcUtils != null && jdbcUtils.size() == 1) {
            JDBCUtil jdbcUtil = jdbcUtils.get(0);
            try {
                jdbcUtil.executeQuery(sql, iCallBack);
            } catch (SQLException e) {
                if (jdbcUtil.equals(main)) {
                    if (checkMain()) throw e;
                } else {
                    if (checkStandby()) throw e;
                }
            }
        } else {
            logger.warn("获取不到要执行的主库备库！");
        }
    }

    public List<List<QueryResult>> executeQuery(String sql) throws SQLException {
        checkCfg();
        List<List<QueryResult>> results = null;
        List<JDBCUtil> jdbcUtils = mainStandbySwitch(0);
        if (jdbcUtils != null && jdbcUtils.size() == 1) {
            JDBCUtil jdbcUtil = jdbcUtils.get(0);
            try {
                results = jdbcUtil.executeQuery(sql);
            } catch (SQLException e) {
                if (jdbcUtil.equals(main)) {
                    if (checkMain()) throw e;
                } else {
                    if (checkStandby()) throw e;
                }
            }
        } else {
            logger.warn("获取不到要执行的主库备库！");
        }
        return results;
    }

    public <T> int executeBatch(String sql, List<T> tList, Class<T> beanCls, String fields) throws SQLException
            , IllegalAccessException, IntrospectionException, InvocationTargetException, InterruptedException, IOException {
        checkCfg();
        int ret = -1;
        List<JDBCUtil> jdbcUtils = mainStandbySwitch(1);
        //判断是否对备库限速
        //注意：这里的备库异常不会抛出
        if (jdbcUtils.size() == 2) {
            JDBCUtil jdbcUtil1 = jdbcUtils.get(0);
            JDBCUtil jdbcUtil2 = jdbcUtils.get(1);
            JDBCUtil jdbcUtilMain;
            JDBCUtil jdbcUtilStandby;
            if (jdbcUtil1.equals(main)) {//1是主库，2是备库
                jdbcUtilMain = jdbcUtil1;
                jdbcUtilStandby = jdbcUtil2;
            } else {//2是主库，1是备库
                jdbcUtilMain = jdbcUtil2;
                jdbcUtilStandby = jdbcUtil1;
            }
            //主库处理
            try {
                ret = jdbcUtilMain.executeBatch(sql, tList, beanCls, fields);
            } catch (SQLException | IOException e) {
                if (checkMain()) throw e;
            }
            //加入备库队列，超过队列阀值则抛
            standbyBeanQueue.offer(new StandbyBean(jdbcUtilStandby, sql, tList, beanCls, fields));
        } else if (jdbcUtils.size() == 1) {
            JDBCUtil jdbcUtil = jdbcUtils.get(0);
            try {
                ret = jdbcUtil.executeBatch(sql, tList, beanCls, fields);
            } catch (SQLException e) {
                if (jdbcUtil.equals(main)) {
                    if (checkMain()) throw e;
                } else {
                    if (checkStandby()) throw e;
                }
            }
        } else {
            logger.warn("获取不到要执行的主库备库！");
        }
        return ret;
    }

    /**
     * 返回要执行的主库或备库或主备
     *
     * @param type <br>
     *             0 查询<br>
     *             1 执行
     * @return
     */
    private List<JDBCUtil> mainStandbySwitch(int type) {
        List<JDBCUtil> jdbcUtils = null;
        if (type == 0) {//查询
            //主库ok、备库ok
            if (mainIsOver.get() && standbyIsOver.get()) {
                //主库查询
                jdbcUtils.add(main);
            }
            //主库ok、备库over
            else if (mainIsOver.get() && !standbyIsOver.get()) {
                //主库查询
                jdbcUtils.add(main);
                //备库告警
                logger.warn("备库异常，请联系DBA！");
            }
            //主库over、备库ok
            else if (!mainIsOver.get() && standbyIsOver.get()) {
                //备库查询
                jdbcUtils.add(standby);
                //主库告警
                logger.warn("主库异常，请联系DBA！");
            }
            //主库over、备库over
            else {
                //主库告警、备库告警
                logger.warn("主备异常，请联系DBA！");
            }
        } else {//执行
            //主库ok、备库ok
            if (mainIsOver.get() && standbyIsOver.get()) {
                //主库写入
                jdbcUtils.add(main);
                //备库写入
                jdbcUtils.add(standby);
            }
            //主库ok、备库over
            else if (mainIsOver.get() && !standbyIsOver.get()) {
                //主库写入
                jdbcUtils.add(main);
                //备库告警
                logger.warn("备库异常，请联系DBA！");
            }
            //主库over、备库ok
            else if (!mainIsOver.get() && standbyIsOver.get()) {
                //主库告警
                logger.warn("主库异常，请联系DBA！");
                //备库写入
                jdbcUtils.add(standby);
            }
            //主库over、备库over
            else {
                //主库告警、备库告警
                logger.warn("主备异常，请联系DBA！");
            }
        }
        return jdbcUtils;
    }

    public void close() {
        if (execStandbyRunable != null) {
            execStandbyRunable.stop();
            try {
                standbyExecThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (main != null) main.close();
        if (standby != null) standby.close();
    }

    class StandbyBean<T> {
        JDBCUtil jdbcUtil;
        String sql;
        List<T> tList;
        Class<T> beanCls;
        String fields;

        StandbyBean(JDBCUtil jdbcUtil, String sql, List<T> tList, Class<T> beanCls, String fields) {
            this.jdbcUtil = jdbcUtil;
            this.sql = sql;
            this.tList = tList;
            this.beanCls = beanCls;
            this.fields = fields;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public List<T> gettList() {
            return tList;
        }

        public void settList(List<T> tList) {
            this.tList = tList;
        }

        public Class<T> getBeanCls() {
            return beanCls;
        }

        public void setBeanCls(Class<T> beanCls) {
            this.beanCls = beanCls;
        }

        public String getFields() {
            return fields;
        }

        public void setFields(String fields) {
            this.fields = fields;
        }

        public JDBCUtil getJdbcUtil() {
            return jdbcUtil;
        }

        public void setJdbcUtil(JDBCUtil jdbcUtil) {
            this.jdbcUtil = jdbcUtil;
        }
    }

    class ExecStandbyRunable extends BaseRunable {

        @Override
        public void exec() throws Exception {
            StandbyBean standbyBean = standbyBeanQueue.poll();
            if (standbyBean != null) {
                try {
                    standbyBean.getJdbcUtil().executeBatch(standbyBean.getSql(), standbyBean.gettList(),
                            standbyBean.getBeanCls(), standbyBean.getFields());
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                    checkStandby();
                } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            SleepUtil.sleepMilliSecond(1);
        }
    }
}
