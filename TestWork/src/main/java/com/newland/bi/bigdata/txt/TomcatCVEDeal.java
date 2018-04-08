package com.newland.bi.bigdata.txt;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Read TXT<br>
 * grep -e 'Fixed in Apache Tomcat' -e 'CVE-' 7.85.txt |more<br>
 * Value FROM "https://tomcat.apache.org/security-7.html"<br>
 * Value FROM "https://tomcat.apache.org/security-6.html"<br>
 * */
public class TomcatCVEDeal {
	public void dealHttp(String url) throws Exception {
		// 模拟一个浏览器
		WebClient webClient = new WebClient();
		// 设置webClient的相关参数
//		webClient.setJavaScriptEnabled(true);
//		webClient.setCssEnabled(false);
//		webClient.setActiveXNative(false);
//		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		webClient.setTimeout(1000000);
//		webClient.setThrowExceptionOnScriptError(false);
//		webClient.setThrowExceptionOnFailingStatusCode(false);
		// 模拟浏览器打开一个目标网址
		HtmlPage rootPage = webClient.getPage(url);
//		HtmlElement priceElement = rootPage.getElementById("promotionPrice");
		
//		String price = priceElement.asText();
//		System.out.println(price.replace("?", ""));
	}

	public void dealHttps(String url) throws Exception {
//		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//		trustStore.load(null, null);
//		SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
//		// 允许所有主机的验证
//		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		// 模拟一个浏览器
		WebClient webClient = new WebClient();
		// 设置webClient的相关参数
//		webClient.setJavaScriptEnabled(true);
//		webClient.setCssEnabled(false);
//		webClient.setActiveXNative(false);
//		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		webClient.setTimeout(1000000);
//		webClient.setThrowExceptionOnScriptError(false);
//		webClient.setThrowExceptionOnFailingStatusCode(false);
//		webClient.getOptions().setSSLClientCertificate(URL,PASSWORD,"pkcs12");
		webClient.getOptions().setUseInsecureSSL(true);
		// 模拟浏览器打开一个目标网址
		HtmlPage rootPage = webClient.getPage(url);
//		HtmlElement priceElement = rootPage.getElementById("promotionPrice");		
//		String price = priceElement.asText();
//		System.out.println(price.replace("?", ""));
	}
	
	public static void main(String[] args) throws Exception {
		TomcatCVEDeal tcd = new TomcatCVEDeal();
		tcd.dealHttps("https://tomcat.apache.org/security-6.html");
	}
}
