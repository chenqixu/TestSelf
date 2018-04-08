package com.cqx.searchcrawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Read TXT<br>
 * grep -e 'Fixed in Apache Tomcat' -e 'CVE-' 7.85.txt |more<br>
 * Value FROM "https://tomcat.apache.org/security-7.html"<br>
 * Value FROM "https://tomcat.apache.org/security-6.html"<br>
 * */
public class TomcatCVEDeal {
	public void dealHttp(String url) throws Exception {
//		WebClient webClient = new WebClient();
//		webClient.setJavaScriptEnabled(true);
//		webClient.setCssEnabled(false);
//		webClient.setActiveXNative(false);
//		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		webClient.setTimeout(1000000);
//		webClient.setThrowExceptionOnScriptError(false);
//		webClient.setThrowExceptionOnFailingStatusCode(false);
//		HtmlPage rootPage = webClient.getPage(url);
//		HtmlElement priceElement = rootPage.getElementById("promotionPrice");		
//		String price = priceElement.asText();
//		System.out.println(price.replace("?", ""));
	}

	public void dealHttps(String url) throws Exception {
		WebClient webClient = new WebClient();
//		webClient.setJavaScriptEnabled(true);
//		webClient.setCssEnabled(false);
//		webClient.setActiveXNative(false);
//		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		webClient.setTimeout(1000000);
//		webClient.setThrowExceptionOnScriptError(false);
//		webClient.setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setUseInsecureSSL(true);
		HtmlPage rootPage = webClient.getPage(url);
		DomElement contentElement = rootPage.getElementById("content");
		int i = 0;
		String tmp = "";
		for(DomElement childe : contentElement.getChildElements()){
			i++;
			String content = childe.asText();
			tmp = content;
//			if(i==3)	System.out.println(content);
			if(i==3)break;
		}
		String[] list = tmp.split("\r\n");
		for(String s : list){
			if(s.indexOf("Fixed")>=0){
				String title = s.replaceFirst(" ", "").replaceAll(" ", "_");
				DomElement fixed = rootPage.getElementById(title);
				String fixedstr = fixed.asText();
//				System.out.println("[fixed]"+fixedstr);
				DomElement nextElement = fixed.getNextElementSibling();
				for(DomElement nextchilde : nextElement.getChildElements()) {
//					System.out.println("[nextchilde]"+nextchilde.asText());
//					System.out.println("[nextchilde.cnt]"+nextchilde.getChildElementCount());
					if(nextchilde.getChildElementCount()>0)
					for(DomElement endnextchilde : nextchilde.getChildElements()) {
						String endstr = endnextchilde.asText();
						if(endstr.indexOf("CVE-")>=0)System.out.println(fixedstr+"TAB"+endstr);
					}
				}
//				break;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		TomcatCVEDeal tcd = new TomcatCVEDeal();
//		tcd.dealHttps("https://tomcat.apache.org/security-6.html");
		tcd.dealHttps("https://tomcat.apache.org/security-7.html");
	}
}
