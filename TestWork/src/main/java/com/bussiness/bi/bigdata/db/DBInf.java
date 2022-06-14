package com.bussiness.bi.bigdata.db;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//import com.bean.BeanInf;
//import com.bean.DBBean;
//import com.bean.TriggerBean;

public abstract class DBInf {
	private Connection conn = null;
//	private DBBean ibean = null;
//	public DBInf(DBBean bean){
//		ibean = bean;
//		init(ibean);
//	}
	
//	public void init(DBBean bean){
//		try {
//			// 加载数据库驱动类
//			Class.forName(bean.getDriver());
//			conn = DriverManager.getConnection(bean.getUrl(), bean.getUser(), bean.getPasswd());
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//	}

	public Connection getConn() {
		return conn;
	}

	/**
	 * 关闭数据库连接
	 * */
	public void closeConn(){
		closeDB(conn, null, null);
	}
	
	/**
	 * 通过类名获取所有字段
	 * @param beanclassname 示例：com.bean.TriggerBean
	 * */
	protected List<String> getBeanFileds(String beanclassname) throws Exception {
		List<String> resultlist = new ArrayList<String>();
		Class<?>  demo = Class.forName(beanclassname);
    	Field[] fields = demo.getDeclaredFields();
    	for(Field f:fields){
    		resultlist.add(f.getName());
    	}
    	if(resultlist.size()>0)
    		return resultlist;
    	else
    		return null;
	}
	
	/**
	 * 查询表
	 * @throws Exception 
	 * */
//	public List<BeanInf> queryTable(Class<?> c, String tablename) throws Exception{
//		List<BeanInf> result = new ArrayList<BeanInf>();
//		// 通过名称获取bean的所有字段
//		List<String> tbfiles = getBeanFileds(c.getName());
//		StringBuffer sqlbuffer = new StringBuffer("select ");
//		// 组装成sql
//		for(String _tbfiles:tbfiles){
//			sqlbuffer.append(_tbfiles)
//				.append(",");
//		}
//		// 删除最后一个逗号
//		sqlbuffer.deleteCharAt(sqlbuffer.length()-1);
//		sqlbuffer.append(" from ")
//			.append(tablename);
//		String sql = sqlbuffer.toString();
//		// 预编译sql语句声明
//		PreparedStatement pstmt =  null;
//		// 结果集
//		ResultSet rs = null;
//		try{
//			if(conn!=null){
//				// 预编译sql
//				pstmt = conn.prepareStatement(sql);
//				// 查询
//				rs = pstmt.executeQuery();
//				while(rs!=null && rs.next()){
//					// 获得结果
//					BeanInf tb = (BeanInf) Class.forName(c.getName()).newInstance();
//					// 根据结果集插入值
//					tb.setRSValue(rs);
//					result.add(tb);
//				}
//			}
//			// 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
//			closeDB(null, pstmt, rs);
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			// 数据库操作完成后，关闭相关的连接资源，这里不关闭连接
//			closeDB(null, pstmt, rs);
//		}		
//		return result;
//	}
	
	/**
	 * 插入更新删除操作
	 * */
//	public abstract boolean updateTable(TriggerBean bean);

    /**
     * 数据库操作完成后，关闭相关的连接资源
	 */
    private static void closeDB(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
        	//关闭结果集
            if (rs != null) {
                rs.close();
                rs = null;
            }
            //关闭Statement
            if (stmt != null) {
            	stmt.close();
            	stmt = null;
            }
            //关闭数据连接
            if (conn != null) {
                if (!conn.isClosed())
                    conn.close();
                conn = null;
            }
        }catch (Exception e) {
			e.printStackTrace();
        }
    }
}
