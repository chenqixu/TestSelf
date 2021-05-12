package com.cqx.common.utils.jdbc;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * IJDBCUtil
 *
 * @author chenqixu
 */
public interface IJDBCUtil extends IJDBCUtilCall {
    /**
     * 获取表的元数据
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    List<QueryResult> getTableMetaData(String tableName) throws SQLException;

    /**
     * 通过字段、表名构造表对象
     *
     * @param fields
     * @param table_name
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    BeanUtil generateBeanByTabeNameAndFields(String fields, String table_name) throws SQLException, ClassNotFoundException;

    /**
     * 通过表名构造表对象
     *
     * @param table_name
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    BeanUtil generateBeanByTabeName(String table_name) throws SQLException, ClassNotFoundException;

    /**
     * 根据sql查询，查询结果进行回调处理，用的是ResultSet
     *
     * @param sql
     * @param iCallBack
     * @throws SQLException
     * @see ResultSet
     */
    void executeQuery(String sql, ICallBack iCallBack) throws SQLException;

    /**
     * 根据sql查询，查询结果进行回调处理，用的是传入的javabean
     *
     * @param sql
     * @param beanCls
     * @param iBeanCallBack
     * @param <T>
     * @throws Exception
     * @see T
     */
    <T> void executeQuery(String sql, Class<T> beanCls, IBeanCallBack<T> iBeanCallBack) throws Exception;

    /**
     * 根据sql查询，查询结果进行回调处理，用的是QueryResult
     *
     * @param sql
     * @param iQueryResultCallBack
     * @throws Exception
     * @see QueryResult
     */
    void executeQuery(String sql, IQueryResultCallBack iQueryResultCallBack) throws Exception;

    /**
     * 根据sql查询，返回List<List<QueryResult>>
     *
     * @param sql
     * @return
     * @throws SQLException
     * @see QueryResult
     */
    List<List<QueryResult>> executeQuery(String sql) throws SQLException;

    /**
     * 根据sql查询，返回的是传入的javabean
     *
     * @param sql
     * @param beanCls
     * @param <T>
     * @return
     * @throws Exception
     * @see T
     */
    <T> List<T> executeQuery(String sql, Class<T> beanCls) throws Exception;

    /**
     * 根据sql查询，返回的是传入的javabean，带where参数，条件要按顺序写入paramList
     *
     * @param sql
     * @param beanCls
     * @param paramList
     * @param <T>
     * @return
     * @throws Exception
     * @see T
     */
    <T> List<T> executeQuery(String sql, Class<T> beanCls, List<Object> paramList) throws Exception;

    /**
     * 根据sql查询，返回的是传入的javabean，带where参数，条件通过解析sql然后去javabean中获取对应的值<br>
     * 支持：select a from b where a=:a and b=:b and c>=:c and (d=:d)
     *
     * @param sql
     * @param paramObject
     * @param beanCls
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> List<T> executeQuery(String sql, Object paramObject, Class<T> beanCls) throws Exception;

    /**
     * 更新
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    int executeUpdate(String sql) throws SQLException;

    /**
     * 更新，可以设置是否自动提交
     *
     * @param sql
     * @param autoCommit
     * @return
     * @throws SQLException
     */
    int executeUpdate(String sql, boolean autoCommit) throws SQLException;

    /**
     * 批量执行sql
     *
     * @param sqls
     * @return
     * @throws SQLException
     */
    List<Integer> executeBatch(List<String> sqls) throws SQLException;

    /**
     * 批量执行，op_type和QueryResult在一起<br>
     * 仅适用于ogg，有对应的操作类型(i,u,d)<br>
     * 根据操作类型、表名、字段、pks、QueryResult自行拼接sql后，批量执行
     *
     * @param iQueryResultBeanList
     * @param table
     * @param fields
     * @param fields_type
     * @param pks
     * @param pks_type
     * @return
     * @throws SQLException
     */
    List<Integer> executeBatch(List<? extends IQueryResultBean> iQueryResultBeanList, String table,
                               String[] fields, String[] fields_type,
                               String[] pks, String[] pks_type) throws SQLException;

