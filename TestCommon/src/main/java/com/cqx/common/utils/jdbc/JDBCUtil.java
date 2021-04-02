package com.cqx.common.utils.jdbc;

import com.cqx.common.utils.system.ArraysUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.util.*;

/**
 * JDBCUtil
 *
 * @author chenqixu
 */
public class JDBCUtil implements IJDBCUtil {
    private static final Logger logger = LoggerFactory.getLogger(JDBCUtil.class);
    private final String insert = "insert into %s(%s) values(%s)";
    private final String update = "update %s set %s where %s";
    private final String delete = "delete from %s where %s";
    private DataSource dataSource;
    private DBBean dbBean;
    private List<String> keyList = new ArrayList<>();
    private List<String> endList = new ArrayList<>();
    private int batchNum = 2000;
    private boolean isThrow = true;//是否抛出异常，默认抛出

    public JDBCUtil(DBBean dbBean) {
        this.dbBean = dbBean;
        //关键字初始化
        keysInit();
        //数据库初始化
        if (dbBean.isPool()) {
            try {
                dataSource = setupDataSource(dbBean);
            } catch (Exception e) {
                logger.error("连接池初始化失败。" + e.getMessage(), e);
            }
        } else {
            try {
                String DriverClassName = dbBean.getDbType().getDriver();
                Class.forName(DriverClassName);
                DriverManager.setLoginTimeout(15);//超时
            } catch (ClassNotFoundException e) {
                logger.error("无法加载到数据库驱动类，请检查lib。" + e.getMessage(), e);
            }
        }
    }

    private void keysInit() {
        //关键字初始化
        keyList.add(")");
        keyList.add("%");
        keyList.add(",");
        keyList.add(" ");
        //关键字初始化
        endList.add("'");
    }

    private DataSource setupDataSource(DBBean dbBean) {
        return setupDataSource(dbBean.getDbType().getDriver(),
                dbBean.getUser_name(), dbBean.getPass_word(), dbBean.getTns(),
                5, 2, 3, 5000,
                dbBean.getDbType().getValidation_query(),
                false, true, false, false,
                60000, 300000);
    }

    private DataSource setupDataSource(DBBean dbBean, int MaxActive, int MinIdle, int MaxIdle) {
        return setupDataSource(dbBean.getDbType().getDriver(),
                dbBean.getUser_name(), dbBean.getPass_word(), dbBean.getTns(),
                MaxActive, MinIdle, MaxIdle, 5000L,
                dbBean.getDbType().getValidation_query(),
                false, true, false, false,
                60000L, 300000L);
    }

