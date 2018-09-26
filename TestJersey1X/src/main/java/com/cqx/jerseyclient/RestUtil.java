package com.cqx.jerseyclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;

import com.cqx.bean.RestParam;
import com.cqx.util.LogUtil;

public class RestUtil {
	
	private static LogUtil log = LogUtil.getInstance();
	
	/**
	 * ����http/https���󲢻�ȡ���
	 */
	public static JSONObject httpRequest(RestParam restParam) {
		JSONObject jsonObject = null;
		// �������������
		InetSocketAddress addr = null;
		Proxy proxy = null;
		boolean ifProxyModel = restParam.getIfProxy() != null
				&& restParam.getIfProxy() != ""
				&& "TRUE".equals(restParam.getIfProxy());

		if (ifProxyModel) {
			addr = new InetSocketAddress(restParam.getProxyAddress(),
					Integer.parseInt(restParam.getProxyPort()));
			proxy = new Proxy(Proxy.Type.HTTP, addr); // http ����
//			Authenticator.setDefault(new MyAuthenticator(restParam
//					.getProxyUser(), restParam.getProxyPassWord()));// ���ô�����û�������
		}

		try {

			URL url = new URL(restParam.getReqUrl());
			if ("https".equals(restParam.getReqHttpsModel())) {
				TrustManager[] tmCerts = new javax.net.ssl.TrustManager[1];
				tmCerts[0] = new SimpleTrustManager();
				try {
					SSLContext sslContext = SSLContext.getInstance("SSL");
					sslContext.init(null, tmCerts, null);
					HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
							.getSocketFactory());

					HostnameVerifier hostnameVerifier = new SimpleHostnameVerifier();
					HttpsURLConnection
							.setDefaultHostnameVerifier(hostnameVerifier);
				} catch (Exception e) {
					e.printStackTrace();
				}
				HttpsURLConnection httpUrlConn = null;
				if (ifProxyModel) {
					httpUrlConn = (HttpsURLConnection) url
							.openConnection(proxy);
				} else {
					httpUrlConn = (HttpsURLConnection) url.openConnection();
				}

				// httpUrlConn.setSSLSocketFactory(ssf);
				jsonObject = ardoHttpsURLConnection(httpUrlConn,
						restParam.getReqMethod(), restParam.getReqContent(),
						restParam.getSessionId());
			} else {
				HttpURLConnection httpUrlConn = null;
				if (ifProxyModel) {
					httpUrlConn = (HttpURLConnection) url.openConnection(proxy);
				} else {
					httpUrlConn = (HttpURLConnection) url.openConnection();
				}
				jsonObject = ardoHttpURLConnection(httpUrlConn,
						restParam.getReqMethod(), restParam.getReqContent(),
						restParam.getSessionId());

			}

		} catch (ConnectException ce) {
			log.error("API server connection timed out.");
			log.error("��rest�����쳣��Ϣ��" + ce.getMessage());
		} catch (Exception e) {
			log.error("API https or http request error:{}", e);
			log.error("��rest�쳣��Ϣ��" + e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * http���󷽷�
	 * 
	 * @param httpUrlConn
	 *            ����·��
	 * @param requestMethod
	 *            ��������POST|GET
	 * @param outputStr
	 *            ��������
	 * @param sessionId
	 *            sessionId(�Ǳ���)
	 * @return JSONObject��������
	 */
	public static JSONObject ardoHttpURLConnection(
			HttpURLConnection httpUrlConn, String requestMethod,
			String outputStr, String sessionId) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {

			// httpUrlConn = (HttpURLConnection) url.openConnection();

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);

			if (sessionId != null && sessionId != "") {
				httpUrlConn.setRequestProperty("Cookie", "JSESSIONID="
						+ sessionId);
			}

			// ��������ʽGET/POST
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// ����������Ҫ�ύʱ
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// ע������ʽ����ֹ��������
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// �����ص�������ת�����ַ���
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// �ͷ���Դ
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (ConnectException ce) {
			log.error("API server connection timed out.");
			log.error("��rest http�����쳣��Ϣ��" + ce.getMessage());
		} catch (Exception e) {
			log.error("API http request error:{}", e);
			log.error("��rest http�쳣��Ϣ��" + e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * https���󷽷�
	 * 
	 * @param httpUrlConn
	 *            ����·��
	 * @param requestMethod
	 *            ��������POST|GET
	 * @param outputStr
	 *            ��������
	 * @param sessionId
	 *            sessionId(�Ǳ���)
	 * @return JSONObject��������
	 */
	public static JSONObject ardoHttpsURLConnection(
			HttpsURLConnection httpUrlConn, String requestMethod,
			String outputStr, String sessionId) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {

			// httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);

			if (sessionId != null && sessionId != "") {
				httpUrlConn.setRequestProperty("Cookie", "JSESSIONID="
						+ sessionId);
			}

			// ��������ʽGET/POST
			httpUrlConn.setRequestMethod(requestMethod);
			httpUrlConn.setRequestProperty("Content-Type", "application/json");

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// ����������Ҫ�ύʱ
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// ע������ʽ����ֹ��������
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// �����ص�������ת�����ַ���
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// �ͷ���Դ
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (ConnectException ce) {
			log.error("API server connection timed out.");
			log.error("��rest https�����쳣��Ϣ��" + ce.getMessage());
		} catch (Exception e) {
			log.error("API https request error:{}", e);
			log.error("��rest https�쳣��Ϣ��" + e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * ����ģʽ�������֤
	 * 
	 * @author ardo
	 *
	 */
	static class MyAuthenticator extends Authenticator {
		private String user = "";
		private String password = "";

		public MyAuthenticator(String user, String password) {
			this.user = user;
			this.password = password;
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, password.toCharArray());
		}
	}

	// test url
	// public static String menu_create_url =
	// "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

	private static class SimpleTrustManager implements TrustManager,
			X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			return;
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			return;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static class SimpleHostnameVerifier implements HostnameVerifier {

		public boolean verify(String hostname, SSLSession session) {
			return true;
		}

	}

	public static void main(String[] args) {
		// Test for rest
		RestUtil.httpRequest(new RestParam(
				"http://10.47.248.26:18160/services/env/conn/detail/name?connName=hive-fjedcmultenahd-fj_edc_middle",
				"POST", "", "http", "", "TRUE", "10.1.2.199", "8123",
				"", ""));
	}
}
