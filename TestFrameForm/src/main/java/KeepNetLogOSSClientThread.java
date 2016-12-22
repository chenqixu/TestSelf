import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import bean.KeepNetLogOSSHttpBean;
import bean.KeepNetLogOSSIpBean;
import bean.KeepNetLogOSSWlanBean;

/**
 * hive查询线程,对ip表,http表同时查询,缩短查询时间
 * */
public class KeepNetLogOSSClientThread extends Thread {
	private boolean flag = false;//标志
	private int dbType = 1;//1:hive 2:bishow
	private String sql = "";//查询sql
	private String tag_name = "";//标记
	private String telnumber = "";//号码
	
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
	
	private List resultList = null;
	private List<String> tmpList = null;
	
	//测试开关 true为测试
	private boolean test_flag = true;
	
	/**
	 * 是否完成
	 * */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * 用来判断完成后的回射
	 * */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	/**
	 * 构造函数
	 * @param str 查询sql
	 * @param _dbType 数据库类型
	 * @param _tag_name 返回标签名,用于判断ip,http,bishow,方便处理结果
	 * */
	public KeepNetLogOSSClientThread(String str, int _dbType, String _tag_name, String _telnumber) {
		this.sql = str;
		this.dbType = _dbType;
		this.resultList = new Vector();
		this.tag_name = _tag_name;
		this.telnumber = _telnumber;
	}
	
