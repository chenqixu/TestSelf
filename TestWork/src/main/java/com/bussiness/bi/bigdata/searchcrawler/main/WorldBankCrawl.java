package com.bussiness.bi.bigdata.searchcrawler.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public class WorldBankCrawl {

	private static String TARGET_URL = "http://product.suning.com/123129015.html";

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException,
			IOException {
		long startTime = System.currentTimeMillis();
		// 模拟一个浏览器
		WebClient webClient = new WebClient();
		// 设置webClient的相关参数
		webClient.setJavaScriptEnabled(true);
		webClient.setActiveXNative(false);
		webClient.setCssEnabled(false);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		webClient.setTimeout(105000);
		webClient.setThrowExceptionOnScriptError(false);
		webClient.setThrowExceptionOnFailingStatusCode(false);
		// 模拟浏览器打开一个目标网址
		HtmlPage page = webClient.getPage(TARGET_URL);
//		page.save(new File("F:/1.html"));
		String str = page.asText();
        System.out.println(str);
        // 关闭webclient
        webClient.closeAllWindows();
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("totalTime:"+totalTime);
	}
}