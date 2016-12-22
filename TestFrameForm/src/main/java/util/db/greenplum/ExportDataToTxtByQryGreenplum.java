package util.db.greenplum;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ExportDataToTxtByQryGreenplum extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5921899325084093726L;
	private Connection db = null;
	private Statement st = null;
	private ResultSet rs = null;
	private String usr = "";
	private String pwd = "";
	private String url = "";
	
	private JLabel jlb_usr = null;
	private JLabel jlb_pwd = null;
	private JLabel jlb_url = null;
	private JTextField jtf_usr = null;
	private JTextField jtf_pwd = null;
//	private JTextField jtf_url = null;
	private JTextArea jta_sql = null;
	private JTextArea jta_result = null;
	private JButton jbtn_qry = null;
	private JButton jbtn_export = null;
	private JComboBox jcb_url = null;
	private JComboBox jcb_change = null;
	
	private JPanel jp_top = null;
	private JPanel jp_center = null;
	private Container c = null;
	private JScrollPane js1 = null;
	private JScrollPane js2 = null;
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int LOGIN_WIDTH = 800;
	public static final int LOGIN_SHORT = 600;
	
	public ExportDataToTxtByQryGreenplum(){
		c = this.getContentPane();
		
		jp_top = new JPanel();
		jp_top.setLayout(new GridLayout(5,4,5,5));
		jlb_usr = new JLabel("用户");
		jlb_pwd = new JLabel("密码");
		jlb_url = new JLabel("数据库地址");
		jtf_usr = new JTextField(10);
		jtf_usr.setText("gpbase");
		jtf_pwd = new JTextField(10);
		jtf_pwd.setText("000000");
//		jtf_url = new JTextField(10);
//		jtf_url.setText("jdbc:postgresql://10.1.4.88:5432/bigdatagp");		
		String[] arg_url = {"jdbc:postgresql://10.1.4.88:5432/bigdatagp"
				,"jdbc:postgresql://10.46.219.48:5432/bigdatagp"};
		jcb_url = new JComboBox(arg_url);		
		jbtn_qry = new JButton("查询");
		jbtn_qry.setActionCommand("qry");
		jbtn_qry.addActionListener(this);
		jbtn_export = new JButton("导出");
		jbtn_export.setActionCommand("export");
		jbtn_export.addActionListener(this);
		String[] arg_change = {"开发", "生产"};
		jcb_change = new JComboBox(arg_change);
		jcb_change.setActionCommand("change");
		jcb_change.addActionListener(this);
		jp_top.add(jlb_usr);
		jp_top.add(jtf_usr);
		jp_top.add(jlb_pwd);
		jp_top.add(jtf_pwd);
		jp_top.add(jlb_url);
		jp_top.add(jcb_url);
		jp_top.add(jbtn_qry);
		jp_top.add(jbtn_export);
		jp_top.add(jcb_change);
		
		jp_center = new JPanel();
		jp_center.setLayout(new GridLayout(2,2,5,5));
		jta_sql = new JTextArea();
		jta_sql.setLineWrap(true);
		jta_result = new JTextArea();
		jta_result.setLineWrap(true);
		jta_result.setEditable(false);
		js1 = new JScrollPane(jta_sql);
		js2 = new JScrollPane(jta_result);
		jp_center.add(js1);
		jp_center.add(js2);
		
		c.add(jp_top,BorderLayout.NORTH);
		c.add(jp_center,BorderLayout.CENTER);
		this.setTitle("Tool");
		this.setSize(LOGIN_WIDTH, LOGIN_SHORT);
		this.setLocation((int)(SCREEN_WIDTH/2)-(int)(LOGIN_WIDTH/2),
				(int)(SCREEN_HEIGHT/2)-(int)(LOGIN_SHORT/2));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String actionstr = e.getActionCommand();
		if(actionstr.equals("qry")){
			this.usr = getUsr();
			this.pwd = getPwd();
			this.url = getUrl();
			qry(getSql());
		}else if(actionstr.equals("change")){
			String selectChange = this.jcb_change.getSelectedItem().toString();
			if(selectChange.equals("开发")){
				this.jtf_pwd.setText("000000");
				this.jcb_url.setSelectedIndex(0);
			}else{
				this.jtf_pwd.setText("gpbaseFZ2#");
				this.jcb_url.setSelectedIndex(1);
			}
		}else if(actionstr.equals("export")){
			this.usr = getUsr();
			this.pwd = getPwd();
			this.url = getUrl();
			export(getSql());
		}
	}
	
	private String getUsr(){
		String tmp = this.jtf_usr.getText();
		return tmp;
	}
	
	private String getPwd(){
		String tmp = this.jtf_pwd.getText();
		return tmp;
	}
	
	private String getUrl(){
//		String tmp = this.jtf_url.getText();
		String tmp = this.jcb_url.getSelectedItem().toString();
		return tmp;
	}
	
	private String getSql(){
		String tmp = this.jta_sql.getText();
		return tmp;
	}
	
	private void setLog(String str){
		String strs = this.jta_result.getText();
		strs = strs + "\n" + str;
		this.jta_result.setText(strs);
	}
	
	private void setLog(List<String> str){
		StringBuffer sb = new StringBuffer("");
		Iterator it = str.iterator();
		while(it.hasNext()){
			sb.append(it.next().toString());
			sb.append("\n");
		}
		this.jta_result.setText(sb.toString());
	}
	
	/**
	 * 查询数据库
	 * */
	public void qry(String sql){
		boolean is_coonection = connectionDb();
		if(is_coonection){//连接成功
			List<String> result = qryDb(sql);//查询
			if(result!=null&&result.size()>0){
				setLog(result);
			}
			closeDb(st, db, rs);//关闭连接
		}
	}
	
	/**
	 * 查询数据库,导出txt文件
	 * */
	public void export(String sql){
		boolean is_coonection = connectionDb();
		if(is_coonection){//连接成功
			List<String> result = qryDb(sql);//查询
			if(result!=null&&result.size()>0){
				exportToTxt(result);
			}			
			closeDb(st, db, rs);//关闭连接
		}
	}
	
	/**
	 * 导出到文本
	 * */
	private void exportToTxt(List<String> list){
		FileWriter fw = null;
    	BufferedWriter bw = null;
		try {
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
		}catch(Exception e){
			e.printStackTrace();
			setLog(e.toString());
		}finally{
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
					setLog(e.toString());
				}
			}
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
					setLog(e.toString());
				}
			}
		}
	}
	
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
			setLog(e.toString());
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
			setLog(e.toString());
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
			setLog(ex.toString());
		}

		try {
			if (st != null) {
				st.close();
				st = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			setLog(ex.toString());
		}

		try {
			if (conn != null) {
				if (!conn.isClosed())
					conn.close();
				conn = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			setLog(ex.toString());
		}
	}
	
	public static void main(String[] args) {
		new ExportDataToTxtByQryGreenplum();
	}
}
