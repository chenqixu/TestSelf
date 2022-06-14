package com.bussiness.bi.bigdata.http.frame;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;


/**
 * description:调用外部webservices
 * @author cqx
 * @version 1.0.0
 * @since 2015-12-09
 * */
public class CallWebService {
	// 缓冲字节
    public static int MAX_REC_BUFFER = 20480;
    
    /**
     * 单态
     * */
    private static CallWebService cwb = new CallWebService();    
    private CallWebService(){}    
    public static CallWebService getInstance(){
    	if(cwb==null)cwb=new CallWebService();
    	return cwb;
    }
    
    /**
     * 测试连接是否异常
     * @param sUrl 访问地址
     * @param iTimeout 超时时间
     * @return URL
     * */
    public URL testWebServer(String sUrl, int iTimeout){
        URL url;
        try{
	        url = new URL(sUrl);
	        // 地址
	        String ipAddr = url.getHost();
	        // 端口
	        int iPort = url.getPort();
	        if(iTimeout > 0){
	            Socket socket = new Socket();
	            // 通过地址、端口创建套接字地址
	            InetSocketAddress address = new InetSocketAddress(ipAddr, iPort);
	            // 测试连接
	            socket.connect(address, iTimeout);
	            // 关闭测试连接
	            socket.close();
	        }
	        return url;
        }catch(Exception ex){
	        ex.printStackTrace();
	        // 异常返回空值
	        return null;
        }
    }
    
    /**
     * 发送报文并返回结果
     * @param sMethod 请求方式{"GET","POST"}
     * @param sUrl 服务地址
     * @param aRequestContent 请求报文
     * @return 返回结果
     * */
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
        	// 测试连接是否异常
        	url = this.testWebServer(sUrl, 0);
        	// 打开连接
        	uc = url.openConnection();
        	if(uc instanceof HttpURLConnection){
        		urlcon = (HttpURLConnection)uc;
        		// 设置POST请求方式
                urlcon.setRequestMethod("POST");
                if(aRequestContent != null && aRequestContent.length > 0){
                	// 设置发送内容
                	urlcon.setDoOutput(true);
                	// 获得发送内容的输出流
                    out = urlcon.getOutputStream();
                    // 新建数据输出流
                    DataOutputStream dos = new DataOutputStream(out);
                    // 写入XML报文并发送
                    dos.write(aRequestContent);
                    dos.flush();
                    dos.close();
                    
                    out = null;
                    // 获得返回结果的输入流
                    ins = urlcon.getInputStream();
                    aResult = null;
                    os = null;
                    os = new ByteArrayOutputStream();
                    int d = 0;
                    byte bytes[] = new byte[MAX_REC_BUFFER];
                    // 读取返回结果
                    while((d = ins.read(bytes)) != -1) 
                        os.write(bytes, 0, d);
                    aResult = os.toByteArray();
                }
        	}
        }catch(Exception e){
        	e.printStackTrace();
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
        	}
        }
        try{
        	result = new String(aResult);
        }catch(Exception e){
        	e.printStackTrace();
        }
    	return result;
    }    
}

