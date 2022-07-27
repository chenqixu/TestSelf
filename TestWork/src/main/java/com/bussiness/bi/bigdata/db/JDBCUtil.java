package com.bussiness.bi.bigdata.db;

import com.bussiness.bi.bigdata.changecode.FileUtil;
import com.bussiness.bi.bigdata.db.impl.WriteResultSetDeal;
import com.cqx.bean.CmdBean;
import com.cqx.process.CmdTool;
import com.bussiness.bi.bigdata.db.impl.IResultSetDeal;
import com.bussiness.bi.bigdata.db.impl.PrintResultSetDeal;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC工具
 *
 * @author chenqixu
 */
public class JDBCUtil {

    protected Map<String, String> driverMap = new HashMap<>();
    protected Connection conn = null;
    protected Statement stm = null;
    protected PreparedStatement pstmt = null;
    protected CmdBean cmdBean = null;

    public JDBCUtil() {
        loadDriver();
    }

    public JDBCUtil(CmdBean cmdBean) {
        this();
        this.cmdBean = cmdBean;
        try {
            String DriverClassName = driverMap.get(cmdBean.getType());
            Class.forName(DriverClassName);
            DriverManager.setLoginTimeout(15); // 超时
            conn = DriverManager.getConnection(cmdBean.getDns(), cmdBean.getUsername(), cmdBean.getPassword());
            CmdTool.println("conn success! conn is " + conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行sql查询语句，返回结果
     *
     * @param sql
     * @return ResultSet
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        ResultSet rs;
        try {
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
        } finally {
            closeStm();
        }
        if (rs == null) {
            return null;
        }
        return rs;
    }

    /**
     * 执行更新语句，返回执行结果
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public int executeUpdate(String sql) throws SQLException {
        int result = -1;
        try {
            stm = conn.createStatement();
            result = stm.executeUpdate(sql);
        } finally {
            closeStm();
        }
        return result;
    }

    /**
     * 通过掺入List参数来进行相关组件的操作
     *
     * @param sql
     * @param params
     * @throws SQLException
     */
    public long executeBatch(String sql, List<List<String>> params) throws SQLException {
        long count = 0;
        try {
            conn.setAutoCommit(false);// 关闭自动提交
            if (pstmt == null)
                pstmt = conn.prepareStatement(sql);// 预编译SQL
            for (List<String> param : params) {
                int index = 1;
                for (String p : param) {
                    if (p == null || "".equals(p)) {
                        pstmt.setObject(index++, null);
                    } else {
                        pstmt.setString(index++, p);
                    }
                }
                pstmt.addBatch();
            }
            int[] cnt = pstmt.executeBatch();
            conn.commit();
            count = arraySum(cnt);
        } catch (SQLException e) {
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw e;
        }
        return count;
    }

    /**
     * <pre>
     *     desc tablename
     *     export d:/test.txt as select * from a
     *     import d:/test.txt as insert into a
     * </pre>
     *
     * @param sql
     * @throws SQLException
     */
    public void parserSql(String sql) throws Exception {
        String newSql = sql.trim();
        if (newSql.startsWith("desc")) {
            newSql = desc(cmdBean.getUsername(), newSql.replace("desc", "").trim());
            printlnResultSet(executeQuery(newSql));
        } else if (newSql.startsWith("export")) {
            //去掉export
            //使用" as "切割
            String _sql = newSql.replace("export", "").trim();
            String[] arr = _sql.split(" as ");
            if (arr.length == 2) {
                exportData(arr[0], arr[1]);
            }
        } else if (newSql.startsWith("import")) {
            //去掉import
            //使用" as "切割
            String _sql = newSql.replace("import", "").trim();
            String[] arr = _sql.split(" as ");
            if (arr.length == 2) {
                importData(arr[0], arr[1]);
            }
        } else if (newSql.startsWith("select")) {
            printlnResultSet(executeQuery(newSql));
        } else if (newSql.startsWith("insert")) {
            printlnUpdateResult(executeUpdate(newSql));
        } else {
            String reason = "无法识别的语句，sql：" + newSql;
            String SQLState = "err";
            int vendorCode = -1;
            throw new SQLException(reason, SQLState, vendorCode);
        }
    }

    public void exportData(String filename, String sql) throws Exception {
        FileUtil fileUtil = new FileUtil();
        //创建文件
        fileUtil.createFile(filename, "UTF-8");
        //执行sql语句并将结果写入文件
        int count = writeResultSet(executeQuery(sql), fileUtil);
        //关闭文件
        fileUtil.closeWrite();
        CmdTool.println("success save data to " + filename + "，file count is " + count);
    }

    public void importData(String filename, String sql) throws Exception {
        FileUtil fileUtil = new FileUtil();
        //读取文件
        fileUtil.getFile(filename, "UTF-8");
        //生成sql
        List<String> sqllist = fileUtil.read(sql);
        //关闭文件
        fileUtil.closeRead();
        //执行sql
        int count = 0;
        int fail = 0;
        for (String str : sqllist) {
            CmdTool.debug("sql语句：" + str);
            int result = printlnUpdateResult(executeUpdate(str));
            if (result == 0) count++;
            else fail++;
        }
        CmdTool.println("success import data to " + sql + "，success count is " + count + "，fail count is " + fail);
    }

    public String desc(String owner, String tablename) {
        StringBuffer sb = new StringBuffer();
        sb.append("select column_name||' '||data_type||'('||data_length||')' from ALL_TAB_COLUMNS ")
                .append("where table_name=upper('")
                .append(tablename)
                .append("') and owner=upper('")
                .append(owner)
                .append("')");
        return sb.toString();
    }

    public int printlnUpdateResult(int result) {
        CmdTool.println("执行结果：" + result);
        return result;
    }

    public void printlnResultSet(ResultSet rs) throws SQLException {
        dealResultSet(rs, 5, new PrintResultSetDeal());
    }

    public int writeResultSet(ResultSet rs, FileUtil fileUtil) throws SQLException {
        return dealResultSet(rs, 0, new WriteResultSetDeal(fileUtil));
    }

    public <T extends IResultSetDeal> int dealResultSet(ResultSet rs, int limit, T t) throws SQLException {
        int cnt = 0;
        if (rs != null) {
            while (rs.next() && isLimit(cnt, limit)) {
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    t.execValue(rs.getString(i + 1));
                    if (i < (rs.getMetaData().getColumnCount() - 1))
                        t.execValueSplit();
                }
                t.execValueEnd();
                cnt++;
            }
        }
        return cnt;
    }

    private boolean isLimit(int cnt, int limit) {
        if (limit > 0) {//有限制
            return cnt < limit;
        } else {//无限制
            return true;
        }
    }

    private void closeConn() {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    private void closeStm() {
        if (stm != null)
            try {
                stm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    private void closeStmt() {
        try {
            if (pstmt != null)
                pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeAll() {
        closeStm();
        closeStmt();
        closeConn();
    }

    private long arraySum(int[] arr) {
        if (arr == null) {
            return 0;
        }
        long res = 0;
        for (int i : arr) {
            if (i > 0) {
                res += i;
            }
        }
        return res;
    }

    private void loadDriver() {
        driverMap.put("oracle", "oracle.jdbc.driver.OracleDriver");
        driverMap.put("mysql", "com.mysql.jdbc.Driver");
        driverMap.put("redis", "com.cqx.common.utils.redis.RedisDriver");
        driverMap.put("hive", "org.apache.hive.jdbc.HiveDriver");
        driverMap.put("timesten", "TimestenJDBCTest");
    }

    protected void finalize() {
        closeAll();
    }
}
