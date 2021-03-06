import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;


public class OracleTest {
	//hive数据库连接信息
	private String driverName = "";
	private String url = "";//10.46.219.33生产测试库;10.46.219.32生产库;10.1.4.53开发库
	private String user = "";
	private String password = "";
	
	//数据库
	private Connection conn = null;
	private Statement stmt = null;
	private PreparedStatement pst = null;
	
	private ResultSet rs = null;//查询结果集
	
	//private List resultList = null;
	//private List<String> tmpList = null;
	
	//测试开关 true为测试
	private boolean test_flag = true;

	public void setTest_flag(boolean test_flag) {
		this.test_flag = test_flag;
	}
	
	/**
	 * 查询配置-本机测试
	 * */
	private void getConfTest(int dbType) {
		if(test_flag){//测试为真
			if(dbType==1){//hive
				this.driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
				this.url = "jdbc:hive://10.1.4.53:10000/default";
				this.user = "hive";
				this.password = "hive";
			}else if(dbType==2){//bishow
				this.driverName = "oracle.jdbc.driver.OracleDriver";
				this.url = "jdbc:oracle:thin:@10.1.0.242:1521:ywxx";
				this.user = "bishow";
				this.password = "bishow";
			}else if(dbType==3){//bassweb
				this.driverName = "oracle.jdbc.driver.OracleDriver";
				this.url = "jdbc:oracle:thin:@10.1.0.242:1521:ywxx";
				this.user = "web";
				this.password = "qwE_60";
			}
		}else{//非测试为假
			if(dbType==1){//hive
				this.driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
				this.url = "jdbc:hive://10.46.219.32:10000/default";
				this.user = "hive";
				this.password = "hive";
			}else if(dbType==2){//bishow
				this.driverName = "oracle.jdbc.driver.OracleDriver";
				this.url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.46.61.84)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = 10.46.61.86)(PORT = 1521))(FAILOVER=ON)(LOAD_BALANCE = no))(CONNECT_DATA =(SERVICE_NAME = ywxx)))";
				this.user = "bishow";
				this.password = "fbi_bass_show";
			}else if(dbType==3){//bassweb
				this.driverName = "oracle.jdbc.driver.OracleDriver";
				this.url = "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 10.46.61.84)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = 10.46.61.86)(PORT = 1521))(FAILOVER=ON)(LOAD_BALANCE = no))(CONNECT_DATA =(SERVICE_NAME = ywxx)))";
				this.user = "bassweb";
				this.password = "fbi_bass_web";
			}
		}
	}
    
	/**
	 * 执行select语句
	 * dbType 1:hive库  2:bishow信息库
	 */
    public List executeQuery(int dbType, String sSQL) throws Exception {
		Vector<String> resultList = new Vector<String>();//返回记录集
		//先断开当前所有连接,
		closeDB();
		//再连接数据库防止连接语句执行太久有什么异常
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间格式
		try{
			//配置查询
			getConfTest(dbType);//测试
			Class.forName(driverName);//加载驱动
			conn = DriverManager.getConnection(url, user, password);//连接数据库	
			stmt = conn.createStatement();
			System.out.println("OracleTest executeQuery 语句执行开始时间:"+sim.format(new Date()));
			System.out.println("OracleTest executeQuery 执行语句:" + sSQL.toString());
			rs = stmt.executeQuery(sSQL.toString());//执行语句
			System.out.println("OracleTest executeQuery 语句执行结束时间:"+sim.format(new Date()));
			
			//获取字段个数
			int colCnt = rs.getMetaData().getColumnCount();
			
			while (rs != null && rs.next()) {//将查询结果存储到list中
				//tmpList = new Vector<String>();
				String tmpstr = "";
				for (int i = 1; i <= colCnt; i++) {
					String reslut_rs = "";
					if(rs.getString(i)!=null){
						reslut_rs = rs.getString(i);
					}
					tmpstr += reslut_rs+"	";
					//tmpList.add(reslut_rs);
					//System.out.print(reslut_rs+" ");
				}
				resultList.add(tmpstr);
				System.out.println();
			}
		}catch(Exception ex){
			System.out.println("OracleTest executeQuery 执行数据库查询操作出现异常:"+ex.toString());
			System.out.println(ex.toString());
			throw ex;//抛出异常
		}finally{
			//关闭数据库连接
			closeDB();
		}
		return resultList;
	}
    

    
	/**
	 * 执行update语句
	 */
    public int executeUpdate(int dbType, String sSQL) throws Exception {
		int flag = -1;////返回结果
		//先断开当前所有连接,
		closeDB();
		//再连接数据库防止连接语句执行太久有什么异常
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间格式
		try{
			//配置查询
			getConfTest(dbType);//测试
			Class.forName(driverName);//加载驱动
			conn = DriverManager.getConnection(url, user, password);//连接数据库	
			stmt = conn.createStatement();
			System.out.println("OracleTest executeUpdate 语句执行开始时间:"+sim.format(new Date()));
			System.out.println("OracleTest executeUpdate 执行语句:" + sSQL.toString());
			flag = stmt.executeUpdate(sSQL.toString());//执行语句
			if(flag==1)
				conn.commit();//提交
			else
				conn.rollback();//回滚
			System.out.println("OracleTest executeUpdate 语句执行结束时间:"+sim.format(new Date()));
			System.out.println("OracleTest executeUpdate 执行结果:"+flag);
		}catch(Exception ex){
			System.out.println("OracleTest executeQuery 执行数据库更新操作出现异常:"+ex.toString());
			System.out.println(ex.toString());
			throw ex;//抛出异常
		}finally{
			//关闭数据库连接
			closeDB();
		}
		return flag;
	}
	
    /**
     * 数据库操作完成后，关闭相关的连接资源
	 */
    public void closeDB() throws Exception {
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
			if(pst != null){
				pst.close();
				pst = null;
			}
            //关闭数据连接
            if (conn != null) {
                if (!conn.isClosed())
                    conn.close();
                conn = null;
            }
        }catch (Exception e) {
			System.out.println("OracleTest closeDB 关闭数据连接资源出现异常:"+e.toString());
        	System.out.println(e.toString());
        	e.printStackTrace();
			throw e;//抛出异常
        }
    }
    
    public static void main(String[] args) {
    	BufferedReader fin = null;
    	FileWriter fw = null;
    	BufferedWriter bw = null;
		OracleTest ot = new OracleTest();	
		
    	//由参赛指定数据名 生产/开发
    	String db_type = "";
    	String query_falg = "";
    	String query_or_update = "";
    	if(args.length==3){
    		db_type = args[0];
    		query_falg = args[1];
    		query_or_update = args[2];
		}else{
			db_type = "2";
    		query_falg = "true";
    		query_or_update = "q";//默认查询
		}
    	if(query_falg.equals("true")){
    		ot.setTest_flag(true);//测试
    	}else{
    		ot.setTest_flag(false);//生产
    	}
    	System.out.println("db_type:"+db_type);
    	System.out.println("query_falg:"+query_falg);
    	System.out.println("query_or_update:"+query_or_update);
    	
    	try {
			String filepath = System.getProperty("user.dir")+File.separator+"2.sql";
			fin = new BufferedReader(new FileReader(filepath));
			String str = "";
			String result = "";
			while ((str = fin.readLine()) != null) {
				result += str;//SQL语句
			}
			//结果集
			List<String> list = null;
			if(query_or_update.equals("q")){//查询
				list = ot.executeQuery(Integer.valueOf(db_type), result);	
			}else if(query_or_update.equals("u")){//更新
				ot.executeUpdate(Integer.valueOf(db_type), result);
			}

			if(list!=null){//有结果
		    	File f = new File(System.getProperty("user.dir")+File.separator+"resultlist.txt");
		        fw = new FileWriter(f);
		        bw = new BufferedWriter(fw);
		        for(int i=0;i<list.size();i++){
			        fw.write(list.get(i));
		            fw.write("\r\n");
			        fw.flush();
		        }
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}    	
	}
}
