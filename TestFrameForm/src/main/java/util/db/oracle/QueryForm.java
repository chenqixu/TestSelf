package util.db.oracle;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class QueryForm extends JFrame implements ActionListener {

	private List<OracleUser> ouslist = null;
	
	private Connection db = null;
	private Statement st = null;
	private ResultSet rs = null;
	private String usr = "";
	private String pwd = "";
	private String url = "";
	private String drivername = "";
	private String qry_count = "100";
	
	private JLabel jlb_usr = null;
	private JLabel jlb_pwd = null;
	private JLabel jlb_url = null;
	private JLabel jlb_count = null;
	private JTextField jtf_usr = null;
	private JTextField jtf_pwd = null;
	private JTextField jtf_count = null;
	private JTextField jtf_jdbcurl = null;
	private JTextArea jta_sql = null;//��ѯ����
	private JButton jbtn_qry = null;
	private JComboBox jcb_url = null;
	
	private JPanel jp_top = null;
	private JPanel jp_center = null;
	private JPanel jp_end = null;
	private Container c = null;
	private JScrollPane js1 = null;
	private JScrollPane js2 = null;
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int LOGIN_WIDTH = 800;
	public static final int LOGIN_SHORT = 600;
	public QueryForm(){
		//����jdbc url
		ouslist = this.getJdbcUrl();
		url = ouslist.get(0).getUrl();
		usr = ouslist.get(0).getUser();
		pwd = ouslist.get(0).getPassword();
		drivername = ouslist.get(0).getDrivername();
		
		c = this.getContentPane();
		
		jp_top = new JPanel();
		jp_top.setLayout(new GridLayout(5,4,5,5));
		jlb_usr = new JLabel("�û�");
		jlb_pwd = new JLabel("����");
		jlb_url = new JLabel("����");
		jlb_count = new JLabel("��ѯ��¼(��)");
		jtf_usr = new JTextField(10);
		jtf_usr.setText(usr);
		jtf_pwd = new JTextField(10);
		jtf_pwd.setText(pwd);
		jtf_count = new JTextField(10);
		jtf_count.setText("100");
		jtf_jdbcurl = new JTextField(10);
		jtf_jdbcurl.setText(url);
		String[] arg_url = new String[ouslist.size()];
		for(int i=0;i<arg_url.length;i++){
			arg_url[i] = ouslist.get(i).getName();
		}		
		jcb_url = new JComboBox(arg_url);
		jcb_url.setActionCommand("change");
		jcb_url.addActionListener(this);
		jbtn_qry = new JButton("ִ��");
		jbtn_qry.setActionCommand("qry");
		jbtn_qry.addActionListener(this);
		jp_top.add(jlb_usr);
		jp_top.add(jtf_usr);
		jp_top.add(jlb_pwd);
		jp_top.add(jtf_pwd);
		jp_top.add(jlb_count);
		jp_top.add(jtf_count);
		jp_top.add(jlb_url);
		jp_top.add(jcb_url);
		jp_top.add(jbtn_qry);
		
		jp_center = new JPanel();
		jp_center.setLayout(new GridLayout(2,2,5,5));
		jta_sql = new JTextArea();
		jta_sql.setLineWrap(true);
		js1 = new JScrollPane(jta_sql);
		jp_center.add(js1);
				
		jp_end = new JPanel();
		jp_end.setLayout(new GridLayout(1,1,5,5));
		Object[][] cellData = {{""}};
		String[] columnNames = {""};
		JTable table = new JTable(cellData, columnNames);
		table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);//ˮƽ������
		table = FitTableColumns(table);//���ñ������Զ���Ӧ,������̫խ
		js2 = new JScrollPane(table);
		jp_end.add(js2);
		jp_center.add(jp_end);
				
		c.add(jp_top,BorderLayout.NORTH);
		c.add(jp_center,BorderLayout.CENTER);
		this.setTitle("OracleTool");
		this.setSize(LOGIN_WIDTH, LOGIN_SHORT);
		this.setLocation((int)(SCREEN_WIDTH/2)-(int)(LOGIN_WIDTH/2),
				(int)(SCREEN_HEIGHT/2)-(int)(LOGIN_SHORT/2));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		//���󽹵�
		jta_sql.requestFocus();
	}

	/**
	 * ��������
	 * */
	public void actionPerformed(ActionEvent e) {
		String actionstr = e.getActionCommand();
		if(actionstr.equals("qry")){
			this.usr = getUsr();
			this.pwd = getPwd();
			this.url = getUrl();
			this.qry_count = getCount();
			//������Ҫ�ԷֺŽ��д���
			String sql = getSql().trim();
			if(sql.indexOf(";")>0){//�������,�зֺ�
				String[] sqlList = sql.split(";");
				List<List<List<String>>> allResultList = new Vector<List<List<String>>>();
				for(int i=0;i<sqlList.length;i++){
					//�ж��ǲ�ѯ������������,��ѯʹ��qry,����ʹ��updateDb
					String tmp_sql = sqlList[i].trim();
					if(tmp_sql.length()>0)//�����ݲŲ���
						if(tmp_sql.toLowerCase().indexOf("select")==0){//��ѯ
							//�ж���oracle����postgresql
							if(drivername.toLowerCase().indexOf("oracle")>=0){
								List<List<String>> result = qry("select * from ("+tmp_sql+") t where rownum<="+this.qry_count);
								allResultList.add(result);
							}else if(drivername.toLowerCase().indexOf("postgresql")>=0){
								List<List<String>> result = qry(tmp_sql+" limit "+this.qry_count);
								allResultList.add(result);
							}
						}else{//�ǲ�ѯ
							List<List<String>> result = updateDb(tmp_sql);
							allResultList.add(result);
						}
				}
				//������tab���
				ResultTabPanel rtp = new ResultTabPanel(allResultList);
				jp_end.removeAll();
				jp_end.add(rtp);
				jp_end.revalidate();//ˢ��
			}else{//�������,û�ֺ�
				//�ж��ǲ�ѯ������������,��ѯʹ��qry,����ʹ��updateDb
				String tmp_sql = sql.trim();
				if(tmp_sql.length()>0)//�����ݲŲ���
					if(tmp_sql.toLowerCase().indexOf("select")==0){//��ѯ
						//�ж���oracle����postgresql
						if(drivername.toLowerCase().indexOf("oracle")>=0){
							List<List<String>> result = qry("select * from ("+tmp_sql+") t where rownum<="+this.qry_count);
							if(result!=null&&result.size()>0)
								setLog(result);//���ý������
						}else if(drivername.toLowerCase().indexOf("postgresql")>=0){
							List<List<String>> result = qry(tmp_sql+" limit "+this.qry_count);
							if(result!=null&&result.size()>0)
								setLog(result);//���ý������
						}
					}else{//�ǲ�ѯ
						List<List<String>> result = updateDb(tmp_sql);
						if(result!=null&&result.size()>0)
							setLog(result);//���ý������
					}
			}
		}else if(actionstr.equals("change")){
			String selectChange = this.jcb_url.getSelectedItem().toString();
			for(int i=0;i<ouslist.size();i++){
				if(selectChange.equals(ouslist.get(i).getName())){
					this.jtf_usr.setText(ouslist.get(i).getUser());
					this.jtf_pwd.setText(ouslist.get(i).getPassword());
					this.jtf_jdbcurl.setText(ouslist.get(i).getUrl());
					drivername = ouslist.get(i).getDrivername();
					break;
				}
			}
		}
	}
	
	/**
	 * ����û���
	 * */
	private String getUsr(){
		String tmp = this.jtf_usr.getText();
		return tmp;
	}
	
	/**
	 * �������
	 * */
	private String getPwd(){
		String tmp = this.jtf_pwd.getText();
		return tmp;
	}
	
	/**
	 * ��ò�ѯ��¼(��)
	 * */
	private String getCount(){
		String tmp = this.jtf_count.getText();
		return tmp;
	}
	
	/**
	 * ���jdbc url
	 * */
	private String getUrl(){
//		String tmp = this.jcb_url.getSelectedItem().toString();
		String tmp = this.jtf_jdbcurl.getText();		
		return tmp;
	}
	
	/**
	 * ���sql
	 * */
	private String getSql(){
		String tmp = this.jta_sql.getText();
		return tmp;
	}

	/**
	 * ������־/���
	 * */
	@SuppressWarnings("unchecked")
	private void setLog(List<List<String>> str){
		String[][] cellData = new String[str.size()-1][str.get(0).size()];
		Object[] columnObj = str.get(0).toArray();
		String[] columnNames = new String[columnObj.length];
		for(int j=0;j<columnObj.length;j++){
			columnNames[j] = columnObj[j].toString();
		}
		
		Iterator it = str.iterator();
		int i = 0;
		while(it.hasNext()){
			List<String> tmp = (List<String>)it.next();
			if(i==0){
			}else{
				Object[] tmpObj = tmp.toArray();
				for(int x=0;x<tmpObj.length;x++){					
					cellData[i-1][x] = (tmpObj[x]==null?"NULL":tmpObj[x].toString());
				}
			}
			i++;
		}
		JTable table = new JTable(cellData, columnNames);
		table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);//ˮƽ������
		table = FitTableColumns(table);
		js2 = new JScrollPane(table);
		jp_end.removeAll();
		jp_end.add(js2);
		jp_end.revalidate();//ˢ��
	}
	
	/**
	 * ��ѯ���ݿ�
	 * */
	public List<List<String>> qry(String sql) {
		List<List<String>> result = null;
		try{
			boolean is_coonection = connectionDb();
			if(is_coonection){//���ӳɹ�
				result = qryDb(sql);//��ѯ
				closeDb(st, db, rs);//�ر�����
			}else{//����ʧ��
				result = new Vector<List<String>>();
				List<String> errortmp = new Vector<String>();
				errortmp.add("����ʧ��");
				result.add(errortmp);			
			}
		}catch(Exception e){
//			e.printStackTrace();
			result = new Vector<List<String>>();
			List<String> errortmp = new Vector<String>();
			errortmp.add("����ʧ��."+e.toString());
			result.add(errortmp);
		}
		return result;
	}
	
	/**
	 * �������ݿ�
	 * */
	private boolean connectionDb() throws Exception {
		boolean flag = false;
		Class.forName(drivername);
		db = DriverManager.getConnection(url, usr, pwd);
		flag = true;
		return flag;
	}
	
	/**
	 * ��ѯ���ݿ�
	 * */
	private List<List<String>> qryDb(String sql){
		List<List<String>> resultlist = new Vector<List<String>>();
		try{
			st = db.createStatement();
			rs = st.executeQuery(sql);
			if(rs!=null){
				int columCount = rs.getMetaData().getColumnCount();
				List<String> columstrtmp = new Vector<String>();
				for(int i=1;i<=columCount;i++){
					columstrtmp.add(rs.getMetaData().getColumnName(i));
				}
				resultlist.add(columstrtmp);//�ֶ���
				while (rs.next()) {
					List<String> strtmp = new Vector<String>();
					for(int i=1;i<=columCount;i++){
						strtmp.add(rs.getString(i));
					}
					resultlist.add(strtmp);//����
				}
			}
		}catch(Exception e){
//			e.printStackTrace();
			List<String> errortmp = new Vector<String>();
			errortmp.add(e.toString());
			resultlist.add(errortmp);
		}
		return resultlist;
	}
	
	/**
	 * �������ݿ�
	 * */
	private List<List<String>> updateDb(String sql){
		int flag = -1;
		List<List<String>> resultlist = new Vector<List<String>>();
		try{
			boolean is_coonection = connectionDb();
			if(is_coonection){//���ӳɹ�
				st = db.createStatement();
				flag = st.executeUpdate(sql);//ִ�����
				if(flag>=0){
					db.commit();//�ύ
					List<String> infotmp = new Vector<String>();
					infotmp.add("ִ�гɹ�.����ֵ:"+flag);
					resultlist.add(infotmp);
				}else{
					db.rollback();//�ع�
					List<String> infotmp = new Vector<String>();
					infotmp.add("ִ��ʧ��.����ֵ:"+flag);
					resultlist.add(infotmp);
				}
				closeDb(st, db, rs);//�ر�����
			}
		}catch(Exception e){
//			e.printStackTrace();
			List<String> infotmp = new Vector<String>();
			infotmp.add("ִ��ʧ��."+e.toString());
			resultlist.add(infotmp);
		}		
		return resultlist;
	}
	
	/**
	 * ��ѯ������ִ�в���
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
	 * �������� url usr pwd driver
	 * */
	private List<OracleUser> getJdbcUrl(){
		List<OracleUser> oulist = new Vector<OracleUser>();
		String rootPath = System.getProperty("user.dir");
		String spltstr = File.separator;//�ļ�·���ָ���(����windows��linux)
		String pathQueryform = rootPath+spltstr+"config"+spltstr+"application_queryform.properties";

		File f = null;
		FileInputStream pInStream = null;
		Properties p = null;
		try{
			f = new File(pathQueryform);
			pInStream = new FileInputStream(f);
			p = new Properties();
			p.load(pInStream);
			Enumeration enuVersion = p.propertyNames();
			int count = 0;
			while(enuVersion.hasMoreElements()){
				enuVersion.nextElement();
				count++;
			}
			int all_count = count/5;
			for(int i=1;i<=all_count;i++){
				OracleUser ou = new OracleUser();
				ou.setUrl(p.getProperty("config"+i+".param1"));
				ou.setUser(p.getProperty("config"+i+".param2"));
				ou.setPassword(p.getProperty("config"+i+".param3"));
				ou.setDrivername(p.getProperty("config"+i+".param4"));
				ou.setName(p.getProperty("config"+i+".param5"));
				oulist.add(ou);
			}

			f = null;
			pInStream.close();
			p.clear();
			p = null;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(f!=null)
				f = null;
			if(pInStream!=null)
				try {
					pInStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(p!=null){
				p.clear();
				p = null;
			}
		}
		return oulist;
	}
	
	/**
	 * �O��table���Ќ��S�����{��
	 * */
	public JTable FitTableColumns(JTable tmpTable) {
		JTable myTable = tmpTable;
	    JTableHeader header = myTable.getTableHeader();
	    int rowCount = myTable.getRowCount();
	    Enumeration columns = myTable.getColumnModel().getColumns();
	    while (columns.hasMoreElements()) {
	        TableColumn column = (TableColumn) columns.nextElement();
	        int col = header.getColumnModel().getColumnIndex(
	                column.getIdentifier());
	        int width = (int) myTable.getTableHeader().getDefaultRenderer()
	                .getTableCellRendererComponent(myTable,
	                        column.getIdentifier(), false, false, -1, col)
	                .getPreferredSize().getWidth();
	        for (int row = 0; row < rowCount; row++) {
	            int preferedWidth = (int) myTable.getCellRenderer(row, col)
	                    .getTableCellRendererComponent(myTable,
	                            myTable.getValueAt(row, col), false, false,
	                            row, col).getPreferredSize().getWidth();
	            width = Math.max(width, preferedWidth);
	        }
	        header.setResizingColumn(column);
	        column.setWidth(width + myTable.getIntercellSpacing().width);
	    }
	    return myTable;
	}
	
	public static void main(String[] args) {
		new QueryForm();
//		String a = "1; 2; 3 ; 4;";
//		String[] b = a.split(";");
//		for(int i=0;i<b.length;i++){
//			System.out.println("@"+b[i]+"@");
//		}
//		String a = null;
//		System.out.println(a==null?"":a.toString());
//		String a = " selecT";
//		String tmp = a.trim().toLowerCase();
//		System.out.println(tmp.indexOf("select"));
	}

}
