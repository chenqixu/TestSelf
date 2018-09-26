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
 * @Description:ͨ������ʵ��Java�������ָ��URL
 * @date: 2017��8��24��
 * @�޸ı�ע:
 */
public class OpenUrl_Proxy {
	static String proxy_ip = "10.1.2.199";
	static int proxy_port = 8123;

	/**
	 * @Description:HttpComponents--HttpClient��ʽ����ָ��URL 
	 *                                                  ͨ������Ϊ�������İ�ȫ���ò�����Java������Ϊ�ͻ��˷���
	 *                                                  ��������������ÿͻ��˵�User Agent
	 * @date: 2017��8��24�� ����7:45:14
	 * @�޸ı�ע:
	 */
	@SuppressWarnings("deprecation")
	public static void openUrl_httpComponents(String url) {
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			int httpStatusCode = response.getStatusLine().getStatusCode();
			// ����һЩ�������⣬���ʰٶ���ҳ���ܷ��ص�httpStatusCode��403������200
			// ��ԭ����ǿ��ܰٶȷ�������֧��ͨ������������url
			if (HttpStatus.SC_OK == httpStatusCode) {
				System.out.println("��ӡ���������ص�״̬: " + response.getStatusLine());
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					System.out
							.println("��ӡ������Ϣ:" + EntityUtils.toString(entity));// ��ӡ������Ϣ
					// entity.consumeContent();//�ͷ���Դ
					EntityUtils.consume(entity);// �ͷ���Դ this is the new consume
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
	 * @Description:ͨ����Ӵ���ʽ��ʵ�ַ���ָ��URL
	 * @date: 2017��8��24�� ����8:21:26
	 * @�޸ı�ע:
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
	 * @Description:ͨ����Ӵ���ʽ��ʵ�ַ���ָ��URL 
	 *                                 ͨ������Ϊ�������İ�ȫ���ò�����Java������Ϊ�ͻ��˷��ʣ�������������ÿͻ��˵�User
	 *                                 Agent
	 * @date: 2017��8��24�� ����8:22:19
	 * @�޸ı�ע:
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
