package com.cqx.sync;

import com.cqx.sync.bean.BatchBean;
import com.cqx.sync.bean.BeanUtil;
import com.cqx.sync.bean.DBBean;
import com.cqx.sync.bean.QueryResult;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBCUtil
 *
 * @author chenqixu
 */
public class JDBCUtil {

    private static Logger logger = LoggerFactory.getLogger(JDBCUtil.class);
    //    private Connection conn = null;
    private Statement stm = null;
    private PreparedStatement pstmt = null;
    private DataSource dataSource;
    private List<String> keyList = new ArrayList<>();
    private List<String> endList = new ArrayList<>();

    public JDBCUtil(DBBean dbBean) {
        keyList.add(")");
        keyList.add("%");
        keyList.add(",");
        keyList.add(" ");

        endList.add("'");

        try {
//            String DriverClassName = dbBean.getDbType().getDriver();
//            Class.forName(DriverClassName);
//            DriverManager.setLoginTimeout(15); // 超时
//            conn = DriverManager.getConnection(dbBean.getTns(),
//                    dbBean.getUser_name(), dbBean.getPass_word());
            dataSource = setupDataSource(dbBean.getDbType().getDriver(),
                    dbBean.getUser_name(), dbBean.getPass_word(), dbBean.getTns());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 创建数据源
     *
     * @param driver
     * @param username
     * @param password
     * @param url
     * @return
     */
    private DataSource setupDataSource(String driver, String username,
                                       String password, String url) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driver);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setUrl(url);
        // 最大活动连接
        ds.setMaxActive(5);
        // 最小空闲连接
        ds.setMinIdle(2);
        // 最大空闲连接
        ds.setMaxIdle(3);
//		ds.setValidationQuery("select 1 from dual");
//		ds.setValidationQueryTimeout(1000);
//		ds.setTestOnBorrow(false);
//		ds.setTestWhileIdle(true);
//		ds.setTimeBetweenEvictionRunsMillis(15000);
        return ds;
    }

