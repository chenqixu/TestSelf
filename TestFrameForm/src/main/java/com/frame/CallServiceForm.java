package com.frame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import util.CallWebService;

public class CallServiceForm extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int LOGIN_WIDTH = 800;
	public static final int LOGIN_SHORT = 600;

	private JTextArea resquestjta = null;
	private JTextArea responsejta = null;
	private JTextField serviceurljtf = null;
	private JLabel serviceurljlb = null;
	private JButton sendbtn = null;

	private Container c = null;
	private JScrollPane jsleft = null;
	private JScrollPane jsright = null;
	private JPanel jptop = null;
	private JPanel jpcenter = null;
	
	public CallServiceForm() {		
		//top panel
		serviceurljlb = new JLabel("ServiceURL:");
		serviceurljtf = new JTextField(10);
		serviceurljtf.setText("http://10.46.219.60:8080/bi_bigdata_svc/services/NgService");
		//serviceurljtf.setText("http://127.0.0.1:8080/bi_bigdata_svc/services/NgService");
		//serviceurljtf.setText("http://10.1.4.53:8008/bi_bigdata_svc/services/NgService");
		sendbtn = new JButton("SendXml");
		sendbtn.setActionCommand("sendxml");
		sendbtn.addActionListener(this);
		jptop = new JPanel();
		jptop.setLayout(new GridLayout(3,1));
		jptop.add(serviceurljlb);
		jptop.add(serviceurljtf);
		jptop.add(sendbtn);

		//center panel
		resquestjta = new JTextArea();
		resquestjta.setLineWrap(true);//anto change line
		jsleft = new JScrollPane(resquestjta);
		responsejta = new JTextArea();
		responsejta.setLineWrap(true);//anto change line
		responsejta.setEditable(false);
		jsright = new JScrollPane(responsejta);
		jpcenter = new JPanel();
		jpcenter.setLayout(new GridLayout(1,2));
		jpcenter.add(jsleft);
		jpcenter.add(jsright);
		
		//all pane
		c = this.getContentPane();
		c.add(jptop,BorderLayout.NORTH);
		c.add(jpcenter,BorderLayout.CENTER);
		
		this.setTitle("CallServiceTool");
		this.setSize(LOGIN_WIDTH, LOGIN_SHORT);
		this.setLocation((int)(SCREEN_WIDTH/2)-(int)(LOGIN_WIDTH/2),
				(int)(SCREEN_HEIGHT/2)-(int)(LOGIN_SHORT/2));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		CallWebService cws = new CallWebService();
		String url = "";
		String strxml = "";
		String resultxml = "";
		if(ac.equals("sendxml")){
			this.responsejta.setText("querying...");//querying
			url = this.serviceurljtf.getText().trim();
			strxml = this.resquestjta.getText().trim();
			if(strxml.length()>0){				
				resultxml = cws.doAction("POST", url, strxml.getBytes());				
				this.responsejta.setText("");//clear
				if(resultxml.length()>0){
					resultxml = formatXMLStr(resultxml);
					this.responsejta.setText(resultxml);//set result xml
				}else{
		        	this.responsejta.append("No data return from service.");
				}
			}else{
				this.responsejta.setText("");//clear
				this.responsejta.append("No request data.");
			}
		}
		cws = null;
		System.gc();
	}
	
	/**
	 * 格式化xml字符串,在每个</>后面添加 /r/n
	 * */
	private String formatXMLStr(String arg0){
		StringBuffer result = new StringBuffer("");
		String tmp = arg0.trim();
		if(tmp.startsWith("<") && tmp.endsWith(">") && tmp.indexOf("</")>=0){
			while(tmp.length()>0){
				int indexs = -1;
				int indexe = -1;
				indexs = tmp.indexOf("</");
				indexe = tmp.indexOf(">");
				if(indexe>indexs){
					result.append(tmp.substring(indexs,indexe+1));
					result.append("\r\n");
					tmp = tmp.substring(indexe+1, tmp.length());
				}else{
					result.append(tmp.substring(0,indexs));
					tmp = tmp.substring(indexs, tmp.length());
				}
			}
		}else{
			result.append(tmp);
		}
		return result.toString();
	}
	
	public static void main(String[] args) {
		//超时
//		System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(20000));//连接主机超时20秒超时
//		System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(20000));//从主机读取数据超时20秒超时
		new CallServiceForm();
	}
}