	/**
	 * 主线程
	 * */
	public synchronized void run() {//线程安全
		List list = null;
		List result_list = new Vector();
		try{
			list = executeQuery(this.dbType, this.sql);//执行查询
			//转成bean,并处理公网IP
			//查询此号码配置的公网ip
			String ggsnip = "";//queryGgsnIp(this.telnumber);
			if(this.tag_name.equals("wlan")){
				for(int i=0;i<list.size();i++){
					KeepNetLogOSSWlanBean bean = new KeepNetLogOSSWlanBean();
					bean.setStarttime_s(setListBean((List)list.get(i), 0));
					bean.setStarttime_e(setListBean((List)list.get(i), 1));
					bean.setTelnumber(setListBean((List)list.get(i), 2));
					bean.setMac(setListBean((List)list.get(i), 3));
					bean.setAc_home_county(setListBean((List)list.get(i), 4));
					bean.setUser_home_county(setListBean((List)list.get(i), 5));
					bean.setAcip(setListBean((List)list.get(i), 6));
					bean.setNasid(setListBean((List)list.get(i), 7));
					bean.setUp(setListBean((List)list.get(i), 8));
					bean.setDown(setListBean((List)list.get(i), 9));
					bean.setUser_gw_ip(setListBean((List)list.get(i), 10));	
					//如果公网ip为空,设置
					if(bean.getUser_gw_ip().trim().length()==0){
						bean.setUser_gw_ip(ggsnip);
					}				
					//设置bean 需要过滤公网ip所以不转list
					result_list.add(bean);
				}
			}else if(this.tag_name.equals("ip")){
				for(int i=0;i<list.size();i++){
					KeepNetLogOSSIpBean bean = new KeepNetLogOSSIpBean();
					bean.setSid(setListBean((List)list.get(i), 0));//sid
					bean.setTelnumber(setListBean((List)list.get(i), 1));//手机号码
					bean.setGgsnip(setListBean((List)list.get(i), 2));//用户NAT后公网IP地址
					bean.setUserip(setListBean((List)list.get(i), 3));//用户私网IP地址
					bean.setUsrsport(setListBean((List)list.get(i), 4));//NAT后源端口
					bean.setUsrdip(setListBean((List)list.get(i), 5));//目的IP
					bean.setUsrdport(setListBean((List)list.get(i), 6));//目的端口
					bean.setStarttime(setListBean((List)list.get(i), 7));//访问时间
					bean.setLasttime(setListBean((List)list.get(i), 8));//下线时间
					bean.setApn(setListBean((List)list.get(i), 9));//APN
					bean.setRattype(setListBean((List)list.get(i), 10));//2g/3g
					//如果公网ip为空,设置
					if(bean.getGgsnip().trim().length()==0){
						bean.setGgsnip(ggsnip);
					}
					//设置bean 需要join处理所以不转list
					result_list.add(bean);
				}
			}else if(this.tag_name.equals("http")){
				for(int i=0;i<list.size();i++){
					KeepNetLogOSSHttpBean bean = new KeepNetLogOSSHttpBean();
					bean.setIpsid(setListBean((List)list.get(i), 0));//http.ipsid
					bean.setUrl(setListBean((List)list.get(i), 1));//url
					bean.setStarttime(setListBean((List)list.get(i), 2));//上线时间
					bean.setLasttime(setListBean((List)list.get(i), 3));//下线时间
					//设置bean 需要join处理所以不转list
					result_list.add(bean);
				}
			}
            //标志完成
			this.flag = true;
			//结果
			this.resultList = result_list;
		}catch(Exception e){
			System.out.println("KeepNetLogOSSClientThread-error:"+e.toString());
			e.printStackTrace();
		}finally{
			try {
				closeDB();//关闭数据连接
			} catch (Exception e2) {
				System.out.println("KeepNetLogOSSClientThread run 关闭数据连接资源出现异常:"+e2.toString());
				e2.printStackTrace();
			}			
		}
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
				this.url = "jdbc:oracle:thin:@10.1.0.242:1521";
				this.user = "bishow";
				this.password = "bishow";
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
			}
		}
	}

	
    /**
     * 去除头尾的引号
     * */
    private String formatHiveResult(String str) {
    	String result = str;
    	if(str==null || str.length()==0){
    		result = "";
    	}else{
    		result = result.substring(1,result.length()-1);
    	}
    	return result;
    }
    
	/**
	 * 执行select语句
	 * dbType 1:hive库  2:bishow信息库
	 */
    private List executeQuery(int dbType, String sSQL) throws Exception {
		Vector<List<String>> resultList = new Vector<List<String>>();//返回记录集
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
			System.out.println("KeepNetLogOSSClientThread executeQuery 语句执行开始时间:"+sim.format(new Date()));
			System.out.println("KeepNetLogOSSClientThread executeQuery 执行语句:" + sSQL.toString());
			rs = stmt.executeQuery(sSQL.toString());//执行语句
			System.out.println("KeepNetLogOSSClientThread executeQuery 语句执行结束时间:"+sim.format(new Date()));
			
			//获取字段个数
			int colCnt = rs.getMetaData().getColumnCount();
			
			while (rs != null && rs.next()) {//将查询结果存储到list中
				tmpList = new Vector<String>();
				for (int i = 1; i <= colCnt; i++) {
					String reslut_rs = "";
					if(rs.getString(i)!=null){
						reslut_rs = rs.getString(i);
					}
					if(dbType==1){//Hive字符串处理
						if(reslut_rs.endsWith("'")){//是以引号结束的字符串
							tmpList.add(formatHiveResult(reslut_rs));
						}else{
							tmpList.add(reslut_rs);
						}
					}else if(dbType==2){//bishow
						tmpList.add(reslut_rs);
					}
				}
				resultList.add(tmpList);
			}
		}catch(Exception ex){
			System.out.println("KeepNetLogOSSClientThread executeQuery 执行数据库更新操作出现异常:"+ex.toString());
			System.out.println(ex.toString());
			throw ex;//抛出异常
		}

		return resultList;
	}
	
    /**
     * 数据库操作完成后，关闭相关的连接资源
	 */
    private void closeDB() throws Exception {
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
			System.out.println("KeepNetLogOSSClientThread closeDB 关闭数据连接资源出现异常:"+e.toString());
        	System.out.println(e.toString());
        	e.printStackTrace();
			throw e;//抛出异常
        }
    }

    /**
     * 通过设置的标签名判断ip,http,bishow
     * */
	public String getTag_name() {
		return tag_name;
	}

	/**
	 * 查询结果
	 * */
	public List getResultList() {
		return resultList;
	}
	
	/**
	 * 从list设置结果bean
	 * */
	public String setListBean(List list, int size) {
		String str = "";
		if(list!=null && list.size()>=size){
			str = list.get(size).toString();
		}
		return str;
	}

	public void setTest_flag(boolean test_flag) {
		this.test_flag = test_flag;
	}
}
