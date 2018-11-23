package util.db.greenplum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

public class GpTest {
	private Connection db = null;
	private Statement st = null;
	private ResultSet rs = null;
	private String usr = "gpbase";
	private String pwd = "000000";
	private String url = "jdbc:postgresql://10.1.4.88:5432/bigdatagp";
	
	/**
	 * 连接数据库
	 * */
	private boolean connectionDb(){
		boolean flag = false;
		try{
			Class.forName("org.postgresql.Driver");
//			System.out.println("Success loading Driver!");
			db = DriverManager.getConnection(url, usr, pwd);
//			System.out.println("Success connecting server!");
			flag = true;
		}catch(Exception e){
//			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 查询数据库
	 * */
	private List<String> qryDb(String sql){
//		StringBuffer resultsb = new StringBuffer("");
		List<String> resultlist = new Vector<String>();
		try{
			st = db.createStatement();
			rs = st.executeQuery(sql);
			if(rs!=null){
				int columCount = rs.getMetaData().getColumnCount();
				while (rs.next()) {
					String strtmp = "";
					for(int i=1;i<=columCount;i++){
//						resultsb.append(rs.getString(i)).append(",");
						strtmp = strtmp + rs.getString(i) + ",";
					}
					resultlist.add(strtmp);					
//					resultsb.append("\n");
				}
			}
		}catch(Exception e){
//			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
		return resultlist;
	}
	
	/**
	 * 查询结束后执行操作
	 */
	private void closeDb(Statement st, Connection conn, ResultSet rs){
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			if (st != null) {
				st.close();
				st = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			if (conn != null) {
				if (!conn.isClosed())
					conn.close();
				conn = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 查询数据库
	 * */
	public void qry(String sql){
		boolean is_coonection = connectionDb();
		if(is_coonection){//连接成功
			List<String> result = qryDb(sql);//查询
			if(result!=null&&result.size()>0){
			}
			closeDb(st, db, rs);//关闭连接
		}
	}
	
	public static void main(String[] args) {
		
	}
}
