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
 * hive��ѯ�߳�,��ip��,http��ͬʱ��ѯ,���̲�ѯʱ��
 * */
public class KeepNetLogOSSClientThread extends Thread {
	private boolean flag = false;//��־
	private int dbType = 1;//1:hive 2:bishow
	private String sql = "";//��ѯsql
	private String tag_name = "";//���
	private String telnumber = "";//����
	
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
	
	private List resultList = null;
	private List<String> tmpList = null;
	
	//���Կ��� trueΪ����
	private boolean test_flag = true;
	
	/**
	 * �Ƿ����
	 * */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * �����ж���ɺ�Ļ���
	 * */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	/**
	 * ���캯��
	 * @param str ��ѯsql
	 * @param _dbType ���ݿ�����
	 * @param _tag_name ���ر�ǩ��,�����ж�ip,http,bishow,���㴦����
	 * */
	public KeepNetLogOSSClientThread(String str, int _dbType, String _tag_name, String _telnumber) {
		this.sql = str;
		this.dbType = _dbType;
		this.resultList = new Vector();
		this.tag_name = _tag_name;
		this.telnumber = _telnumber;
	}
	
	/**
	 * ���߳�
	 * */
	public synchronized void run() {//�̰߳�ȫ
		List list = null;
		List result_list = new Vector();
		try{
			list = executeQuery(this.dbType, this.sql);//ִ�в�ѯ
			//ת��bean,��������IP
			//��ѯ�˺������õĹ���ip
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
					//�������ipΪ��,����
					if(bean.getUser_gw_ip().trim().length()==0){
						bean.setUser_gw_ip(ggsnip);
					}				
					//����bean ��Ҫ���˹���ip���Բ�תlist
					result_list.add(bean);
				}
			}else if(this.tag_name.equals("ip")){
				for(int i=0;i<list.size();i++){
					KeepNetLogOSSIpBean bean = new KeepNetLogOSSIpBean();
					bean.setSid(setListBean((List)list.get(i), 0));//sid
					bean.setTelnumber(setListBean((List)list.get(i), 1));//�ֻ�����
					bean.setGgsnip(setListBean((List)list.get(i), 2));//�û�NAT����IP��ַ
					bean.setUserip(setListBean((List)list.get(i), 3));//�û�˽��IP��ַ
					bean.setUsrsport(setListBean((List)list.get(i), 4));//NAT��Դ�˿�
					bean.setUsrdip(setListBean((List)list.get(i), 5));//Ŀ��IP
					bean.setUsrdport(setListBean((List)list.get(i), 6));//Ŀ�Ķ˿�
					bean.setStarttime(setListBean((List)list.get(i), 7));//����ʱ��
					bean.setLasttime(setListBean((List)list.get(i), 8));//����ʱ��
					bean.setApn(setListBean((List)list.get(i), 9));//APN
					bean.setRattype(setListBean((List)list.get(i), 10));//2g/3g
					//�������ipΪ��,����
					if(bean.getGgsnip().trim().length()==0){
						bean.setGgsnip(ggsnip);
					}
					//����bean ��Ҫjoin�������Բ�תlist
					result_list.add(bean);
				}
			}else if(this.tag_name.equals("http")){
				for(int i=0;i<list.size();i++){
					KeepNetLogOSSHttpBean bean = new KeepNetLogOSSHttpBean();
					bean.setIpsid(setListBean((List)list.get(i), 0));//http.ipsid
					bean.setUrl(setListBean((List)list.get(i), 1));//url
					bean.setStarttime(setListBean((List)list.get(i), 2));//����ʱ��
					bean.setLasttime(setListBean((List)list.get(i), 3));//����ʱ��
					//����bean ��Ҫjoin�������Բ�תlist
					result_list.add(bean);
				}
			}
            //��־���
			this.flag = true;
			//���
			this.resultList = result_list;
		}catch(Exception e){
			System.out.println("KeepNetLogOSSClientThread-error:"+e.toString());
			e.printStackTrace();
		}finally{
			try {
				closeDB();//�ر���������
			} catch (Exception e2) {
				System.out.println("KeepNetLogOSSClientThread run �ر�����������Դ�����쳣:"+e2.toString());
				e2.printStackTrace();
			}			
		}
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
				this.url = "jdbc:oracle:thin:@10.1.0.242:1521";
				this.user = "bishow";
				this.password = "bishow";
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
			}
		}
	}

	
    /**
     * ȥ��ͷβ������
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
	 * ִ��select���
	 * dbType 1:hive��  2:bishow��Ϣ��
	 */
    private List executeQuery(int dbType, String sSQL) throws Exception {
		Vector<List<String>> resultList = new Vector<List<String>>();//���ؼ�¼��
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
			System.out.println("KeepNetLogOSSClientThread executeQuery ���ִ�п�ʼʱ��:"+sim.format(new Date()));
			System.out.println("KeepNetLogOSSClientThread executeQuery ִ�����:" + sSQL.toString());
			rs = stmt.executeQuery(sSQL.toString());//ִ�����
			System.out.println("KeepNetLogOSSClientThread executeQuery ���ִ�н���ʱ��:"+sim.format(new Date()));
			
			//��ȡ�ֶθ���
			int colCnt = rs.getMetaData().getColumnCount();
			
			while (rs != null && rs.next()) {//����ѯ����洢��list��
				tmpList = new Vector<String>();
				for (int i = 1; i <= colCnt; i++) {
					String reslut_rs = "";
					if(rs.getString(i)!=null){
						reslut_rs = rs.getString(i);
					}
					if(dbType==1){//Hive�ַ�������
						if(reslut_rs.endsWith("'")){//�������Ž������ַ���
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
			System.out.println("KeepNetLogOSSClientThread executeQuery ִ�����ݿ���²��������쳣:"+ex.toString());
			System.out.println(ex.toString());
			throw ex;//�׳��쳣
		}

		return resultList;
	}
	
    /**
     * ���ݿ������ɺ󣬹ر���ص�������Դ
	 */
    private void closeDB() throws Exception {
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
			System.out.println("KeepNetLogOSSClientThread closeDB �ر�����������Դ�����쳣:"+e.toString());
        	System.out.println(e.toString());
        	e.printStackTrace();
			throw e;//�׳��쳣
        }
    }

    /**
     * ͨ�����õı�ǩ���ж�ip,http,bishow
     * */
	public String getTag_name() {
		return tag_name;
	}

	/**
	 * ��ѯ���
	 * */
	public List getResultList() {
		return resultList;
	}
	
	/**
	 * ��list���ý��bean
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
