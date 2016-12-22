package util.db.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class LoadOracle {
	//hive���ݿ�������Ϣ
	private String driverName = "";
	private String url = "";//10.46.219.33�������Կ�;10.46.219.32������;10.1.4.53������
	private String user = "";
	private String password = "";
	
	//���ݿ�
	private Connection conn = null;
	private Statement stmt = null;
	private PreparedStatement pst = null;
	
	private ResultSet rs = null;//��ѯ�����
	
	//���Կ��� trueΪ����
	private boolean test_flag = true;

	public void setTest_flag(boolean test_flag) {
		this.test_flag = test_flag;
	}
	
	/**
	 * ��ѯ����-��������
	 * */
	private void getConfTest(int dbType) {
		if(test_flag){//����Ϊ��
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
		}else{//�ǲ���Ϊ��
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
	 * ִ��select���
	 * dbType 1:hive��  2:bishow��Ϣ��
	 */
    public List executeQuery(int dbType, String sSQL) throws Exception {
		Vector<String> resultList = new Vector<String>();//���ؼ�¼��
		//�ȶϿ���ǰ��������,
		closeDB();
		//���������ݿ��ֹ�������ִ��̫����ʲô�쳣
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//ʱ���ʽ
		try{
			//���ò�ѯ
			getConfTest(dbType);//����
			Class.forName(driverName);//��������
			conn = DriverManager.getConnection(url, user, password);//�������ݿ�	
			stmt = conn.createStatement();
			System.out.println("OracleTest executeQuery ���ִ�п�ʼʱ��:"+sim.format(new Date()));
			System.out.println("OracleTest executeQuery ִ�����:" + sSQL.toString());
			rs = stmt.executeQuery(sSQL.toString());//ִ�����
			System.out.println("OracleTest executeQuery ���ִ�н���ʱ��:"+sim.format(new Date()));
			
			String result_str = "";
			//��ȡ�ֶθ���
			int colCnt = rs.getMetaData().getColumnCount();			
			for (int i = 1; i <= colCnt; i++) {
				String columnname = rs.getMetaData().getColumnName(i);
				String columtypename = rs.getMetaData().getColumnTypeName(i);
				int columndisplaysize = rs.getMetaData().getColumnDisplaySize(i);
				if(columtypename.equals("VARCHAR2")){
					result_str += columnname + " char(" + columndisplaysize + "),";
				}else if(columtypename.equals("DATE")){
					result_str += columnname + " date \"yyyy-mm-dd hh24:mi:ss\",";
				}else{
					result_str += columnname + ",";
				}
			}
			if(result_str.length()>0)
				result_str = result_str.substring(0, result_str.length()-1);
			System.out.println(result_str);
			
			
//			while (rs != null && rs.next()) {//����ѯ����洢��list��
//				//tmpList = new Vector<String>();
//				String tmpstr = "";
//				for (int i = 1; i <= colCnt; i++) {
//					String reslut_rs = "";
//					if(rs.getString(i)!=null){
//						reslut_rs = rs.getString(i);
//					}
//					tmpstr += reslut_rs+"	";
//					//tmpList.add(reslut_rs);
//					//System.out.print(reslut_rs+" ");
//				}
//				resultList.add(tmpstr);
//				System.out.println();
//			}
		}catch(Exception ex){
			System.out.println("OracleTest executeQuery ִ�����ݿ��ѯ���������쳣:"+ex.toString());
			System.out.println(ex.toString());
			throw ex;//�׳��쳣
		}finally{
			//�ر����ݿ�����
			closeDB();
		}
		return resultList;
	}
    

    
	/**
	 * ִ��update���
	 */
    public int executeUpdate(int dbType, String sSQL) throws Exception {
		int flag = -1;////���ؽ��
		//�ȶϿ���ǰ��������,
		closeDB();
		//���������ݿ��ֹ�������ִ��̫����ʲô�쳣
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//ʱ���ʽ
		try{
			//���ò�ѯ
			getConfTest(dbType);//����
			Class.forName(driverName);//��������
			conn = DriverManager.getConnection(url, user, password);//�������ݿ�	
			stmt = conn.createStatement();
			System.out.println("OracleTest executeUpdate ���ִ�п�ʼʱ��:"+sim.format(new Date()));
			System.out.println("OracleTest executeUpdate ִ�����:" + sSQL.toString());
			flag = stmt.executeUpdate(sSQL.toString());//ִ�����
			if(flag==1)
				conn.commit();//�ύ
			else
				conn.rollback();//�ع�
			System.out.println("OracleTest executeUpdate ���ִ�н���ʱ��:"+sim.format(new Date()));
			System.out.println("OracleTest executeUpdate ִ�н��:"+flag);
		}catch(Exception ex){
			System.out.println("OracleTest executeQuery ִ�����ݿ���²��������쳣:"+ex.toString());
			System.out.println(ex.toString());
			throw ex;//�׳��쳣
		}finally{
			//�ر����ݿ�����
			closeDB();
		}
		return flag;
	}
	
    /**
     * ���ݿ������ɺ󣬹ر���ص�������Դ
	 */
    public void closeDB() throws Exception {
        try {
        	//�رս����
            if (rs != null) {
                rs.close();
                rs = null;
            }
            //�ر�Statement
            if (stmt != null) {
            	stmt.close();
            	stmt = null;
            }
			if(pst != null){
				pst.close();
				pst = null;
			}
            //�ر���������
            if (conn != null) {
                if (!conn.isClosed())
                    conn.close();
                conn = null;
            }
        }catch (Exception e) {
			System.out.println("OracleTest closeDB �ر�����������Դ�����쳣:"+e.toString());
        	System.out.println(e.toString());
        	e.printStackTrace();
			throw e;//�׳��쳣
        }
    }
    
    public static void main(String[] args) {
		try{
			LoadOracle lo = new LoadOracle();
			lo.executeQuery(2, "select * from bishow.qry_cus_mk2015_reward_month where 1<>1");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
