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
	
	//private List resultList = null;
	//private List<String> tmpList = null;
	
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
			
			//��ȡ�ֶθ���
			int colCnt = rs.getMetaData().getColumnCount();
			
			while (rs != null && rs.next()) {//����ѯ����洢��list��
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
    	BufferedReader fin = null;
    	FileWriter fw = null;
    	BufferedWriter bw = null;
		OracleTest ot = new OracleTest();	
		
    	//�ɲ���ָ�������� ����/����
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
    		query_or_update = "q";//Ĭ�ϲ�ѯ
		}
    	if(query_falg.equals("true")){
    		ot.setTest_flag(true);//����
    	}else{
    		ot.setTest_flag(false);//����
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
				result += str;//SQL���
			}
			//�����
			List<String> list = null;
			if(query_or_update.equals("q")){//��ѯ
				list = ot.executeQuery(Integer.valueOf(db_type), result);	
			}else if(query_or_update.equals("u")){//����
				ot.executeUpdate(Integer.valueOf(db_type), result);
			}

			if(list!=null){//�н��
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