    /**
     * 创建数据源
     *
     * @param driver
     * @param username
     * @param password
     * @param url
     * @param MaxActive
     * @param MinIdle
     * @param MaxIdle
     * @param validation_query
     * @param MaxWait
     * @param TestOnBorrow
     * @param TestWhileIdle
     * @param TestOnReturn
     * @param PoolPreparedStatements
     * @param TimeBetweenEvictionRunsMillis
     * @param MinEvictableIdleTimeMillis
     * @return
     */
    private DataSource setupDataSource(String driver, String username,
                                       String password, String url, int MaxActive, int MinIdle, int MaxIdle,
                                       long MaxWait, String validation_query, boolean TestOnBorrow,
                                       boolean TestWhileIdle, boolean TestOnReturn, boolean PoolPreparedStatements,
                                       long TimeBetweenEvictionRunsMillis, long MinEvictableIdleTimeMillis) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        if (username != null && username.trim().length() > 0) {
            ds.setUsername(username);
        }
        if (password != null && password.trim().length() > 0) {
            ds.setPassword(password);
        }
        // 最大活动连接
        ds.setMaxActive(MaxActive);//5
        // 最小空闲连接
        ds.setMinIdle(MinIdle);//2
        // 最大空闲连接
        ds.setMaxIdle(MaxIdle);//3
        // 获取连接时最大等待时间，单位毫秒
        ds.setMaxWait(MaxWait);//5000
        if (validation_query != null && validation_query.trim().length() > 0) {
            // 设置验证sql，在连接空闲的时候会做
            ds.setValidationQuery(validation_query);//select 1 from dual
            // 连接验证的查询超时时间，单位毫秒
//            ds.setValidationQueryTimeout(ValidationQueryTimeout);//1000
            // 连接借出时是否做验证
            ds.setTestOnBorrow(TestOnBorrow);//false
            // 连接在空闲的时候是否做验证
            ds.setTestWhileIdle(TestWhileIdle);//true
            // 连接回收的时候是否做验证
            ds.setTestOnReturn(TestOnReturn);//false
        }
        // 是否缓存preparedStatement，支持游标的数据库有性能提升
        ds.setPoolPreparedStatements(PoolPreparedStatements);//false
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        ds.setTimeBetweenEvictionRunsMillis(TimeBetweenEvictionRunsMillis);//60000
        // 配置一个连接在池中最小生存的时间，单位是毫秒
        ds.setMinEvictableIdleTimeMillis(MinEvictableIdleTimeMillis);//300000
        return ds;
    }

    /**
     * 获取连接
     *
     * @return 数据库连接
     * @throws SQLException SQL异常
     */
    protected Connection getConnection() throws SQLException {
        if (dbBean.isPool()) {//走连接池
            if (dataSource != null) {
                return dataSource.getConnection();
            }
        } else {//不走连接池
            Properties props = new Properties();
            //没有用户名
            if (dbBean.getUser_name() == null || dbBean.getUser_name().trim().length() == 0) {
                return DriverManager.getConnection(dbBean.getTns());
            } else {
                //有用户名
                props.put("user", dbBean.getUser_name());
                props.put("password", dbBean.getPass_word());
                props.put("remarksReporting", "true");
                return DriverManager.getConnection(dbBean.getTns(), props);
            }
        }
        return null;
    }

    /**
     * 获取表的元数据
     *
     * @param tableName 表名
     * @return List<QueryResult>
     * @throws SQLException SQL异常
     */
    public List<QueryResult> getTableMetaData(String tableName) throws SQLException {
        List<QueryResult> queryResultList = new ArrayList<>();
        Map<String, String> columnRemarksMap = new HashMap<>();
        Connection conn = null;
        ResultSet rs = null;
        Statement stm = null;
        try {
            conn = getConnection();
            assert conn != null;
            DatabaseMetaData meta = conn.getMetaData();
            //实际上是查询all_tab_columns，具体columnLable可以点进去看具体查询的SQL
            //主要是为了获取注释，这个需要连接的时候设置remarksReporting为true
            rs = meta.getColumns(null, null, tableName, null);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String columnComment = rs.getString("REMARKS");
                columnRemarksMap.put(columnName, columnComment);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closeConn(conn);
        }
        try {
            String sql = "select * from " + tableName + " where rownum=0";
            conn = getConnection();
            assert conn != null;
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            //查询结果设置到List<QueryResult>中
            queryResultList = buildQueryResult(rsMeta, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            closeResultSet(rs);
            closeStm(stm);
            closeConn(conn);
        }
        List<QueryResult> ret = new ArrayList<>();
        for (QueryResult queryResult : queryResultList) {
            String remarks = columnRemarksMap.get(queryResult.getColumnName());
            if (remarks != null) {
                queryResult.setREMARKS(remarks);
            }
            ret.add(queryResult);
        }
        return ret;
    }

    /**
     * 通过表名构造表对象
     *
     * @param fields     字段
     * @param table_name 表名
     * @return BeanUtil
     * @throws SQLException           SQL异常
     * @throws ClassNotFoundException 类找不到
     */
    public BeanUtil generateBeanByTabeNameAndFields(String fields, String table_name) throws SQLException, ClassNotFoundException {
        ResultSet rs = null;
        BeanUtil beanUtil;
        LinkedHashMap<String, Class<?>> properties = new LinkedHashMap<>();
        Connection conn = null;
        Statement stm = null;
        try {
            conn = getConnection();
            assert conn != null;
            stm = conn.createStatement();
            String sql = "select " + fields + " from " + table_name + " where 1<>1";
            rs = stm.executeQuery(sql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
                String ColumnLabel = rsMeta.getColumnLabel(i + 1);
                int ColumnType = rsMeta.getColumnType(i + 1);
                String ColumnClassName = rsMeta.getColumnClassName(i + 1);
                properties.put(ColumnLabel, Class.forName(ColumnClassName));
                logger.debug("ColumnLabel：{}，ColumnType：{}，ColumnClassName：{}",
                        ColumnLabel, ColumnType, ColumnClassName);
            }
            beanUtil = new BeanUtil();
            beanUtil.generateObject(properties);
        } finally {
            closeResultSet(rs);
            closeStm(stm);
            closeConn(conn);
        }
        return beanUtil;
    }

    /**
     * 通过表名构造表对象
     *
     * @param table_name 表名
     * @return BeanUtil
     * @throws SQLException           SQL异常
     * @throws ClassNotFoundException 类找不到
     */
    public BeanUtil generateBeanByTabeName(String table_name) throws SQLException, ClassNotFoundException {
        ResultSet rs = null;
        BeanUtil beanUtil;
        LinkedHashMap<String, Class<?>> properties = new LinkedHashMap<>();
        Connection conn = null;
        Statement stm = null;
        try {
            conn = getConnection();
            assert conn != null;
            stm = conn.createStatement();
            String sql = "select * from " + table_name + " where 1<>1";
            rs = stm.executeQuery(sql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
                String ColumnLabel = rsMeta.getColumnLabel(i + 1);
                int ColumnType = rsMeta.getColumnType(i + 1);
                String ColumnClassName = rsMeta.getColumnClassName(i + 1);
                properties.put(ColumnLabel, Class.forName(ColumnClassName));
                logger.debug("ColumnLabel：{}，ColumnType：{}，ColumnClassName：{}",
                        ColumnLabel, ColumnType, ColumnClassName);
            }
            beanUtil = new BeanUtil();
            beanUtil.generateObject(properties);
        } finally {
            closeResultSet(rs);
            closeStm(stm);
            closeConn(conn);
        }
        return beanUtil;
    }

    /**
     * 执行sql查询语句，使用回调接口
     *
     * @param sql
     * @param iCallBack
     * @throws SQLException
     */
    public void executeQuery(String sql, ICallBack iCallBack) throws SQLException {
        ResultSet rs = null;
        Connection conn = null;
        Statement stm = null;
        try {
            conn = getConnection();
            assert conn != null;
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                iCallBack.call(rs);
            }
            //关闭
            iCallBack.close();
        } catch (Exception e) {
            logger.error("JDBCUtilException：executeQuery异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            if (isThrow()) throw e;
        } finally {
            closeResultSet(rs);
            closeStm(stm);
            closeConn(conn);
        }
    }

    /**
     * 执行sql查询语句，使用回调接口，带Bean
     *
     * @param sql
     * @param iBeanCallBack
     * @throws SQLException
     */
    public <T> void executeQuery(String sql, Class<T> beanCls, IBeanCallBack<T> iBeanCallBack) throws Exception {
        ResultSet rs = null;
        Connection conn = null;
        Statement stm = null;
        T t;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            conn = getConnection();
            assert conn != null;
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                //查询结果设置到java bean中
                t = queryResultSet(rsMeta, propertyDescriptors, rs, beanCls);
                iBeanCallBack.call(t);
            }
            //关闭
            iBeanCallBack.close();
        } catch (Exception e) {
            logger.error("JDBCUtilException：executeQuery异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            if (isThrow()) throw e;
        } finally {
            closeResultSet(rs);
            closeStm(stm);
            closeConn(conn);
        }
    }

    /**
     * 执行sql查询语句，使用回调接口，带QueryResult
     *
     * @param sql
     * @param iQueryResultCallBack
     * @throws Exception
     */
    public void executeQuery(String sql, IQueryResultCallBack iQueryResultCallBack) throws Exception {
        ResultSet rs = null;
        Connection conn = null;
        Statement stm = null;
        try {
            conn = getConnection();
            assert conn != null;
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                //查询结果设置到List<QueryResult>中
                List<QueryResult> queryResults = buildQueryResult(rsMeta, rs);
                iQueryResultCallBack.call(queryResults);
            }
            //关闭
            iQueryResultCallBack.close();
        } catch (Exception e) {
            logger.error("JDBCUtilException：executeQuery异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            if (isThrow()) throw e;
        } finally {
            closeResultSet(rs);
            closeStm(stm);
            closeConn(conn);
        }
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql sql
     * @return List<List < QueryResult>
     */
    public List<List<QueryResult>> executeQuery(String sql) throws SQLException {
        ResultSet rs = null;
        List<List<QueryResult>> tList = new ArrayList<>();
        Connection conn = null;
        Statement stm = null;
        try {
            conn = getConnection();
            assert conn != null;
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                //查询结果设置到List<QueryResult>中
                List<QueryResult> queryResults = buildQueryResult(rsMeta, rs);
                tList.add(queryResults);
            }
        } catch (Exception e) {
            logger.error("JDBCUtilException：executeQuery异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            tList = null;
            if (isThrow()) throw e;
        } finally {
            closeResultSet(rs);
            closeStm(stm);
            closeConn(conn);
        }
        return tList;
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql     sql
     * @param beanCls 结果类
     * @param <T>     结果类
     * @return List<T>
     */
    public <T> List<T> executeQuery(String sql, Class<T> beanCls) throws Exception {
        ResultSet rs = null;
        T t;
        List<T> tList = new ArrayList<>();
        Connection conn = null;
        Statement stm = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            conn = getConnection();
            assert conn != null;
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                //查询结果设置到java bean中
                t = queryResultSet(rsMeta, propertyDescriptors, rs, beanCls);
                tList.add(t);
            }
        } catch (Exception e) {
            logger.error("JDBCUtilException：executeQuery异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            tList = null;
            if (isThrow()) throw e;
        } finally {
            closeResultSet(rs);
            closeStm(stm);
            closeConn(conn);
        }
        return tList;
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql       sql
     * @param beanCls   结果类
     * @param paramList 参数列表
     * @param <T>       结果类
     * @return List<T>
     */
    public <T> List<T> executeQuery(String sql, Class<T> beanCls, List<Object> paramList) throws Exception {
        ResultSet rs = null;
        T t;
        List<T> tList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            conn = getConnection();
            assert conn != null;
            pstmt = conn.prepareStatement(sql);
            for (int i = 1; i <= paramList.size(); i++) {
                Object queryParam = paramList.get(i);
                pstmt.setObject(i, queryParam);
            }
            rs = pstmt.executeQuery();//注意，上面已经绑定变量了，这里不需要再传sql
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                //查询结果设置到java bean中
                t = queryResultSet(rsMeta, propertyDescriptors, rs, beanCls);
                tList.add(t);
            }
        } catch (Exception e) {
            logger.error("JDBCUtilException：executeQuery异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            tList = null;
            if (isThrow()) throw e;
        } finally {
            closeResultSet(rs);
            closePstmt(pstmt);
            closeConn(conn);
        }
        return tList;
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql         sql
     * @param paramObject 查询参数
     * @param beanCls     结果类
     * @param <T>         结果类
     * @return List<T>
     */
    public <T> List<T> executeQuery(String sql, Object paramObject, Class<T> beanCls) throws Exception {
        ResultSet rs = null;
        T t;
        List<T> tList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;

        //解析sql，找到所有的:xx，比如：select a from b where a=:a and b=:b and c>=:c and (d=:d)
        String[] params = (sql + " ").split(":", -1);
        List<String> paramList = new ArrayList<>();
        for (int i = 1; i < params.length; i++) {
            String _tmp = params[i];
            //找到空格、括号等等就返回
            String key = getParam(_tmp, ")", 0);
            //判断结尾字符是否异常
            if (paramEndWith(key)) {
                paramList.add(key);
            } else {
                key = null;
            }
            logger.info(String.format("%s：【%s】", params[i], key));
            if (key != null) sql = sql.replace(":" + key, "?");
        }
        logger.info("sql：{}", sql);

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            conn = getConnection();
            assert conn != null;
            pstmt = conn.prepareStatement(sql);

            BeanInfo paramBeanInfo = Introspector.getBeanInfo(paramObject.getClass());
            PropertyDescriptor[] paramPropertyDescriptors = paramBeanInfo.getPropertyDescriptors();
            //循环参数，设置值
            int parameterIndex = 0;
            for (String param : paramList) {
                for (PropertyDescriptor property : paramPropertyDescriptors) {
                    String key = property.getName().toUpperCase();
                    String propertyName = property.getPropertyType().getName();// 获取bean字段的类型
                    if (param.toUpperCase().equals(key)) {
                        parameterIndex++;
                        logger.info("param：{}，key：{}，propertyName：{}", param, key, propertyName);
                        Method getter = property.getReadMethod();// Java中提供了用来访问某个属性的
                        Object value = getter.invoke(paramObject);
                        // setter方法，oracle一般把数值型转换成BigDecimal，所以这里需要转换
                        // 设置参数
                        pstmtSetValue(pstmt, propertyName, parameterIndex, value);
                        break;
                    }
                }
            }
            rs = pstmt.executeQuery();
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                //查询结果设置到java bean中
                t = queryResultSet(rsMeta, propertyDescriptors, rs, beanCls);
                tList.add(t);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            tList = null;
            if (isThrow()) throw e;
        } finally {
            closeResultSet(rs);
            closePstmt(pstmt);
            closeConn(conn);
        }
        return tList;
    }

    /**
     * 执行更新语句，返回执行结果
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public int executeUpdate(String sql) throws SQLException {
        int result;
        Connection conn = null;
        Statement stm = null;
        try {
            conn = getConnection();
            assert conn != null;
            conn.setAutoCommit(false);
            stm = conn.createStatement();
            result = stm.executeUpdate(sql);
            conn.commit();
        } catch (SQLException e) {
            logger.error("JDBCUtilException：executeUpdate异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            result = -1;
            if (conn != null) conn.rollback();
            if (isThrow()) throw e;
        } finally {
            closeStm(stm);
            closeConn(conn);
        }
        return result;
    }

    /**
     * 批量执行更新语句，返回执行结果
     *
     * @param sqls
     * @return
     * @throws SQLException
     */
    public List<Integer> executeBatch(List<String> sqls) throws SQLException {
        int add_cnt = 0;
        int success_cnt = 0;
        int batch_cnt = 0;
        int commit_cnt = 0;
        List<Integer> retList = new ArrayList<>();
        Connection conn = null;
        Statement stm = null;
        try {
            conn = getConnection();
            assert conn != null;
            conn.setAutoCommit(false);
            stm = conn.createStatement();
            for (String sql : sqls) {
                stm.addBatch(sql);
                add_cnt++;
                // x条提交一次
                if (add_cnt > 0 && (add_cnt % getBatchNum() == 0)) {
                    batch_cnt++;
                    int[] rets = stm.executeBatch();
                    conn.commit();
                    success_cnt += getSuccess(rets);
                    commit_cnt = add_cnt;
                    for (int ret : rets) retList.add(ret);
                }
            }
            // 剩余数据提交
            if (add_cnt > commit_cnt) {
                batch_cnt++;
                int[] rets = stm.executeBatch();
                conn.commit();
                success_cnt += getSuccess(rets);
                commit_cnt = add_cnt;
                for (int ret : rets) retList.add(ret);
            }
            logger.debug("add_cnt：{}，success_cnt：{}，batch_cnt：{}", add_cnt, success_cnt, batch_cnt);
        } catch (SQLException e) {
            logger.error("JDBCUtilException：executeBatch异常，" + e.getMessage() + "，报错的SQL：" + sqls, e);
            retList.add(-1);
            if (conn != null) conn.rollback();
            if (isThrow()) throw e;
        } finally {
            closeStm(stm);
            closeConn(conn);
        }
        return retList;
    }

    /**
     * 根据操作类型，字段，pk来构造SQL
     *
     * @param op_type            操作类型：i、u、d
     * @param queryResults       操作数据
     * @param table              操作表
     * @param fields             更新字段（需要剔除pk）
     * @param fields_type        更新字段类型
     * @param pks                pk字段
     * @param pks_type           pk字段类型
     * @param ismissing          字段是否有值（ogg特殊）
     * @param insert_fields      插入字段
     * @param insert_fields_type 插入字段类型
     * @return
     * @throws SQLException
     */
    private String buildBatchSQL(String op_type, List<QueryResult> queryResults,
                                 String table, String[] fields, String[] fields_type,
                                 String[] pks, String[] pks_type, boolean ismissing,
                                 String insert_fields, String[] insert_fields_type) throws SQLException {
        String sql = "";
        if (op_type == null || op_type.trim().length() == 0) throw new NullPointerException("op_type为空！");
        op_type = op_type.toLowerCase();
        switch (op_type) {
            case "i":
                //数据检查
                if (queryResults.size() != (fields.length + pks.length))
                    throw new SQLException("数据长度和(fields+pks)长度不一致！");
                StringBuilder insert_values = new StringBuilder();
                for (int i = 0; i < queryResults.size(); i++) {
                    QueryResult queryResult = queryResults.get(i);
                    String field_type = insert_fields_type[i];
                    insert_values.append(stmtSetValue(field_type, queryResult.getValue()));
                    if (i < queryResults.size() - 1) {
                        insert_values.append(",");
                    }
                }
                sql = String.format(insert, table, insert_fields, insert_values.toString());
                break;
            case "u":
                StringBuilder update_set_values = new StringBuilder();
                StringBuilder update_where_values = new StringBuilder();
                //字段缺失
                if (ismissing) {
                    //========================================
                    //ismissing只针对set字段
                    //pk可能为null，但目前观察不会有ismissing的情况
                    //List<QueryResult>可能的顺序为
                    //- field1_ismissing
                    //- field1
                    //- field2_ismissing
                    //- field2
                    //- pk1
                    //- pk2
                    //========================================
                    //数据检查
                    if (queryResults.size() != (fields.length + fields.length + pks.length))
                        throw new SQLException("数据长度和(fields+fields_ismissing+pks)的长度不一致！");
                    //处理set
                    for (int i = 0; i < fields.length; i++) {
                        int ismissing_location = i * 2;
                        int real_location = ismissing_location + 1;
                        QueryResult queryResult = queryResults.get(real_location);
                        String field = fields[i];
                        String field_type = fields_type[i];
                        QueryResult queryResultIsmissing = queryResults.get(ismissing_location);
                        if (queryResultIsmissing == null || queryResultIsmissing.getValue() == null)
                            logger.warn("{} {} {}", field, ismissing_location, queryResultIsmissing);
                        boolean field_ismissing = Boolean.valueOf(queryResultIsmissing.getValue().toString());
                        if (!field_ismissing) {
                            update_set_values
                                    .append(field)
                                    .append("=")
                                    .append(stmtSetValue(field_type, queryResult.getValue()));
                            update_set_values.append(",");
                        }
                    }
                    if (update_set_values.length() > 0)
                        update_set_values.delete(update_set_values.length() - 1, update_set_values.length());
                } else {
                    //数据检查
                    if (queryResults.size() != (fields.length + pks.length))
                        throw new SQLException("数据长度和(fields+pks)的长度不一致！");
                    //处理set
                    for (int i = 0; i < fields.length; i++) {
                        QueryResult queryResult = queryResults.get(i);
                        String field = fields[i];
                        String field_type = fields_type[i];
                        update_set_values
                                .append(field)
                                .append("=")
                                .append(stmtSetValue(field_type, queryResult.getValue()));
                        if (i < fields.length - 1) {
                            update_set_values.append(",");
                        }
                    }
                }
                //处理where
                for (int i = 0; i < pks.length; i++) {
                    QueryResult queryResult;
                    if (ismissing) {
                        queryResult = queryResults.get(fields.length * 2 + i);
                    } else {
                        queryResult = queryResults.get(fields.length + i);
                    }
                    String pk = pks[i];
                    String pk_type = pks_type[i];
                    Object field_value = queryResult.getValue();
                    if (field_value != null) {
                        update_where_values
                                .append(pk)
                                .append("=")
                                .append(stmtSetValue(pk_type, field_value));
                        update_where_values.append(" and ");
                    }
                }
                if (update_where_values.length() > 0) {
                    update_where_values.delete(update_where_values.length() - 5, update_where_values.length());
                } else if (update_where_values.length() == 0) {
                    //todo pkMissing
//                    if (pkMissing != null) pkMissing.mark();
                    logger.warn("where条件均为空！op_type：{}，pks：{}，数据：{}",
                            op_type, Arrays.asList(pks), queryResults);
                    break;
                }
                sql = String.format(update, table, update_set_values.toString(), update_where_values.toString());
                break;
            case "d":
                //数据检查
                if (queryResults.size() != (pks.length))
                    throw new SQLException("数据长度和pks的长度不一致！");
                StringBuilder delete_values = new StringBuilder();
                //处理where
                for (int i = 0; i < pks.length; i++) {
                    QueryResult queryResult = queryResults.get(i);
                    String pk = pks[i];
                    String pk_type = pks_type[i];
                    Object field_value = queryResult.getValue();
                    if (field_value != null) {
                        delete_values
                                .append(pk)
                                .append("=")
                                .append(stmtSetValue(pk_type, field_value));
                        delete_values.append(" and ");
                    }
                }
                if (delete_values.length() > 0) {
                    delete_values.delete(delete_values.length() - 5, delete_values.length());
                } else if (delete_values.length() == 0) {
                    //todo pkMissing
//                    if (pkMissing != null) pkMissing.mark();
                    logger.warn("where条件均为空！op_type：{}，pks：{}，数据：{}",
                            op_type, Arrays.asList(pks), queryResults);
                    break;
                }
                sql = String.format(delete, table, delete_values.toString());
                break;
            default:
                throw new SQLException("不支持的op_type！【" + op_type + "】");
        }
        logger.debug("buildBatchSQL：{}", sql);
        return sql;
    }

    /**
     * 根据操作类型，字段，pk来构造SQL，最后批量执行
     *
     * @param iQueryResultBeanList
     * @param table
     * @param fields
     * @param fields_type
     * @param pks
     * @param pks_type
     * @return
     * @throws SQLException
     * @see JDBCUtil#buildBatchSQL(String, List, String, String[], String[], String[], String[], boolean, String, String[])
     */
    public List<Integer> executeBatch(List<? extends IQueryResultBean> iQueryResultBeanList, String table,
                                      String[] fields, String[] fields_type,
                                      String[] pks, String[] pks_type) throws SQLException {
        return executeBatch(iQueryResultBeanList, table, fields, fields_type, pks, pks_type, false);
    }

    /**
     * 根据操作类型，字段，pk来构造SQL，最后批量执行
     *
     * @param iQueryResultBeanList
     * @param table
     * @param fields
     * @param fields_type
     * @param pks
     * @param pks_type
     * @param ismissing
     * @return
     * @throws SQLException
     * @see JDBCUtil#buildBatchSQL(String, List, String, String[], String[], String[], String[], boolean, String, String[])
     */
    public List<Integer> executeBatch(List<? extends IQueryResultBean> iQueryResultBeanList, String table,
                                      String[] fields, String[] fields_type,
                                      String[] pks, String[] pks_type, boolean ismissing) throws SQLException {
        List<String> sqlList = new ArrayList<>();

        //检查
        if (table == null || table.trim().length() == 0) throw new NullPointerException("table为空！");
        if (fields == null || fields.length == 0) throw new NullPointerException("fields为空！");
        if (fields_type == null || fields_type.length == 0) throw new NullPointerException("fields_type为空！");
        if (fields.length != fields_type.length) throw new SQLException("fields长度和fields_type不一致！");
        if (pks == null || pks.length == 0) throw new NullPointerException("pks为空！");
        if (pks_type == null || pks_type.length == 0) throw new NullPointerException("pks_type为空！");
        if (pks.length != pks_type.length) throw new SQLException("pks长度和pks_type不一致！");

        //插入的字段、字段类型由 field+pks 组成
        String[] insert_fields_type = ArraysUtil.arrayCopy(fields_type, pks_type);
        String insert_fields = ArraysUtil.arrayToStr(ArraysUtil.arrayCopy(fields, pks), ",");
        //具体处理
        for (IQueryResultBean iQueryResultBean : iQueryResultBeanList) {
            String op_type = iQueryResultBean.getOp_type();
            List<QueryResult> queryResults = iQueryResultBean.getQueryResults();
            //构造SQL
            String sql = buildBatchSQL(op_type, queryResults, table, fields,
                    fields_type, pks, pks_type, ismissing, insert_fields, insert_fields_type);
            if (sql != null && sql.length() > 0) sqlList.add(sql);
        }
        return executeBatch(sqlList);
    }

    /**
     * 根据操作类型，字段，pk来构造SQL，最后批量执行
     *
     * @param op_types
     * @param tList
     * @param table
     * @param fields
     * @param fields_type
     * @param pks
     * @param pks_type
     * @return
     * @throws SQLException
     * @see JDBCUtil#buildBatchSQL(String, List, String, String[], String[], String[], String[], boolean, String, String[])
     */
    public List<Integer> executeBatch(List<String> op_types, List<List<QueryResult>> tList,
                                      String table, String[] fields, String[] fields_type,
                                      String[] pks, String[] pks_type) throws SQLException {
        return executeBatch(op_types, tList, table, fields, fields_type, pks, pks_type, false);
    }

    /**
     * 根据操作类型，字段，pk来构造SQL，最后批量执行
     *
     * @param op_types
     * @param tList
     * @param table
     * @param fields
     * @param fields_type
     * @param pks
     * @param pks_type
     * @param ismissing
     * @return
     * @throws SQLException
     * @see JDBCUtil#buildBatchSQL(String, List, String, String[], String[], String[], String[], boolean, String, String[])
     */
    public List<Integer> executeBatch(List<String> op_types, List<List<QueryResult>> tList,
                                      String table, String[] fields, String[] fields_type,
                                      String[] pks, String[] pks_type, boolean ismissing) throws SQLException {
        List<String> sqlList = new ArrayList<>();

        //检查
        if (op_types.size() != tList.size()) throw new SQLException("op_types长度和tList长度不一致！");
        if (table == null || table.trim().length() == 0) throw new NullPointerException("table为空！");
        if (fields == null || fields.length == 0) throw new NullPointerException("fields为空！");
        if (fields_type == null || fields_type.length == 0) throw new NullPointerException("fields_type为空！");
        if (fields.length != fields_type.length) throw new SQLException("fields长度和fields_type不一致！");
        if (pks == null || pks.length == 0) throw new NullPointerException("pks为空！");
        if (pks_type == null || pks_type.length == 0) throw new NullPointerException("pks_type为空！");
        if (pks.length != pks_type.length) throw new SQLException("pks长度和pks_type不一致！");

        //插入的字段、字段类型由 field+pks 组成
        String[] insert_fields_type = ArraysUtil.arrayCopy(fields_type, pks_type);
        String insert_fields = ArraysUtil.arrayToStr(ArraysUtil.arrayCopy(fields, pks), ",");
        //具体处理
        for (int op_type_index = 0; op_type_index < op_types.size(); op_type_index++) {
            String op_type = op_types.get(op_type_index);
            List<QueryResult> queryResults = tList.get(op_type_index);
            //构造SQL
            String sql = buildBatchSQL(op_type, queryResults, table, fields,
                    fields_type, pks, pks_type, ismissing, insert_fields, insert_fields_type);
            if (sql != null && sql.length() > 0) sqlList.add(sql);
        }
        return executeBatch(sqlList);
    }

    /**
     * 执行更新语句，返回成功记录数，小于0表示失败
     *
     * @param sql
     * @param tList
     * @param dstFieldsType
     * @return
     */
    public int executeBatch(String sql, List<List<QueryResult>> tList, List<String> dstFieldsType) throws Exception {
        int add_cnt = 0;
        int success_cnt = 0;
        int batch_cnt = 0;
        int commit_cnt = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            assert conn != null;
            conn.setAutoCommit(false);// 关闭自动提交
            pstmt = conn.prepareStatement(sql);// 预编译SQL
            // 循环查询结果
            for (List<QueryResult> queryResults : tList) {
                for (int i = 0; i < queryResults.size(); i++) {
                    String dstFieldType = dstFieldsType.get(i);
                    QueryResult queryResult = queryResults.get(i);
                    Object fieldValue = queryResult.getValue();
                    String srcFieldType = queryResult.getColumnClassName();
                    int parameterIndex = i + 1;
                    // 设置参数
                    pstmtSetValue(pstmt, dstFieldType, srcFieldType, parameterIndex, fieldValue);
                }
                pstmt.addBatch();
                add_cnt++;
                // x条提交一次
                if (add_cnt > 0 && (add_cnt % getBatchNum() == 0)) {
                    batch_cnt++;
                    int[] ret = pstmt.executeBatch();
                    conn.commit();
                    success_cnt += getSuccess(ret);
                    commit_cnt = add_cnt;
                }
            }
            // 剩余数据提交
            if (add_cnt > commit_cnt) {
                batch_cnt++;
                int[] ret = pstmt.executeBatch();
                conn.commit();
                success_cnt += getSuccess(ret);
                commit_cnt = add_cnt;
            }
            logger.debug("add_cnt：{}，success_cnt：{}，batch_cnt：{}", add_cnt, success_cnt, batch_cnt);
        } catch (Exception e) {
            logger.error("JDBCUtilException：executeBatch异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            success_cnt = -1;
            if (conn != null) conn.rollback();
            if (isThrow()) throw e;
        } finally {
            closePstmt(pstmt);
            closeConn(conn);
        }
        return success_cnt;
    }

    /**
     * 执行更新语句，返回执行结果，动态带参
     *
     * @param sql          sql语句
     * @param tList        数据
     * @param srcBeanCls   源端javabean
     * @param dstBeanCls   目标端javabean
     * @param dstFieldsMap 目标端字段映射
     * @param <T>
     * @return 结果
     */
    public <T> int executeBatch(String sql, List<T> tList, Class<T> srcBeanCls, Class<T> dstBeanCls, LinkedHashMap<String, Object> dstFieldsMap) throws SQLException {
        int ret;
        int commit_cnt = 0;
        int success_cnt = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // oracle关键字
            if (sql.contains(",user,")) {
                sql = sql.replace(",user,", ",\"USER\",");
                logger.info("oracle关键字替换，sql：{}", sql);
            }
            conn = getConnection();
            assert conn != null;
            conn.setAutoCommit(false);// 关闭自动提交
            pstmt = conn.prepareStatement(sql);// 预编译SQL
            // 获取javabean属性，源端
            BeanInfo srcBeanInfo = Introspector.getBeanInfo(srcBeanCls);
            PropertyDescriptor[] srcPropertyDescriptors = srcBeanInfo.getPropertyDescriptors();
            // 获取javabean属性，目标端
            BeanInfo dstBeanInfo = Introspector.getBeanInfo(dstBeanCls);
            PropertyDescriptor[] dstPropertyDescriptors = dstBeanInfo.getPropertyDescriptors();
            // 轮询源端/目标端javabean属性，把get方法映射到目标端字段map中
            for (PropertyDescriptor property : srcPropertyDescriptors) {
                String key = property.getName().toLowerCase();
                // 目标端字段有值
                if (dstFieldsMap.get(key) != null) {
                    for (PropertyDescriptor dstProperty : dstPropertyDescriptors) {
                        String dstKey = dstProperty.getName().toLowerCase();
                        if (dstKey.equals(key)) {
                            // 字段名，目标端字段类型，源端get方法
                            dstFieldsMap.put(key, new BatchBean(key, property.getPropertyType(), dstProperty.getPropertyType(), property.getReadMethod()));
                            break;
                        }
                    }
                }
            }
            // 循环查询结果
            for (T t : tList) {
                int i = 1;
                // 轮询字段map，把值设置到预编译中
                for (Map.Entry<String, Object> entry : dstFieldsMap.entrySet()) {
                    BatchBean batchBean = (BatchBean) entry.getValue();
                    Class<?> srcFieldType = batchBean.getSrccls();
                    Class<?> dstFieldType = batchBean.getDstcls();
                    Object fieldValue = batchBean.getMethod().invoke(t);
                    // 设置参数
                    pstmtSetValue(pstmt, dstFieldType.getName(), srcFieldType.getName(), i, fieldValue);
                    i++;
                }
                pstmt.addBatch();
                commit_cnt++;
                // 2000条提交一次
                if (commit_cnt % getBatchNum() == 0) {
                    pstmt.executeBatch();
                    conn.commit();
                }
                success_cnt++;
            }
            // 剩余数据提交
            pstmt.executeBatch();
            conn.commit();
            ret = 0;
            logger.info("成功记录数：{}", success_cnt);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ret = -1;
            if (conn != null) conn.rollback();
        } finally {
            closePstmt(pstmt);
            closeConn(conn);
        }
        return ret;
    }

    /**
     * 执行更新语句，返回执行结果
     *
     * @param sql     sql语句
     * @param tList   数据
     * @param beanCls 内容javabean
     * @param fields  字段，参考nsert into tabe(id,name) values(:id,:name)
     * @param <T>     内容javabean
     * @return 结果
     */
    public <T> int executeBatch(String sql, List<T> tList, Class<T> beanCls, String fields) throws SQLException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        return executeBatch(sql, tList, beanCls, fields, false).get(0);
    }

    public <T> List<Integer> executeBatch(String sql, List<T> tList, Class<T> beanCls, String fields, boolean hasRet) throws SQLException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        int add_cnt = 0;
        int success_cnt = 0;
        int batch_cnt = 0;
        int commit_cnt = 0;
        List<Integer> retList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        String[] fields_arr = fields.split(",", -1);
        try {
            conn = getConnection();
            assert conn != null;
            conn.setAutoCommit(false);// 关闭自动提交
            pstmt = conn.prepareStatement(sql);// 预编译SQL
            // 获取javabean属性，源端
            BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            LinkedHashMap<String, BatchBean> methodLinkedHashMap = new LinkedHashMap<>();
            for (String str : fields_arr) {
                String name = str.replace(":", "").toUpperCase();
                for (PropertyDescriptor property : propertyDescriptors) {
                    String key = property.getName().toUpperCase();
                    if (name.equals(key)) {
                        Method getter = property.getReadMethod();// Java中提供了用来访问某个属性的
                        String propertyName = property.getPropertyType().getName();
                        methodLinkedHashMap.put(key, new BatchBean(propertyName, getter));
                        break;
                    }
                }
            }
            // 循环查询结果
            for (T t : tList) {
                int i = 1;
                for (Map.Entry<String, BatchBean> entry : methodLinkedHashMap.entrySet()) {
                    Object fieldValue = entry.getValue().getMethod().invoke(t);
                    // 设置参数
                    pstmtSetValue(pstmt, entry.getValue().getName(), i, fieldValue);
                    i++;
                }
                pstmt.addBatch();
                add_cnt++;
                // x条提交一次
                if (add_cnt > 0 && (add_cnt % getBatchNum() == 0)) {
                    batch_cnt++;
                    int[] ret = pstmt.executeBatch();
                    conn.commit();
                    success_cnt += getSuccess(ret);
                    commit_cnt = add_cnt;
                    if (hasRet) for (int r : ret) retList.add(r);
                }
            }
            // 剩余数据提交
            if (add_cnt > commit_cnt) {
                batch_cnt++;
                int[] ret = pstmt.executeBatch();
                conn.commit();
                success_cnt += getSuccess(ret);
                commit_cnt = add_cnt;
                if (hasRet) for (int r : ret) retList.add(r);
            }
            if (!hasRet) retList.add(success_cnt);
            logger.debug("add_cnt：{}，success_cnt：{}，batch_cnt：{}", add_cnt, success_cnt, batch_cnt);
        } catch (Exception e) {
            logger.error("JDBCUtilException：executeBatch异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            if (conn != null) conn.rollback();
            if (isThrow()) throw e;
        } finally {
            closePstmt(pstmt);
            closeConn(conn);
        }
        return retList;
    }

    /**
     * 批量执行更新，可以自动抛掉异常数据并重试（每次一行）
     *
     * @param sql
     * @param tList
     * @param beanCls
     * @param fields
     * @param <T>
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    public <T> void executeBatchRetry(String sql, List<T> tList, Class<T> beanCls, String fields) throws SQLException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        int success_cnt = 0;
        int batch_cnt = 0;
        // 获取javabean属性，源端
        BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        LinkedHashMap<String, BatchBean> methodLinkedHashMap = new LinkedHashMap<>();
        for (String str : fields.split(",", -1)) {
            String name = str.replace(":", "").toUpperCase();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName().toUpperCase();
                if (name.equals(key)) {
                    Method getter = property.getReadMethod();// Java中提供了用来访问某个属性的
                    String propertyName = property.getPropertyType().getName();
                    methodLinkedHashMap.put(key, new BatchBean(propertyName, getter));
                    break;
                }
            }
        }
        // 循环查询结果
        Iterator<T> iterator = tList.iterator();
        List<T> executeBatchList = new ArrayList<>();
        while (iterator.hasNext()) {
            T t = iterator.next();
            executeBatchList.add(t);
            // 2000条提交一次
            if (executeBatchList.size() % getBatchNum() == 0) {
                int[] ret = executeBatch(sql, executeBatchList, methodLinkedHashMap);
                executeBatchList.clear();
                batch_cnt++;
                success_cnt += ret.length;
            }
        }
        // 剩余数据提交
        if (executeBatchList.size() > 0) {
            int[] ret = executeBatch(sql, executeBatchList, methodLinkedHashMap);
            batch_cnt++;
            success_cnt += ret.length;
        }
        logger.info("batch_cnt：{}，success_cnt：{}", batch_cnt, success_cnt);
    }

    private <T> int[] executeBatch(String sql, List<T> tList, LinkedHashMap<String, BatchBean> methodLinkedHashMap) throws SQLException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int[] result = {0};
        try {
            conn = getConnection();
            assert conn != null;
            conn.setAutoCommit(false);// 关闭自动提交
            pstmt = conn.prepareStatement(sql);// 预编译SQL
            // 循环查询结果
            Iterator<T> iterator = tList.iterator();
            while (iterator.hasNext()) {
                T t = iterator.next();
                int i = 1;
                for (Map.Entry<String, BatchBean> entry : methodLinkedHashMap.entrySet()) {
                    Object fieldValue = entry.getValue().getMethod().invoke(t);
                    // 设置参数
                    pstmtSetValue(pstmt, entry.getValue().getName(), i, fieldValue);
                    i++;
                }
                pstmt.addBatch();
            }
            result = pstmt.executeBatch();
            conn.commit();
        } catch (BatchUpdateException e) {
            //关闭资源
            closePstmt(pstmt);
            closeConn(conn);
            logger.warn("BatchUpdateException：{}", e.getMessage());
            //获取更新的个数
            int[] updateCounts = e.getUpdateCounts();
            T errObj = tList.get(updateCounts.length);
            tList.remove(updateCounts.length);
            int remaining_size = tList.size();
            //先尝试抛掉异常的一行数据
            logger.warn("updateCounts：{}，error data：{}，remaining_size：{}", updateCounts.length, errObj, remaining_size);
            //抛掉一行后继续重试
            if (remaining_size > 0)
                executeBatch(sql, tList, methodLinkedHashMap);
        } catch (Exception e) {
            //回滚
            if (conn != null) conn.rollback();
            logger.error("JDBCUtilException：executeBatch异常，" + e.getMessage() + "，报错的SQL：" + sql, e);
            if (isThrow()) throw e;
        } finally {
            closePstmt(pstmt);
            closeConn(conn);
        }
        return result;
    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("JDBCUtilException：关闭ResultSet异常，" + e.getMessage(), e);
            }
        }
    }

    private void closeStm(Statement stm) {
        if (stm != null)
            try {
                logger.debug("closeStm：{}", stm);
                stm.close();
            } catch (SQLException e) {
                logger.error("JDBCUtilException：关闭Statement异常，" + e.getMessage(), e);
            }
    }

    private void closePstmt(PreparedStatement pstmt) {
        if (pstmt != null)
            try {
                logger.debug("closePstmt：{}", pstmt);
                pstmt.close();
            } catch (SQLException e) {
                logger.error("JDBCUtilException：关闭PreparedStatement异常，" + e.getMessage(), e);
            }
    }

    private void closeConn(Connection conn) {
        if (conn != null)
            try {
                logger.debug("closeConn：{}", conn);
                conn.close();
            } catch (SQLException e) {
                logger.error("JDBCUtilException：关闭Connection异常，" + e.getMessage(), e);
            }
    }

    private void closeDataSource() {
        if (dataSource != null) {
            logger.info("关闭连接池：{}", dataSource);
            BasicDataSource bdataSource = (BasicDataSource) dataSource;
            try {
                bdataSource.close();
            } catch (SQLException e) {
                logger.error("JDBCUtilException：关闭连接池异常，" + e.getMessage(), e);
            }
        }
    }

    public void close() {
        if (dbBean.isPool()) {
            closeDataSource();
        }
    }

    @Override
    protected void finalize() {
        close();
    }

    public int getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(int batchNum) {
        this.batchNum = batchNum;
    }

    public boolean isThrow() {
        return isThrow;
    }

    public void setThrow(boolean aThrow) {
        isThrow = aThrow;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public DBBean getDbBean() {
        return dbBean;
    }

    /**
     * 根据字段类型拼接
     *
     * @param dstFieldType 字段类型
     * @param fieldValue   字段值
     * @return
     */
    private String stmtSetValue(String dstFieldType, Object fieldValue) {
        String result;
        // 判断目标字段类型
        switch (dstFieldType) {
            case "java.lang.String":
                if (fieldValue == null) result = "" + fieldValue;
                else result = "'" + fieldValue.toString() + "'";
                break;
            case "java.lang.Integer":
            case "int":
                result = "" + fieldValue;
                break;
            case "java.lang.Long":
            case "long":
                result = "" + fieldValue;
                break;
            case "java.math.BigDecimal":
                result = "" + fieldValue;
                break;
            case "java.lang.Boolean":
            case "boolean":
                result = "" + fieldValue;
                break;
            case "java.sql.Timestamp":
                if (fieldValue == null) result = "" + fieldValue;
                else result = "'" + fieldValue.toString() + "'";
                break;
            case "java.sql.Time":
                if (fieldValue == null) result = "" + fieldValue;
                else result = "'" + fieldValue.toString() + "'";
                break;
            case "java.sql.Date":
                if (fieldValue == null) result = "" + fieldValue;
                else result = "'" + fieldValue.toString() + "'";
                break;
            case "java.util.Date":
                if (fieldValue == null) result = "" + fieldValue;
                else result = "'" + fieldValue.toString() + "'";
                break;
            default:
                result = "" + fieldValue;
                break;
        }
        return result;
    }

    /**
     * 根据字段类型判断，进行值转换并设置到PreparedStatement中
     *
     * @param pstmt
     * @param dstFieldType
     * @param parameterIndex
     * @param fieldValue
     * @throws SQLException
     */
    private void pstmtSetValue(PreparedStatement pstmt, String dstFieldType, int parameterIndex,
                               Object fieldValue) throws SQLException {
        pstmtSetValue(pstmt, dstFieldType, null, parameterIndex, fieldValue);
    }

    /**
     * 根据字段类型判断，进行值转换并设置到PreparedStatement中
     *
     * @param pstmt
     * @param dstFieldType
     * @param srcFieldType
     * @param parameterIndex
     * @param fieldValue
     * @throws SQLException
     */
    private void pstmtSetValue(PreparedStatement pstmt, String dstFieldType, String srcFieldType,
                               int parameterIndex, Object fieldValue) throws SQLException {
        // 判断目标字段类型
        switch (dstFieldType) {
            case "java.lang.String":
                pstmt.setString(parameterIndex, (String) fieldValue);
                break;
            case "java.lang.Integer":
            case "int":
                if (!ifNullSet(pstmt, parameterIndex, dstFieldType, fieldValue))
                    pstmt.setInt(parameterIndex, (Integer) fieldValue);
                break;
            case "java.lang.Long":
            case "long":
                if (!ifNullSet(pstmt, parameterIndex, dstFieldType, fieldValue))
                    pstmt.setBigDecimal(parameterIndex, BigDecimal.valueOf((Long) fieldValue));
                break;
            case "java.math.BigDecimal":
                if (srcFieldType != null && !srcFieldType.equals(dstFieldType)) {
                    if (srcFieldType.equals("java.lang.Integer") || srcFieldType.equals("int")) {
                        if (!ifNullSet(pstmt, parameterIndex, srcFieldType, fieldValue))
                            pstmt.setBigDecimal(parameterIndex, BigDecimal.valueOf((Integer) fieldValue));
                    } else if (srcFieldType.equals("java.lang.Long") || srcFieldType.equals("long")) {
                        if (!ifNullSet(pstmt, parameterIndex, srcFieldType, fieldValue))
                            pstmt.setBigDecimal(parameterIndex, BigDecimal.valueOf((Long) fieldValue));
                    } else {
                        throw new SQLException("无法转换的类型，srcFieldType：" + srcFieldType + "，dstFieldType：" + dstFieldType);
                    }
                } else {
                    if (!ifNullSet(pstmt, parameterIndex, dstFieldType, fieldValue))
                        pstmt.setBigDecimal(parameterIndex, (BigDecimal) fieldValue);
                }
                break;
            case "java.sql.Timestamp":
                pstmt.setTimestamp(parameterIndex, (Timestamp) fieldValue);
                break;
            case "java.sql.Time":
                pstmt.setTime(parameterIndex, (Time) fieldValue);
                break;
            case "java.sql.Date":
                pstmt.setDate(parameterIndex, (Date) fieldValue);
                break;
            case "java.util.Date":
                pstmt.setDate(parameterIndex, fieldValue == null ? null : new Date(((java.util.Date) fieldValue).getTime()));
                break;
            default:
                pstmt.setObject(parameterIndex, fieldValue);
                break;
        }
    }

    /**
     * 如果为空就设置Object，并返回true，否则返回false
     *
     * @param pstmt
     * @param parameterIndex
     * @param fieldValue
     * @return
     * @throws SQLException
     */
    private boolean ifNullSet(PreparedStatement pstmt, int parameterIndex, String dstFieldType, Object fieldValue) throws SQLException {
        if (fieldValue == null) {
            // 判断目标字段类型
            switch (dstFieldType) {
                case "java.lang.String":
                    pstmt.setNull(parameterIndex, Types.VARCHAR);
                    break;
                case "java.lang.Integer":
                case "int":
                    pstmt.setNull(parameterIndex, Types.INTEGER);
                    break;
                case "java.lang.Long":
                case "long":
                    pstmt.setNull(parameterIndex, Types.DECIMAL);
                    break;
                case "java.math.BigDecimal":
                    pstmt.setNull(parameterIndex, Types.DECIMAL);
                    break;
                case "java.sql.Timestamp":
                    pstmt.setNull(parameterIndex, Types.TIMESTAMP);
                    break;
                case "java.sql.Time":
                    pstmt.setNull(parameterIndex, Types.TIME);
                    break;
                case "java.sql.Date":
                case "java.util.Date":
                    pstmt.setNull(parameterIndex, Types.DATE);
                    break;
                default:
                    pstmt.setObject(parameterIndex, fieldValue);
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * 查询结果设置到java bean中
     *
     * @param rsMeta
     * @param propertyDescriptors
     * @param rs
     * @param beanCls
     * @param <T>
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    private <T> T queryResultSet(ResultSetMetaData rsMeta, PropertyDescriptor[] propertyDescriptors,
                                 ResultSet rs, Class<T> beanCls) throws SQLException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        T t = beanCls.newInstance();
        for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
            String ColumnLabel = rsMeta.getColumnLabel(i + 1).toUpperCase();
            Object value = rs.getObject(i + 1);
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName().toUpperCase();
                if (ColumnLabel.equals(key)) {
                    Method setter = property.getWriteMethod();// Java中提供了用来访问某个属性的
                    String propertyName = property.getPropertyType().getName();// 获取bean字段的类型
                    // setter方法，oracle一般把数值型转换成BigDecimal，所以这里需要转换
                    if (propertyName.equals("int")) {
                        if (value == null) {
                            setter.invoke(t, 0);
                        } else {
                            setter.invoke(t, Integer.valueOf(value.toString()));
                        }
                    } else if (propertyName.equals("long")) {
                        if (value == null) {
                            setter.invoke(t, 0);
                        } else {
                            setter.invoke(t, Long.valueOf(value.toString()));
                        }
                    } else if (propertyName.equals("java.lang.Long")) {
                        if (value == null) {
                            setter.invoke(t, 0L);
                        } else {
                            setter.invoke(t, Long.valueOf(value.toString()));
                        }
                    } else if (propertyName.equals("java.sql.Date")) {
                        if (value instanceof Timestamp) {
                            setter.invoke(t, new Date(((Timestamp) value).getTime()));
                        }
                    } else {
                        setter.invoke(t, value);
                    }
                    break;
                }
            }
        }
        return t;
    }

    /**
     * 查询结果设置到List<QueryResult>中
     *
     * @param rsMeta
     * @param rs
     * @return
     * @throws SQLException
     */
    public List<QueryResult> buildQueryResult(ResultSetMetaData rsMeta, ResultSet rs) throws SQLException {
        List<QueryResult> queryResults = new ArrayList<>();
        for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
            int column = i + 1;
            String ColumnName = rsMeta.getColumnName(column);
            String ColumnLabel = rsMeta.getColumnLabel(column);
            int ColumnType = rsMeta.getColumnType(column);
            String ColumnClassName = rsMeta.getColumnClassName(column);
            String ColumnTypeName = rsMeta.getColumnTypeName(column);
            int ColumnDisplaySize = rsMeta.getColumnDisplaySize(column);
            int Precision = rsMeta.getPrecision(column);
            int Scale = rsMeta.getScale(column);
            String SchemaName = rsMeta.getSchemaName(column);
            String CatalogName = rsMeta.getCatalogName(column);
            Object value = null;
            if (rs != null) {
                value = rs.getObject(column);
            }
            QueryResult queryResult = new QueryResult();
            queryResult.setColumnName(ColumnName);
            queryResult.setColumnLabel(ColumnLabel);
            queryResult.setColumnType(ColumnType);
            queryResult.setColumnClassName(ColumnClassName);
            queryResult.setColumnTypeName(ColumnTypeName);
            queryResult.setColumnDisplaySize(ColumnDisplaySize);
            queryResult.setPrecision(Precision);
            queryResult.setScale(Scale);
            queryResult.setSchemaName(SchemaName);
            queryResult.setCatalogName(CatalogName);
            if (rs != null) {
                queryResult.setValue(value);
            }
            queryResults.add(queryResult);
        }
        return queryResults;
    }

    private int getSuccess(int[] ret) {
        int rets = 0;
        for (int r : ret) {
            rets += r;
        }
        return rets;
    }

    /**
     * 找到空格、括号等等就返回
     *
     * @param param 内容
     * @param key   关键字
     * @param i     关键字List下标
     * @return 结果
     */
    public String getParam(String param, String key, int i) {
        if (param != null && param.length() > 0) {
            int index = param.indexOf(key);
            if (index > 0) {
                return param.substring(0, index);
            } else {
                i++;
                if (keyList.size() > i) return getParam(param, keyList.get(i), i);
            }
        }
        return null;
    }

    /**
     * 判断结尾字符是否异常
     *
     * @param param 内容
     * @return 结果
     */
    public boolean paramEndWith(String param) {
        if (param == null) return false;
        for (String endStr : endList) {
            if (param.trim().endsWith(endStr)) return false;
        }
        return true;
    }
}
