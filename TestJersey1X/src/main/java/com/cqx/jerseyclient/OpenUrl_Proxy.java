package com.cqx.jerseyclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @ClassName: OpenUrl_Proxy
 * @Description:通过代理实现Java代码访问指定URL
 * @date: 2017年8月24日
 * @修改备注:
 */
public class OpenUrl_Proxy {
	static String proxy_ip = "10.1.2.199";
	static int proxy_port = 8123;

	/**
	 * @Description:HttpComponents--HttpClient方式访问指定URL 
	 *                                                  通常是因为服务器的安全设置不接受Java程序作为客户端访问
	 *                                                  ，解决方案是设置客户端的User Agent
	 * @date: 2017年8月24日 下午7:45:14
	 * @修改备注:
	 */
	@SuppressWarnings("deprecation")
	public static void openUrl_httpComponents(String url) {
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			int httpStatusCode = response.getStatusLine().getStatusCode();
			// 由于一些设置问题，访问百度首页可能返回的httpStatusCode是403，不是200
			// 其原因就是可能百度服务器不支持通过代码来调用url
			if (HttpStatus.SC_OK == httpStatusCode) {
				System.out.println("打印服务器返回的状态: " + response.getStatusLine());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					System.out
							.println("打印返回信息:" + EntityUtils.toString(entity));// 打印返回信息
					// entity.consumeContent();//释放资源
					EntityUtils.consume(entity);// 释放资源 this is the new consume
												// method
				}
			} else {
				System.out.println("not ok");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		client.getConnectionManager().shutdown();
	}

	/**
	 * @Description:通过添加代理方式，实现访问指定URL
	 * @date: 2017年8月24日 下午8:21:26
	 * @修改备注:
	 */
	public static void openUrl_setProxy_1(String urlStr) {
		InetSocketAddress inetAddress = null;
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			inetAddress = new InetSocketAddress(
					InetAddress.getByName(proxy_ip), proxy_port);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		URLConnection con = null;
		try {
			con = url.openConnection(new Proxy(Type.HTTP, inetAddress));
//			con.setRequestProperty("No Proxy-Authorization", "true");
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(con.getInputStream(),
					"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String str = null;
		StringBuffer sb = new StringBuffer();
		try {
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(sb.toString());
	}

	/**
	 * @Description:通过添加代理方式，实现访问指定URL 
	 *                                 通常是因为服务器的安全设置不接受Java程序作为客户端访问，解决方案是设置客户端的User
	 *                                 Agent
	 * @date: 2017年8月24日 下午8:22:19
	 * @修改备注:
	 */
	public static void openUrl_setProxy_2(String urlStr) {
		String host = proxy_ip;
		String port = String.valueOf(proxy_port);
		setHttpProxy(host, port);
//		setSocksProxy(host, port);
		URL url = null;
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		InputStream is = null;
		try {
			is = url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println(url.getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setHttpProxy(String host, String port) {
		System.setProperty("proxySet", "true");
		System.setProperty("http.proxyHost", host);
		System.setProperty("http.proxyPort", port);
	}
	
	public static void setSocksProxy(String host, String port) {
		System.setProperty("proxySet", "true");  
		System.setProperty("socksProxyHost", host);  
		System.setProperty("socksProxyPort", port); 
	}

	public static void main(String[] args) {
		String url = "http://10.47.248.26:18160/services/env/conn/detail/name?connName=hive-fjedcmultenahd-fj_edc_middle";
//		String url = "http://10.1.4.185:9295/services/env/conn/detail/name?connName=bishow";
//		String url = "http://httpd.apache.org/";
//		String url = "http://10.1.8.75:19888/jobhistory/logs/node81:8041/container_1533174517432_0066_01_000001/job_1533174517432_0066/hive";
//		openUrl_setProxy_1(url);
		openUrl_setProxy_2(url);
	}

}