    /**
     * 批量执行，op_type和QueryResult在一起<br>
     * 仅适用于ogg，有对应的操作类型(i,u,d)<br>
     * 根据操作类型、表名、字段、pks、QueryResult自行拼接sql后，批量执行<br>
     * 有ismissing参数，表示字段缺失，不需要拼接入sql语句中
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
     */
    List<Integer> executeBatch(List<? extends IQueryResultBean> iQueryResultBeanList, String table,
                               String[] fields, String[] fields_type,
                               String[] pks, String[] pks_type, boolean ismissing) throws SQLException;

    /**
     * 批量执行，op_type和QueryResult是分开的<br>
     * 仅适用于ogg，有对应的操作类型(i,u,d)<br>
     * 根据操作类型、表名、字段、pks、QueryResult自行拼接sql后，批量执行
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
     */
    List<Integer> executeBatch(List<String> op_types, List<List<QueryResult>> tList,
                               String table, String[] fields, String[] fields_type,
                               String[] pks, String[] pks_type) throws SQLException;

    /**
     * 批量执行，op_type和QueryResult是分开的<br>
     * 仅适用于ogg，有对应的操作类型(i,u,d)<br>
     * 根据操作类型、表名、字段、pks、QueryResult自行拼接sql后，批量执行<br>
     * 有ismissing参数，表示字段缺失，不需要拼接入sql语句中
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
     */
    List<Integer> executeBatch(List<String> op_types, List<List<QueryResult>> tList,
                               String table, String[] fields, String[] fields_type,
                               String[] pks, String[] pks_type, boolean ismissing) throws SQLException;

    /**
     * 批量执行，返回结果(0:成功，-1:失败)<br>
     * 适用于上面是查询，下面是插入的情况
     *
     * @param sql
     * @param tList
     * @param dstFieldsType
     * @return
     * @throws Exception
     */
    int executeBatch(String sql, List<List<QueryResult>> tList, List<String> dstFieldsType) throws Exception;

    /**
     * 批量执行，有源端和目标端之分
     *
     * @param sql
     * @param tList
     * @param srcBeanCls
     * @param dstBeanCls
     * @param dstFieldsMap
     * @param <T>
     * @return
     * @throws SQLException
     */
    <T> int executeBatch(String sql, List<T> tList, Class<T> srcBeanCls, Class<T> dstBeanCls, LinkedHashMap<String, Object> dstFieldsMap) throws SQLException;

    /**
     * 批量执行，返回结果(0:成功，-1:失败)
     *
     * @param sql
     * @param tList
     * @param beanCls
     * @param fields
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> int executeBatch(String sql, List<T> tList, Class<T> beanCls, String fields) throws Exception;

    /**
     * 批量执行，返回全部的执行结果
     *
     * @param sql
     * @param tList
     * @param beanCls
     * @param fields
     * @param hasRet
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> List<Integer> executeBatch(String sql, List<T> tList, Class<T> beanCls, String fields, boolean hasRet) throws Exception;

    /**
     * 关闭数据连接池
     */
    void closeDataSource();

    /**
     * 关闭数据源
     */
    void close();

    /**
     * 设置批量提交数
     *
     * @param batchNum
     */
    void setBatchNum(int batchNum);

    /**
     * 设置是否抛出捕获的异常
     *
     * @param aThrow
     */
    void setThrow(boolean aThrow);

    /**
     * 获取数据源
     *
     * @return
     */
    DataSource getDataSource();

    /**
     * 获取数据库配置bean
     *
     * @return
     */
    DBBean getDbBean();

    /**
     * 获取连接，并进行回调操作
     *
     * @param iConnCallBack
     * @throws Exception
     */
    void getConnection(IConnCallBack iConnCallBack) throws Exception;
}