    /**
     * 获取连接
     *
     * @return
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        return null;
    }

    /**
     * 通过表名构造表对象
     *
     * @param fields
     * @param table_name
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public BeanUtil generateBeanByTabeNameAndFields(String fields, String table_name) throws SQLException, ClassNotFoundException {
        ResultSet rs;
        BeanUtil beanUtil;
        LinkedHashMap properties = new LinkedHashMap();
        Connection conn = null;
        try {
            conn = getConnection();
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
            closeStm();
            closeConn(conn);
        }
        return beanUtil;
    }

    /**
     * 通过表名构造表对象
     *
     * @param table_name
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public BeanUtil generateBeanByTabeName(String table_name) throws SQLException, ClassNotFoundException {
        ResultSet rs;
        BeanUtil beanUtil;
        LinkedHashMap properties = new LinkedHashMap();
        Connection conn = null;
        try {
            conn = getConnection();
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
            closeStm();
            closeConn(conn);
        }
        return beanUtil;
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql
     * @return ResultSet
     */
    public <T> List<T> executeQuery(String sql, Class<T> beanCls) {
        ResultSet rs;
        T t;
        List<T> tList = new ArrayList<>();
        Connection conn = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            conn = getConnection();
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                t = beanCls.newInstance();
                for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
                    String ColumnLabel = rsMeta.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    for (PropertyDescriptor property : propertyDescriptors) {
                        String key = property.getName();
                        if (ColumnLabel.contains(key)) {
                            Method setter = property.getWriteMethod();// Java中提供了用来访问某个属性的
                            // getter/setter方法
                            setter.invoke(t, value);
                            break;
                        }
                    }
                }
                tList.add(t);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            tList = null;
        } finally {
            closeStm();
            closeConn(conn);
        }
        return tList;
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql
     * @return
     */
    public List<List<QueryResult>> executeQuery(String sql) {
        ResultSet rs;
        List<List<QueryResult>> tList = new ArrayList<>();
        Connection conn = null;
        try {
            conn = getConnection();
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                List<QueryResult> queryResults = new ArrayList<>();
                for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
                    int column = i + 1;
                    String ColumnName = rsMeta.getColumnName(column);
                    String ColumnLabel = rsMeta.getColumnLabel(column);
                    int ColumnType = rsMeta.getColumnType(column);
                    String ColumnClassName = rsMeta.getColumnClassName(column);
                    String ColumnTypeName = rsMeta.getColumnTypeName(column);
                    Object value = rs.getObject(column);
                    QueryResult queryResult = new QueryResult();
                    queryResult.setColumnName(ColumnName);
                    queryResult.setColumnLabel(ColumnLabel);
                    queryResult.setColumnType(ColumnType);
                    queryResult.setColumnClassName(ColumnClassName);
                    queryResult.setColumnTypeName(ColumnTypeName);
                    queryResult.setValue(value);
                    queryResults.add(queryResult);
                }
                tList.add(queryResults);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            tList = null;
        } finally {
            closeStm();
            closeConn(conn);
        }
        return tList;
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql
     * @return ResultSet
     */
    public <T> List<T> executeQuery(String sql, Class<T> beanCls, List<Object> paramList) {
        ResultSet rs;
        T t;
        List<T> tList = new ArrayList<>();
        Connection conn = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 1; i <= paramList.size(); i++) {
                Object queryParam = paramList.get(i);
                pstmt.setObject(i, queryParam);
            }
            rs = pstmt.executeQuery();//注意，上面已经绑定变量了，这里不需要再传sql
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                t = beanCls.newInstance();
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
                tList.add(t);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            tList = null;
        } finally {
            closeStm();
            closeConn(conn);
        }
        return tList;
    }

    /**
     * 找到空格、括号等等就返回
     *
     * @param param
     * @param key
     * @param i
     * @return
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
     * @param param
     * @return
     */
    public boolean paramEndWith(String param) {
        if (param == null) return false;
        for (String endStr : endList) {
            if (param.trim().endsWith(endStr)) return false;
        }
        return true;
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql
     * @param paramObject
     * @param beanCls
     * @param <T>
     * @return
     */
    public <T> List<T> executeQuery(String sql, Object paramObject, Class<T> beanCls) {
        ResultSet rs;
        T t;
        List<T> tList = new ArrayList<>();
        Connection conn = null;

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
                        if (propertyName.equals("int")) {
                            if (value == null) {
                                pstmt.setInt(parameterIndex, 0);
                            } else {
                                pstmt.setInt(parameterIndex, (Integer) value);
                            }
                        } else if (propertyName.equals("long") || propertyName.equals("java.lang.Long")) {
                            if (value == null) {
                                pstmt.setLong(parameterIndex, 0L);
                            } else {
                                pstmt.setLong(parameterIndex, (Long) value);
                            }
                        } else if (propertyName.equals("java.sql.Timestamp")) {
                            if (value == null) {
                                pstmt.setTimestamp(parameterIndex, null);
                            } else {
                                pstmt.setTimestamp(parameterIndex, (java.sql.Timestamp) value);
                            }
                        } else if (propertyName.equals("java.sql.Date")) {
                            if (value == null) {
                                pstmt.setDate(parameterIndex, null);
                            } else {
                                pstmt.setDate(parameterIndex, (java.sql.Date) value);
                            }
                        } else if (propertyName.equals("java.lang.String")) {
                            if (value == null) {
                                pstmt.setString(parameterIndex, null);
                            } else {
//                                if (param.isFrontLike()) {
//                                    pstmt.setString(parameterIndex, "%" + value);
//                                } else if (param.isBehindLike()) {
//                                    pstmt.setString(parameterIndex, value + "%");
//                                } else if (param.isFrontLike() && param.isBehindLike()) {
//                                    pstmt.setString(parameterIndex, "%" + value + "%");
//                                } else {
                                pstmt.setString(parameterIndex, (String) value);
//                                }
                            }
                        } else {
                            pstmt.setObject(parameterIndex, value);
                        }
                        break;
                    }
                }
            }
            rs = pstmt.executeQuery();
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                t = beanCls.newInstance();
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
                tList.add(t);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            tList = null;
        } finally {
            closeStm();
            closeConn(conn);
        }
        return tList;
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql
     * @return ResultSet
     */
    public <T> List<T> executeQuery(String sql, Class<T> beanCls, Map<String, ?> paramMap) {
        return null;
//        ResultSet rs;
//        T t;
//        List<T> tList = new ArrayList<>();
//        Connection conn = null;
//        try {
//            BeanInfo beanInfo = Introspector.getBeanInfo(beanCls);
//            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//            conn = getConnection();
//            pstmt = conn.prepareStatement(sql);
//            for (int i = 1; i <= paramList.size(); i++) {
//                Object queryParam = paramList.get(i);
//                pstmt.setObject(i, queryParam);
//            }
//            rs = pstmt.executeQuery(sql);
//            ResultSetMetaData rsMeta = rs.getMetaData();
//            while (rs.next()) {
//                t = beanCls.newInstance();
//                for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
//                    String ColumnLabel = rsMeta.getColumnLabel(i + 1).toUpperCase();
//                    Object value = rs.getObject(i + 1);
//                    for (PropertyDescriptor property : propertyDescriptors) {
//                        String key = property.getName().toUpperCase();
//                        if (ColumnLabel.equals(key)) {
//                            Method setter = property.getWriteMethod();// Java中提供了用来访问某个属性的
//                            String propertyName = property.getPropertyType().getName();// 获取bean字段的类型
//                            // setter方法，oracle一般把数值型转换成BigDecimal，所以这里需要转换
//                            if (propertyName.equals("int")) {
//                                if (value == null) {
//                                    setter.invoke(t, 0);
//                                } else {
//                                    setter.invoke(t, Integer.valueOf(value.toString()));
//                                }
//                            } else if (propertyName.equals("long")) {
//                                if (value == null) {
//                                    setter.invoke(t, 0);
//                                } else {
//                                    setter.invoke(t, Long.valueOf(value.toString()));
//                                }
//                            } else if (propertyName.equals("java.lang.Long")) {
//                                if (value == null) {
//                                    setter.invoke(t, 0L);
//                                } else {
//                                    setter.invoke(t, Long.valueOf(value.toString()));
//                                }
//                            } else if (propertyName.equals("java.sql.Date")) {
//                                if (value instanceof Timestamp) {
//                                    setter.invoke(t, new Date(((Timestamp) value).getTime()));
//                                }
//                            } else {
//                                setter.invoke(t, value);
//                            }
//                            break;
//                        }
//                    }
//                }
//                tList.add(t);
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            tList = null;
//        } finally {
//            closeStm();
//            closeConn(conn);
//        }
//        return tList;
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
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stm = conn.createStatement();
            result = stm.executeUpdate(sql);
            conn.commit();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            result = -1;
            if (conn != null)
                conn.rollback();
        } finally {
            closeStm();
            closeConn(conn);
        }
        return result;
    }

    /**
     * 执行更新语句，返回执行结果
     *
     * @param sql
     * @param tList
     * @param dstFieldsType
     * @return
     */
    public int executeBatch(String sql, List<List<QueryResult>> tList, List<String> dstFieldsType) throws SQLException {
        int ret;
        int commit_cnt = 0;
        int success_cnt = 0;
        Connection conn = null;
        try {
            conn = getConnection();
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
                    // 目标字段类型
                    switch (dstFieldType) {
                        case "java.lang.String":
                            pstmt.setString(parameterIndex, (String) fieldValue);
                            break;
                        case "java.sql.Timestamp":
                            pstmt.setTimestamp(parameterIndex, (Timestamp) fieldValue);
                            break;
                        case "java.math.BigDecimal":
                            if (!srcFieldType.equals(dstFieldType)) {
                                if (srcFieldType.equals("java.lang.Integer")) {
                                    pstmt.setBigDecimal(parameterIndex, BigDecimal.valueOf((Integer) fieldValue));
                                } else if (srcFieldType.equals("java.lang.Long")) {
                                    pstmt.setBigDecimal(parameterIndex, BigDecimal.valueOf((Long) fieldValue));
                                } else {
                                    throw new Exception("无法转换的类型，srcFieldType：" + srcFieldType + "，dstFieldType：" + dstFieldType);
                                }
                            } else {
                                pstmt.setBigDecimal(parameterIndex, (BigDecimal) fieldValue);
                            }
                            break;
                        case "java.sql.Time":
                            pstmt.setTime(parameterIndex, (Time) fieldValue);
                            break;
                        case "java.lang.Integer":
                            pstmt.setInt(parameterIndex, (Integer) fieldValue);
                            break;
                        default:
                            pstmt.setObject(parameterIndex, fieldValue);
                            break;
                    }
                }
                pstmt.addBatch();
                commit_cnt++;
                // 2000条提交一次
                if (commit_cnt % 2000 == 0) {
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
            if (conn != null)
                conn.rollback();
        } finally {
            closePstmt();
            closeConn(conn);
        }
        return ret;
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
     * @return
     */
    public <T> int executeBatch(String sql, List<T> tList, Class<T> srcBeanCls, Class<T> dstBeanCls, LinkedHashMap<String, Object> dstFieldsMap) throws SQLException {
        int ret = 0;
        int commit_cnt = 0;
        int success_cnt = 0;
        Connection conn = null;
        try {
            // oracle关键字
            if (sql.contains(",user,")) {
                sql = sql.replace(",user,", ",\"USER\",");
                logger.info("oracle关键字替换，sql：{}", sql);
            }
            conn = getConnection();
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
                    switch (dstFieldType.getName()) {
                        case "java.lang.String":
                            pstmt.setString(i, (String) fieldValue);
                            break;
                        case "java.sql.Timestamp":
                            pstmt.setTimestamp(i, (Timestamp) fieldValue);
                            break;
                        case "java.math.BigDecimal":
                            if (srcFieldType.getName() != dstFieldType.getName()) {
                                if (srcFieldType.getName().equals("java.lang.Integer")) {
                                    pstmt.setBigDecimal(i, BigDecimal.valueOf((Integer) fieldValue));
                                } else if (srcFieldType.getName().equals("java.lang.Long")) {
                                    pstmt.setBigDecimal(i, BigDecimal.valueOf((Long) fieldValue));
                                } else {
                                    throw new Exception("无法转换的类型，srcFieldType：" + srcFieldType.getName() + "，dstFieldType：" + dstFieldType.getName());
                                }
                            } else {
                                pstmt.setBigDecimal(i, (BigDecimal) fieldValue);
                            }
                            break;
                        case "java.sql.Time":
                            pstmt.setTime(i, (Time) fieldValue);
                            break;
                        case "java.lang.Integer":
                            pstmt.setInt(i, (Integer) fieldValue);
                            break;
                        default:
                            pstmt.setObject(i, fieldValue);
                            break;
                    }
                    i++;
                }
                pstmt.addBatch();
                commit_cnt++;
                // 2000条提交一次
                if (commit_cnt % 2000 == 0) {
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
            if (conn != null)
                conn.rollback();
        } finally {
            closePstmt();
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
     * @param <T>
     * @return
     */
    public <T> int executeBatch(String sql, List<T> tList, Class<T> beanCls, String fields) throws SQLException {
        int commit_cnt = 0;
        int success_cnt = 0;
        Connection conn = null;
        String[] fields_arr = fields.split(",", -1);
        try {
            conn = getConnection();
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
                    switch (entry.getValue().getName()) {
                        case "java.lang.String":
                            pstmt.setString(i, (String) fieldValue);
                            break;
                        case "java.sql.Timestamp":
                            pstmt.setTimestamp(i, (Timestamp) fieldValue);
                            break;
                        case "java.lang.Long":
                            pstmt.setBigDecimal(i, BigDecimal.valueOf((Long) fieldValue));
                            break;
                        case "java.sql.Time":
                            pstmt.setTime(i, (Time) fieldValue);
                            break;
                        case "java.lang.Integer":
                            pstmt.setInt(i, (Integer) fieldValue);
                            break;
                        default:
                            pstmt.setObject(i, fieldValue);
                            break;
                    }
                    i++;
                }
                pstmt.addBatch();
                commit_cnt++;
                // 2000条提交一次
                if (commit_cnt % 2000 == 0) {
                    pstmt.executeBatch();
                    conn.commit();
                }
                success_cnt++;
            }
            // 剩余数据提交
            pstmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            success_cnt = -1;
            if (conn != null)
                conn.rollback();
        } finally {
            closePstmt();
            closeConn(conn);
        }
        return success_cnt;
    }

    private void closeStm() {
        if (stm != null)
            try {
                stm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    private void closePstmt() {
        try {
            if (pstmt != null)
                pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeConn(Connection conn) {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void closeDataSource() {
        if (dataSource != null) {
            BasicDataSource bdataSource = (BasicDataSource) dataSource;
            try {
                bdataSource.close();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

//    private void closeConn() {
//        if (conn != null)
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//    }

    public void closeAll() {
        closeStm();
        closePstmt();
//        closeConn();
//        closeDataSource();
    }

    @Override
    protected void finalize() {
        closeAll();
        closeDataSource();
    }

}
