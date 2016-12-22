import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
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
import util.ResultXML;
import util.XMLData;

public class mainForm extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3397053084321452394L;
	//private JButton btn1 = null;
	private JButton btn2 = null;
	private JTextArea ta1 = null;
	private JTextArea ta2 = null;
	private JTextField ClientID = null;
	private JTextField PassWord = null;
	private JTextField telnumber = null;
	private JTextField start_time = null;
	private JTextField end_time = null;
	private JTextField start = null;
	private JTextField end = null;
	private JTextField apn = null;
	private JTextField service_name = null;
	private JTextField reqsource = null;
	private JTextField url = null;
	private JTextField targetName = null;
	private JLabel ClientIDlb = null;
	private JLabel PassWordlb = null;
	private JLabel telnumberlb = null;
	private JLabel start_timelb = null;
	private JLabel end_timelb = null;
	private JLabel startlb = null;
	private JLabel endlb = null;
	private JLabel apnlb = null;
	private JLabel service_namelb = null;
	private JLabel reqsourcelb = null;
	private JLabel urllb = null;
	private JLabel targetNamelb = null;
	private JPanel jptop1 = null;
	private JPanel jptop2 = null;
	private JPanel jpcenter = null;
	private Container c = null;
	private JScrollPane js1 = null;
	private JScrollPane js2 = null;
	public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	//public static final int LOGIN_HEIGHT = 325;
	public static final int LOGIN_WIDTH = 800;
	public static final int LOGIN_SHORT = 600;
	
	public mainForm() {
		c = this.getContentPane();
		//btn1 = new JButton("doAction");
		btn2 = new JButton("Send");
		ta1 = new JTextArea(25,32);
		ta2 = new JTextArea(25,32);
		ta1.setLineWrap(true);
		ta2.setLineWrap(true);
		js1 = new JScrollPane(ta1);
		js2 = new JScrollPane(ta2);
		ClientID = new JTextField(10);
		PassWord = new JTextField(10);
		telnumber = new JTextField(10);
		start_time = new JTextField(10);
		end_time = new JTextField(10);
		start = new JTextField(10);
		end = new JTextField(10);
		apn = new JTextField(10);
		service_name = new JTextField(10);
		reqsource = new JTextField(10);
		url = new JTextField(10);
		targetName = new JTextField(10);
		
		ClientID.setText("9990058");
		PassWord.setText("bSb3b4aUbvbgbzaeaYaxbobvafaKbAbfbXaFb2alapbcaBaAbyaYbHbMbubwaObE");
		telnumber.setText("15206004526");
		start_time.setText("20140101000000");
		end_time.setText("20140104000000");
		end.setText("10");
		url.setText("http://10.46.219.60:8080/bi_bigdata_svc/services/NgService");
		//url.setText("http://127.0.0.1:8080/bi_bigdata_svc/services/NgService");
		//url.setText("http://10.1.4.53:8008/bi_bigdata_svc/services/NgService");
		targetName.setText("http://service.webservice.bi.newland.com/");
		
		ClientIDlb = new JLabel("ClientID:");
		PassWordlb = new JLabel("PassWord:");
		telnumberlb = new JLabel("telnumber:");
		start_timelb = new JLabel("start_time:");
		end_timelb = new JLabel("end_time:");
		startlb = new JLabel("start_row:");
		endlb = new JLabel("page_count:");
		apnlb = new JLabel("apn:");
		service_namelb = new JLabel("service_name:");
		reqsourcelb = new JLabel("reqsource:");
		urllb = new JLabel("url:");
		targetNamelb = new JLabel("targetName:");
		
		jptop1 = new JPanel();
		jptop1.setLayout(new GridLayout(2,6,5,5));
		jptop1.setPreferredSize(new Dimension(700, 55));
		jptop1.add(ClientIDlb);
		jptop1.add(ClientID);
		jptop1.add(PassWordlb);
		jptop1.add(PassWord);
		jptop1.add(telnumberlb);
		jptop1.add(telnumber);
		jptop1.add(start_timelb);
		jptop1.add(start_time);
		jptop1.add(end_timelb);
		jptop1.add(end_time);	
		jptop1.add(startlb);
		jptop1.add(start);

		jptop2 = new JPanel();
		jptop2.setLayout(new GridLayout(2,6,5,5));
		//jptop2.setPreferredSize(new Dimension(700, 45));
		jptop2.add(endlb);
		jptop2.add(end);
		jptop2.add(apnlb);
		jptop2.add(apn);
		jptop2.add(service_namelb);
		jptop2.add(service_name);
		jptop2.add(reqsourcelb);
		jptop2.add(reqsource);
		jptop2.add(urllb);
		jptop2.add(url);
		jptop2.add(targetNamelb);
		jptop2.add(targetName);		
		
		jpcenter = new JPanel();
		jpcenter.add(js1);
		//jpcenter.add(btn1);
		jpcenter.add(btn2);
		jpcenter.add(js2);
		
		c.add(jptop1,BorderLayout.NORTH);
		c.add(jptop2,BorderLayout.CENTER);
		c.add(jpcenter,BorderLayout.SOUTH);
		
		//btn1.addActionListener(this);
		btn2.addActionListener(this);
		this.setTitle("Tool");
		this.setSize(LOGIN_WIDTH, LOGIN_SHORT);
		this.setLocation((int)(SCREEN_WIDTH/2)-(int)(LOGIN_WIDTH/2),
				(int)(SCREEN_HEIGHT/2)-(int)(LOGIN_SHORT/2));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		//System.out.println(e.getActionCommand());
		String ac = e.getActionCommand();
		CallWebService cws = new CallWebService();
		String url = "";
		String strxml = "";
		if(ac.equals("doAction")){
			//url = "http://10.1.0.168:5005/bi_svc/services/NgService";
			url = "http://10.46.219.60:8008/bi_bigdata_svc/services/NgService";
			//strxml = cws.setSendXML(2);
			//String resultxml = cws.doAction("POST", url, strxml.getBytes());
			//System.out.println(resultxml);
			cws.qqCall(url, "post.XML");
			//this.ta2.setText(resultxml);
			
			/*ResultXML rx = new ResultXML();
			StringBuffer xml = new StringBuffer();
			xml.append("<?xml version=\"1.0\"  encoding='UTF-8'?>");
			xml.append( resultxml );
			XMLData xd = new XMLData(xml.toString());
			rx.rtFlag = true;
			rx.bXmldata = true;
			rx.xmldata = xd;
			rx.setbFlag(false);
			
			rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("HeaderResp").setParentPointer();
			String result_str_head = "";
			result_str_head = rx.node("RespResult").getValue();
			result_str_head += rx.node("RespDesc").getValue();
			this.ta1.setText(result_str_head);
			
			rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("BodyResp").node("RespData").setParentPointer();
	        rx.setRowFlagInfo("NgRespBean");
	        rx.First();
	        String result_str = "";
	        while (!rx.isEof()) {
	        	String str = rx.getColumnsValue("content");
	        	result_str += str;
	        	//System.out.println(str);
	        	rx.Next();
	        }
	        this.ta2.setText(result_str);*/
		}else if(ac.equals("Send")){
			//url = "http://10.46.219.60:8008/bi_bigdata_svc/services/NgService";
			//url = "http://10.46.219.60:8080/bi_bigdata_svc/services/NgService";
			//url = "http://10.1.0.168:5005/bi_svc/services/NgService";
			this.ta2.setText("查询中...");//查询中
			url = this.url.getText();
			
			String targetName = this.targetName.getText();
			String clientid_str = this.ClientID.getText();
			String password_str = this.PassWord.getText();
			String telnumber_str = this.telnumber.getText();
			String start_time_str = this.start_time.getText();
			String end_time_str = this.end_time.getText();
			String start_str = this.start.getText();
			String apn = this.apn.getText();
			String service_name = this.service_name.getText();
			String reqsource = this.reqsource.getText();
			//if(start_str.length()==0)start_str="1";
			String end_str = this.end.getText();
			//if(end_str.length()==0)end_str="10";
			
			String heard = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\""+targetName+"\">";
			strxml = cws.setSendXML(heard,clientid_str,password_str,telnumber_str,start_time_str,end_time_str,start_str,end_str,apn,service_name,reqsource);
			System.out.println(strxml);
			this.ta1.setText("");//清空
			this.ta1.append("请求报文:");
        	this.ta1.append("\n");
			this.ta1.append(strxml);
        	this.ta1.append("\n");
			String resultxml = cws.doAction("POST", url, strxml.getBytes());
			System.out.println(resultxml);
			this.ta2.setText("");//清空
			if(resultxml.length()>0){
				ResultXML rx = new ResultXML();
				StringBuffer xml = new StringBuffer();
				xml.append("<?xml version=\"1.0\"  encoding='UTF-8'?>");
				xml.append(resultxml);
				XMLData xd = new XMLData(xml.toString());
				rx.rtFlag = true;
				rx.bXmldata = true;
				rx.xmldata = xd;
				rx.setbFlag(false);
				
				rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("HeaderResp").setParentPointer();
				String RespResult = rx.node("RespResult").getValue();
				String RespDesc = rx.node("RespDesc").getValue();
				//this.ta1.setText(RespDesc);
				if(RespResult.equals("0")){
					rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("BodyResp").node("RespData").setParentPointer();
			        rx.setRowFlagInfo("NgRespBean");
			        rx.First();
		        	this.ta2.append("查询结果描述:");
		        	this.ta2.append("\n");
			        this.ta2.append(RespDesc);
		        	this.ta2.append("\n");
		        	this.ta2.append("查询结果内容:");
		        	this.ta2.append("\n");
			        if(rx.isEof()){
			        	this.ta2.append("没有结果");
			        	this.ta2.append("\n");
			        }else{
			        	int i_first = 0;
				        while (!rx.isEof()) {
				        	String str = rx.getColumnsValue("content");
				        	this.ta2.append(str);
				        	this.ta2.append("\n");
				        	
				        	if(i_first==0){
					        	//汇总信息
					        	ResultXML rxxRow = rx.GetColumnsResultXML();
					        	rxxRow.setbFlag(false);
								rxxRow.setRowFlagInfo("GatherBean");
								rxxRow.Frist();
								  
								String cmwap_total_bytes = "0";//wap
								String cmnet_total_bytes = "0";//net
								String cmwap_total_times = "0";//wap time
								String cmnet_total_times = "0";//net time
								while(!rxxRow.isEof()){
									if(rxxRow.getColumnsValue("apn").equalsIgnoreCase("cmwap") || rxxRow.getColumnsValue("apn").equalsIgnoreCase("CMWAP")){
										cmwap_total_bytes = rxxRow.getColumnsValue("allbytes");
										cmwap_total_times = rxxRow.getColumnsValue("allDelaytime");
									}else if(rxxRow.getColumnsValue("apn").equalsIgnoreCase("cmnet") || rxxRow.getColumnsValue("apn").equalsIgnoreCase("CMNET")){
										cmnet_total_bytes = rxxRow.getColumnsValue("allbytes");
										cmnet_total_times = rxxRow.getColumnsValue("allDelaytime");
									}
									rxxRow.Next();//下一个
								}
								this.ta2.append("cmnet总流量:");
								this.ta2.append(String.valueOf(cmnet_total_bytes));
					        	this.ta2.append("\n");
								this.ta2.append("cmnet总时长:");
								this.ta2.append(String.valueOf(cmnet_total_times));
					        	this.ta2.append("\n");
								this.ta2.append("cmwap总流量:");
								this.ta2.append(String.valueOf(cmwap_total_bytes));
					        	this.ta2.append("\n");
								this.ta2.append("cmwap总时长:");
								this.ta2.append(String.valueOf(cmwap_total_times));
					        	this.ta2.append("\n");
				        	}
				        	rx.Next();//下一个
				        	i_first ++;
				        }
				        //输出totalCount
				        rx.resetParent().node("Body").node("qryNetLogListResponse").node("message").node("BodyResp").setParentPointer();
			        	this.ta1.append("\n");
				        this.ta1.append("totalCount:");
			        	this.ta1.append("\n");
			        	this.ta1.append(rx.node("totalCount").getValue());
			        	this.ta1.append("\n");
			        	/*this.ta1.append("firstRowKey:");
			        	this.ta1.append("\n");
			        	this.ta1.append(rx.node("firstRowKey").getValue());
			        	this.ta1.append("\n");
						this.ta1.append("lastRowKey:");
			        	this.ta1.append("\n");
			        	this.ta1.append(rx.node("lastRowKey").getValue());*/
			        }
				}else{//失败
		        	this.ta2.append("查询结果描述:");
		        	this.ta2.append("\n");
			        this.ta2.append(RespDesc);
				}
			}else{
	        	this.ta2.append("接口没有返回数据。");
			}
		}
	}
	
	public static void main(String[] args) {
		//long delayTimesCmwap = 680;
		mainForm mf = new mainForm();
		/*String a = "&#x6210;&#x529f;";
		try {
			System.out.println(new String(a.getBytes("GBK")));
		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}*/
		//DecimalFormat formater = new DecimalFormat("#0.###");
		//System.out.println(formater.format(1/3.0));
		/*BigDecimal a = new BigDecimal(delayTimesCmwap/1000.0).setScale(0, BigDecimal.ROUND_HALF_UP);
		System.out.println(a);
		System.out.println(delayTimesCmwap/1000.0);
		System.out.println("四舍五入取整:(0.5)=" + new BigDecimal("0.5").setScale(0, BigDecimal.ROUND_HALF_UP)); */		
	}

}
