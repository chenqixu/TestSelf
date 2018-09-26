package com.cqx.jerseyclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.util.EntityUtils;

public class RestTemplate {	

	public static void openUrl_httpComponents(HttpClient client, String url) {
//		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = client.execute(get);
			int httpStatusCode = response.getStatusLine().getStatusCode();
			System.out.println("[httpStatusCode]"+httpStatusCode);
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
	
	public static void main(String[] args) {
		String proxy_ip = "10.1.2.199";
		int proxy_port = 8123;
		String url = "http://10.47.248.20:8020/services/env/conn/detail/name?connName=hive-fjedcmultenahd-fj_edc_middle";
//		CredentialsProvider credsProvider = new BasicCredentialsProvider();
//		credsProvider.setCredentials(new AuthScope(proxy_ip, proxy_port),
//				new UsernamePasswordCredentials("", ""));
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.useSystemProperties();
		clientBuilder.setProxy(new HttpHost(proxy_ip, proxy_port));
//		clientBuilder.setDefaultCredentialsProvider(credsProvider);
		clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
		CloseableHttpClient client = clientBuilder.build();
		openUrl_httpComponents(client, url);
//		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//		factory.setHttpClient(client);
//		restTemplate.setRequestFactory(factory);
//		String re = restTemplate.getForObject(
//				"http://172.22.1.55:8080/asdfa/test", String.class);
//		System.out.println(re);
	}
}
