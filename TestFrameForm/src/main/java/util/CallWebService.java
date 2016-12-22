package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import bean.NgReqBean;

public class CallWebService {
    public static int MAX_REC_BUFFER = 20480;
    
    public CallWebService() {
	}
    
    public URL testWebServer(String sUrl, int iTimeout){
        URL url;
        try{
	        url = new URL(sUrl);
	        String ipAddr = url.getHost();
	        int iPort = url.getPort();
	        if(iTimeout > 0){
	            Socket socket = new Socket();
	            InetSocketAddress address = new InetSocketAddress(ipAddr, iPort);
	            socket.connect(address, iTimeout);
	            socket.close();
	        }
	        return url;
        }catch(Exception ex){
	        ex.printStackTrace();
	        return null;
        }
    }
    
    public String doAction(String sMethod, String sUrl, byte aRequestContent[]){
    	String result = "";
        OutputStream out = null;
        InputStream ins = null;
        URL url = null;
        URLConnection uc = null;
        HttpURLConnection urlcon = null;
        byte aResult[] = null;
        ByteArrayOutputStream os = null;
        try{
        	url = this.testWebServer(sUrl, 0);
        	uc = url.openConnection();
        	if(uc instanceof HttpURLConnection){
        		urlcon = (HttpURLConnection)uc;
                urlcon.setRequestMethod("POST");
//				urlcon.setConnectTimeout(20000);//连接主机超时(单位毫秒)
//				urlcon.setReadTimeout(20000);//从主机读取数据超时(单位毫秒)
                //urlcon.setRequestProperty("Content-Type","text/xml; charset=utf-8");
                if(aRequestContent != null && aRequestContent.length > 0){
                	urlcon.setDoOutput(true);
                    out = urlcon.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);
                    dos.write(aRequestContent);
                    dos.flush();
                    dos.close();
                    
                    out = null;
                    ins = urlcon.getInputStream();
                    aResult = null;
                    os = null;
                    os = new ByteArrayOutputStream();
                    int d = 0;
                    byte bytes[] = new byte[MAX_REC_BUFFER];
                    while((d = ins.read(bytes)) != -1) 
                        os.write(bytes, 0, d);
                    aResult = os.toByteArray();
                }
        	}
        }catch(Exception e){
        	e.printStackTrace();
        	result = result + "\r\n" + e.toString();
        }finally{
        	try{
	        	if(os != null)
	        		os.close();
	        	if(out != null)
	                out.close();
	        	if(ins != null)
	                ins.close();
        	}catch(Exception ex){
        		ex.printStackTrace();
            	result = result + "\r\n" + ex.toString();
        	}
        }
        try{
        	result = new String(aResult, "UTF-8");
        }catch(Exception e){
        	e.printStackTrace();
        	result = result + "\r\n" + e.toString();
        }
    	return result;
    }
    
    public String setSendXML(int flag){
		StringBuffer xml = new StringBuffer();
		if(flag==1){
			xml.append("<?xml version=\"1.0\"  encoding='GBK'?>");
			xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.ws.test.gary.com/\">")
			.append("<soapenv:Header/>")
			.append("<soapenv:Body>")
			.append("<ser:greeting>")
			.append("<arg0>gary</arg0>")
			.append("</ser:greeting>")
			.append("</soapenv:Body>")
			.append("</soapenv:Envelope>");
		}else if(flag==2){
			xml.append("<?xml version=\"1.0\"  encoding='GBK'?>");
			xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ng=\"http://ng.busiapp.webservice.bi.newland.com/\">")
			.append("<soapenv:Header></soapenv:Header>")
			.append("<soapenv:Body>")
			.append("<ng:qryNetLogList>")
			.append("<message>")
			.append("<HeaderReq>")
			.append("<User>")
			.append("<IdentityInfo>")
			.append("<ClientID></ClientID>")
			.append("<PassWord></PassWord>")
			.append("</IdentityInfo>")
			.append("<TokenInfo>")
			.append("<TokenCode></TokenCode>")
			.append("</TokenInfo>")
			.append("</User>")
			.append("<System>")
			.append("<ReqSource>701101</ReqSource>")
			.append("<ReqTime>20130902092723</ReqTime>")
			.append("<ReqSeq>701101010902092723780</ReqSeq>")
			.append("<ReqModule></ReqModule>")
			.append("<ReqType></ReqType>")
			.append("<ReqVersion>1.0</ReqVersion>")
			.append("</System>")
			.append("<Route>")
			.append("<RouteId></RouteId>")
			.append("<RouteType></RouteType>")
			.append("</Route>")
			.append("</HeaderReq>")
			.append("<BodyReq>")
			.append("<ReqData>")
			.append("<NgReqBean>")
			.append("<telnumber>13509323824</telnumber>")
			.append("<starttime_s>20131020</starttime_s>")
			.append("<starttime_e>20131020</starttime_e>")
			.append("<apn></apn>")
			.append("<servicename></servicename>")
			.append("</NgReqBean>")
			.append("</ReqData>")
			.append("<start>1</start>")
			.append("<pageCount>10</pageCount>")
			.append("</BodyReq>")
			.append("</message>")
			.append("</ng:qryNetLogList>")
			.append("</soapenv:Body>")
			.append("</soapenv:Envelope>");
		}else if(flag==3){
			xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
			.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">")
			.append("<soap:Body>")
			.append("<qqCheckOnline xmlns=\"http://WebXml.com.cn/\">")
			.append("<qqCode>306469477</qqCode>")
			.append("</qqCheckOnline>")
			.append("</soap:Body>")
			.append("</soap:Envelope>");
		}
		return xml.toString();
	}
    
    public String setSendXML(String head, String ClientID, String PassWord,String telnumber, String start_time, String end_time,
    		String start, String end, String apn, String service_name, String reqsource){
		StringBuffer xml = new StringBuffer();
			xml.append("<?xml version=\"1.0\"  encoding='GBK'?>");
			xml.append(head)
			//.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ng=\"http://ng.busiapp.webservice.bi.newland.com/\">")
			.append("<soapenv:Header></soapenv:Header>")
			.append("<soapenv:Body>")
			.append("<ser:qryNetLogList>")
			.append("<message>")
			.append("<HeaderReq>")
			.append("<User>")
			.append("<IdentityInfo>")
			.append("<ClientID>"+ClientID+"</ClientID>")
			.append("<PassWord>"+PassWord+"</PassWord>")
			.append("</IdentityInfo>")
			.append("<TokenInfo>")
			.append("<TokenCode></TokenCode>")
			.append("</TokenInfo>")
			.append("</User>")
			.append("<System>")
			.append("<ReqSource>701601</ReqSource>")
			.append("<ReqTime>20130826161849</ReqTime>")
			.append("<ReqSeq>701101010826161849380</ReqSeq>")
			.append("<ReqModule></ReqModule>")
			.append("<ReqType></ReqType>")
			.append("<ReqVersion></ReqVersion>")
			.append("</System>")
			.append("<Route>")
			.append("<routeId></routeId>")
			.append("<routeType></routeType>")
			.append("<routeValue></routeValue>")
			.append("</Route>")
			.append("</HeaderReq>")
			.append("<BodyReq>")
			.append("<ReqData>")
			.append("<NgReqBean>")
			.append("<telnumber>"+telnumber+"</telnumber>")
			.append("<starttime_s>"+start_time+"</starttime_s>")
			.append("<starttime_e>"+end_time+"</starttime_e>")
			.append("<apn>"+apn+"</apn>")
			.append("<servicename>"+service_name+"</servicename>")
			.append("<reqSource>"+reqsource+"</reqSource>")
			.append("</NgReqBean>")
			.append("</ReqData>")
			.append("<start>"+start+"</start>")
			.append("<pageCount>"+end+"</pageCount>")
			.append("</BodyReq>")
			.append("</message>")
			.append("</ser:qryNetLogList>")
			.append("</soapenv:Body>")
			.append("</soapenv:Envelope>");
		return xml.toString();
	}

    public String setSendXML(String head, String ClientID, String PassWord, NgReqBean requestBean, String start, String end){
		StringBuffer xml = new StringBuffer();
			xml.append("<?xml version=\"1.0\"  encoding='GBK'?>");
			xml.append(head)
			.append("<soapenv:Header></soapenv:Header>")
			.append("<soapenv:Body>")
			.append("<ser:qryNetLogList>")
			.append("<message>")
			.append("<HeaderReq>")
			.append("<User>")
			.append("<IdentityInfo>")
			.append("<ClientID>"+ClientID+"</ClientID>")
			.append("<PassWord>"+PassWord+"</PassWord>")
			.append("</IdentityInfo>")
			.append("<TokenInfo>")
			.append("<TokenCode></TokenCode>")
			.append("</TokenInfo>")
			.append("</User>")
			.append("<System>")
			.append("<ReqSource>701601</ReqSource>")
			.append("<ReqTime>20130826161849</ReqTime>")
			.append("<ReqSeq>701101010826161849380</ReqSeq>")
			.append("<ReqModule></ReqModule>")
			.append("<ReqType></ReqType>")
			.append("<ReqVersion></ReqVersion>")
			.append("</System>")
			.append("<Route>")
			.append("<routeId></routeId>")
			.append("<routeType></routeType>")
			.append("<routeValue></routeValue>")
			.append("</Route>")
			.append("</HeaderReq>")
			.append("<BodyReq>")
			.append("<ReqData>")
			.append("<NgReqBean>")
			.append("<telnumber>"+requestBean.getTelnumber()+"</telnumber>")
			.append("<starttime_s>"+requestBean.getStarttime_s()+"</starttime_s>")
			.append("<starttime_e>"+requestBean.getStarttime_e()+"</starttime_e>")
			.append("<apn>"+requestBean.getApn()+"</apn>")
			.append("<servicename>"+requestBean.getServicename()+"</servicename>")
			.append("<reqSource>"+requestBean.getReqSource()+"</reqSource>")
			.append("</NgReqBean>")
			.append("</ReqData>")
			.append("<start>"+start+"</start>")
			.append("<pageCount>"+end+"</pageCount>")
			.append("</BodyReq>")
			.append("</message>")
			.append("</ser:qryNetLogList>")
			.append("</soapenv:Body>")
			.append("</soapenv:Envelope>");
		return xml.toString();
	}
    
    public void qqCall(String urlString, String xmlFile){
    	//String urlString = //"http://www.webxml.com.cn/webservices/qqOnlineWebService.asmx";//此为提供的webservice地址
    	//"http://127.0.0.1:8080/SpringCXFWebService/GreetingService";
        //String xmlFile = "QQOnlineService.XML";//发给对方的xml文档，在webservice说明中，查看soap发送部分即可
        //String soapActionString = "http://WebXml.com.cn/qqCheckOnline";//此为调用的方法qqCheckOnline和命名空间
        try{
	        URL url = new URL(urlString);	
	        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        
	        File fileToSend=new File(xmlFile);
	        byte[] buf=new byte[(int)fileToSend.length()];
	        new FileInputStream(xmlFile).read(buf);
	        //httpConn.setRequestProperty("Content-Length",String.valueOf(buf.length));
	        httpConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
	        //httpConn.setRequestProperty("soapActionString",soapActionString);
	        httpConn.setRequestMethod("POST");
	        httpConn.setDoOutput(true);
	        httpConn.setDoInput(true);
	        OutputStream out = httpConn.getOutputStream();
	        out.write(buf);
	        out.close();
	       
	        InputStreamReader isr = new InputStreamReader(httpConn.getInputStream(),"utf-8");
	        BufferedReader in = new BufferedReader(isr);
	       
	        String inputLine;
	        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("result.xml")));//本地生成的xml文档
	        while ((inputLine = in.readLine()) != null){
	            System.out.println(inputLine);
	            bw.write(inputLine);
	            bw.newLine();
	        }
	        bw.close();
	        in.close();
        }catch(Exception e){
        	e.printStackTrace();
        }
    }
}
