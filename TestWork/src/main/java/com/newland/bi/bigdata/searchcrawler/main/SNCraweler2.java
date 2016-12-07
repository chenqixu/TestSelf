package com.newland.bi.bigdata.searchcrawler.main;

import java.io.IOException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SNCraweler2 {
	public static void main(String[] args) throws FailingHttpStatusCodeException, IOException {
		String refer = "http://open-open.com/";
		String url="http://product.suning.com/123129015.html";//想采集的网址
		URL link = new URL(url);
		WebClient wc = new WebClient();
		WebRequest request = new WebRequest(link);
		request.setCharset("UTF-8");
//		request.setProxyHost("120.120.120.x");
//		request.setProxyPort(8080);
		request.setAdditionalHeader("Referer", refer);// 设置请求报文头里的refer字段
		// //设置请求报文头里的User-Agent字段
		request.setAdditionalHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		// wc.addRequestHeader("User-Agent",
		// "Mozilla/5.0 (Windows NT 5.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		// wc.addRequestHeader和request.setAdditionalHeader功能应该是一样的。选择一个即可。
		// 其他报文头字段可以根据需要添加
		wc.getCookieManager().setCookiesEnabled(false);// 开启cookie管理
		wc.setJavaScriptEnabled(true);// 开启js解析。对于变态网页，这个是必须的
		wc.setCssEnabled(false);// 开启css解析。对于变态网页，这个是必须的。
		wc.setThrowExceptionOnFailingStatusCode(false);
		wc.setThrowExceptionOnScriptError(false);
//		wc.setTimeout(10000);
//		// 设置cookie。如果你有cookie，可以在这里设置
//		Set<Cookie> cookies = null;
//		Iterator<Cookie> i = cookies.iterator();
//		while (i.hasNext()) {
//			wc.getCookieManager().addCookie(i.next());
//		}
		// 准备工作已经做好了
		HtmlPage page = null;
		page = wc.getPage(request);
		if (page == null) {
			System.out.println("采集 " + url + " 失败!!!");
			return;
		}
		String content = page.asText();// 网页内容保存在content里
		if (content == null) {
			System.out.println("采集 " + url + " 失败!!!");
			return;
		}
	}
}
