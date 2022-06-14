package com.bussiness.bi.bigdata.searchcrawler.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SNCraweler extends Thread {
	public static Logger log = Logger.getLogger(SNCraweler.class);
	
	private WebClient webClient = null;
	private BufferedWriter out = null;
	private LinkedList<String> toCrawlList = null;
	private String threadName = "";
	
	public SNCraweler() {
	}

	public SNCraweler(BufferedWriter bw, LinkedList<String> list) {
		// 模拟一个浏览器
		webClient = new WebClient();
		// 设置webClient的相关参数
		webClient.setJavaScriptEnabled(true);
		webClient.setCssEnabled(false);
		webClient.setActiveXNative(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		// webClient.setTimeout(100000);
		webClient.setThrowExceptionOnScriptError(false);
		webClient.setThrowExceptionOnFailingStatusCode(false);		
		
		this.out = bw;
		this.toCrawlList = list;
		this.threadName = Thread.currentThread().getName();
	}

	private void CloseWeb() {
		if (webClient != null) {
			webClient.closeAllWindows();
			webClient = null;
		}
	}
	
	public void run(){
		try{
			log.info(threadName+" [start run]...");
			// 获取商品信息保存到文件
			for (String link : toCrawlList) {
				String phoneRecord = getPhoneMessage(link);
				log.info(threadName+" [link]:"+link+" [phoneRecord]:" + phoneRecord);
				synchronized (out) {
					out.write(phoneRecord);
					out.newLine();
					out.flush();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.toString(), e);
		}finally{			
			CloseWeb();
		}
	}

	private String getPhoneMessage(String link) {
		String phoneRecord = null;
		Map<String, String> parameterMap = new HashMap<String, String>();
		// 通过http连接获取手机参数
		parameterMap = getPhoneParameter(link);
		// 获取需要输出的手机参数
		phoneRecord = getPhoneRecord(parameterMap);
		return phoneRecord;
	}

	public String downloadPage(String page) {
		try {
			URL pageUrl = new URL(page);
			// Open connection to URL for reading.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					pageUrl.openStream(), "utf-8"));
			// Read page into buffer.
			String line;
			StringBuffer pageBuffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				pageBuffer.append(line);
			}
			return pageBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString(), e);
		}
		return null;
	}

	/**
	 * 解析页面并找出链接
	 * */
	public ArrayList<String> retrieveLinks(String pageContents) {
		// 用正则表达式编译链接的匹配模式。
		// Pattern p = Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]",
		// Pattern.CASE_INSENSITIVE);
		Pattern p = Pattern.compile(
				"href=\"(http://product.suning.com/\\d+.html)",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(pageContents);

		ArrayList<String> linkList = new ArrayList<String>();
		while (m.find()) {
			String link = m.group(1).trim();
			if (link.length() < 1) {
				continue;
			}
			linkList.add(link);
		}
		return (linkList);
	}

	/**
	 * 获取需要输出的手机参数
	 * */
	private String getPhoneRecord(Map<String, String> parameterMap) {
		String phoneRecord = null;
		StringBuffer sb = new StringBuffer();
		String tmp = null;
		int lastIndexOfComma;
		// 品牌+型号
		tmp = parameterMap.get("品牌");
		if (tmp != null) {
			sb.append(tmp);
		}
		tmp = parameterMap.get("型号");
		if (tmp != null) {
			sb.append(tmp);
		}
		sb.append("\t");

		// 4G网络制式+","+3G网络制式+","+2G网络制式+","+待机模式
		tmp = parameterMap.get("4G网络制式");
		if (tmp != null) {
			sb.append(tmp).append(",");
		}
		tmp = parameterMap.get("3G网络制式");
		if (tmp != null) {
			sb.append(tmp).append(",");
		}
		tmp = parameterMap.get("2G网络制式");
		if (tmp != null) {
			sb.append(tmp).append(",");
		}
		tmp = parameterMap.get("待机模式");
		if (tmp != null) {
			sb.append(tmp);
		}
		lastIndexOfComma = sb.lastIndexOf(",");
		if (lastIndexOfComma == sb.length() - 1) {
			sb.deleteCharAt(lastIndexOfComma);
		}
		sb.append("\t");

		// 屏幕尺寸
		tmp = parameterMap.get("屏幕尺寸");
		if (tmp != null) {
			sb.append(tmp);
		}
		sb.append("\t");

		// 屏幕分辨率+","+手机操控方式
		tmp = parameterMap.get("屏幕分辨率");
		if (tmp != null) {
			sb.append(tmp).append(",");
		}
		tmp = parameterMap.get("手机操控方式");
		if (tmp != null) {
			sb.append(tmp);
		}
		lastIndexOfComma = sb.lastIndexOf(",");
		if (lastIndexOfComma == sb.length() - 1) {
			sb.deleteCharAt(lastIndexOfComma);
		}
		sb.append("\t");

		// 系统版本
		tmp = parameterMap.get("系统版本");
		if (tmp != null) {
			sb.append(tmp);
		}
		sb.append("\t");

		// 标配电池容量+","+电池更换
		tmp = parameterMap.get("标配电池容量");
		if (tmp != null) {
			sb.append(tmp).append(",");
		}
		tmp = parameterMap.get("电池更换");
		if (tmp != null) {
			sb.append(tmp).append("电池更换");
		}
		lastIndexOfComma = sb.lastIndexOf(",");
		if (lastIndexOfComma == sb.length() - 1) {
			sb.deleteCharAt(lastIndexOfComma);
		}
		sb.append("\t");

		// CPU型号+","+CPU频率+","+CPU核数
		tmp = parameterMap.get("CPU型号");
		if (tmp != null) {
			sb.append(tmp).append(",");
		}
		tmp = parameterMap.get("CPU频率");
		if (tmp != null) {
			sb.append(tmp).append(",");
		}
		tmp = parameterMap.get("CPU核数");
		if (tmp != null) {
			sb.append(tmp);
		}
		lastIndexOfComma = sb.lastIndexOf(",");
		if (lastIndexOfComma == sb.length() - 1) {
			sb.deleteCharAt(lastIndexOfComma);
		}
		sb.append("\t");

		// 摄像头像素+","+副摄像头像素
		tmp = parameterMap.get("摄像头像素");
		if (tmp != null) {
			sb.append(tmp).append(",");
		}
		tmp = parameterMap.get("副摄像头像素");
		if (tmp != null) {
			sb.append(tmp);
		}
		lastIndexOfComma = sb.lastIndexOf(",");
		if (lastIndexOfComma == sb.length() - 1) {
			sb.deleteCharAt(lastIndexOfComma);
		}
		sb.append("\t");
		// 价格
		tmp = parameterMap.get("价格");
		if (tmp != null) {
			sb.append(tmp);
		}

		phoneRecord = sb.toString();
		return phoneRecord;
	}

	/**
	 * 通过http连接获取手机参数
	 * */
	private Map<String, String> getPhoneParameter(String link) {
		Map<String, String> parameterMap = new HashMap<String, String>();
		Source source = null;
		try {
			// 模拟浏览器打开一个目标网址
			HtmlPage page = webClient.getPage(link);
			String pageXml = page.asXml();
			source = new Source(pageXml);
			// 获取参数表格
			List<Element> parameterTableList = source.getAllElements("id",
					"itemParameter", true);
			if(parameterTableList.size()>0){
				Element parameter = parameterTableList.get(0);
				// 获取表格的列
				List<Element> elementList = parameter
						.getAllElements(HTMLElementName.TR);
				// 获取表格单元格数据
				for (int i = 0; i < elementList.size(); i++) {
					List<Element> tdList = elementList.get(i).getAllElements(
							HTMLElementName.TD);
					if (tdList.size() < 2) {
						continue;
					}
					// 获取参数名称
					String parameterName = tdList.get(0)
							.getAllElements(HTMLElementName.SPAN).get(0)
							.getTextExtractor().toString();
					// 获取参数值
					String parameterValue = tdList.get(1).getTextExtractor()
							.toString();
					// 將参数名称和参数值放入参数map
					parameterMap.put(parameterName, parameterValue);
				}
			}
			// 获取价格参数
			String price = "";
			// 获取价格元素
			HtmlElement htmlElment = page.getElementById("promotionPrice");
			if(htmlElment!=null){
				String content = htmlElment.asText();
				// 去除价格前缀
				if (content != null) {
					price = content.replace("¥", "");
				}
				parameterMap.put("价格", price);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e.toString(), e);
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.toString(), e);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString(), e);
		}
		return parameterMap;
	}

//	private Map<String, String> getPhoneParameter_old(String link) {
//		Map<String, String> parameterMap = new HashMap<String, String>();
//		Source source = null;
//		try {
//			source = new Source(new URL(link));
//			// 获取参数表格
//			List<Element> parameterTableList = source.getAllElements("id",
//					"itemParameter", true);
//			Element parameter = parameterTableList.get(0);
//			// 获取表格的列
//			List<Element> elementList = parameter
//					.getAllElements(HTMLElementName.TR);
//			// 获取表格单元格数据
//			for (int i = 0; i < elementList.size(); i++) {
//				List<Element> tdList = elementList.get(i).getAllElements(
//						HTMLElementName.TD);
//				if (tdList.size() < 2) {
//					continue;
//				}
//				// 获取参数名称
//				String parameterName = tdList.get(0)
//						.getAllElements(HTMLElementName.SPAN).get(0)
//						.getTextExtractor().toString();
//				// 获取参数值
//				String parameterValue = tdList.get(1).getTextExtractor()
//						.toString();
//				// 將参数名称和参数值放入参数map
//				parameterMap.put(parameterName, parameterValue);
//			}
//			// 获取价格参数
//			String price = getPrice(link);
//			int getPriceCount = 0;
//			// 连接失败则重跑3次
//			while (price == null) {
//				price = getPrice(link);
//				getPriceCount++;
//				if (getPriceCount > 3) {
//					break;
//				}
//			}
//			parameterMap.put("价格", price);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return parameterMap;
//	}
//
//	private String getPrice(String link) throws FailingHttpStatusCodeException,
//			MalformedURLException, IOException {
//		System.out.println("link:" + link);
//		String price = null;
//		// 模拟一个浏览器
//		WebClient webClient = new WebClient();
//		// 设置webClient的相关参数
//		webClient.setJavaScriptEnabled(true);
//		webClient.setCssEnabled(false);
//		webClient.setActiveXNative(false);
//		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//		// webClient.setTimeout(100000);
//		webClient.setThrowExceptionOnScriptError(false);
//		webClient.setThrowExceptionOnFailingStatusCode(false);
//		// 模拟浏览器打开一个目标网址
//		HtmlPage rootPage = webClient.getPage(link);
//		// 获取价格元素
//		HtmlElement htmlElment = rootPage.getElementById("promotionPrice");
//		String content = htmlElment.asText();
//		// System.out.println("content:"+content);
//		// 去除价格前缀
//		if (content != null) {
//			price = content.replace("¥", "");
//		}
//		// System.out.println("price:"+price);
//		return price;
//	}


	public static void main(String[] args) {
		// 加载log4j配置文件
		String path = System.getProperty("user.dir");
		PropertyConfigurator.configure(path+"/src/main/resources/conf/log4j.properties");
		// http://list.suning.com/0-20006-0-0-0-9018.html
		// http://list.suning.com/0-20006-99-0-0-9018.html		
		FileWriter fw = null;
		BufferedWriter bw = null;
		SNCraweler snCrawler = new SNCraweler();
		List<SNCraweler> threadlist = new ArrayList<SNCraweler>();
		try{
			int pageNum = 1;
			List<String> pageList = new ArrayList<String>(pageNum);
			LinkedHashSet<String> toCrawlHashSetList = new LinkedHashSet<String>();
			LinkedList<String> toCrawlList = new LinkedList<String>();
			// 输出文件设置
			File saveFile = new File("d:/Work/ETL/网络爬虫/suning_phone1.txt");
			if (saveFile.exists()) {
				saveFile.delete();
			}
			fw = new FileWriter(saveFile);
			bw = new BufferedWriter(fw);
			// 获取各个页面的链接
			for (int i = 0; i < pageNum; i++) {
				String page = "http://list.suning.com/0-20006-" + i
						+ "-0-0-9018.html";
				pageList.add(page);
				SNCraweler.log.info("page:" + page);
			}
	
			// 获取各个有效商品链接存入toCrawlList
			for (String page : pageList) {
				String pageContents = snCrawler.downloadPage(page);
				// System.out.println("pageContents:"+pageContents);
				if (pageContents != null && pageContents.length() > 0) {
					// 从页面中获取有效的链接
					ArrayList<String> links = snCrawler.retrieveLinks(pageContents);
					// 将页面中的链接加入toCrawlHashSetList去重
					toCrawlHashSetList.addAll(links);
				}
			}
			// 把去重后的HashSet加入到List中
			for(String link : toCrawlHashSetList){
				toCrawlList.add(link);
			}			
			SNCraweler.log.info("toCrawlList.size():"+toCrawlList.size());
			
//			int size = 3721;
			int size = toCrawlList.size();
			if(size>5)size=5;
			int x = 1;
			int y = size/x;
			int last = 0;
			LinkedList<String> tmplist = new LinkedList<String>();
			for(int i=0;i<size;i++){
				tmplist.add(toCrawlList.get(i));
//				tmplist.add("1");
				if(i%y==0 && i>0){
					SNCraweler.log.info("i:"+i);
					SNCraweler.log.info("tmplist.size():"+tmplist.size());
					// 加入线程list
					threadlist.add(new SNCraweler(bw, tmplist));
					// 下一个线程所需要下载的列表
					tmplist = new LinkedList<String>();					
				}
				if(i==(size-1) && i%y==0){
					last = 1;
				}
			}			
			if(last==0){
				SNCraweler.log.info("last:"+tmplist.size());
				// 加入剩余的列表到线程list
				threadlist.add(new SNCraweler(bw, tmplist));
			}
			// 启动所有线程
			for(int j=0;j<threadlist.size();j++){
				threadlist.get(j).start();
			}
			// 等待所有线程都结束
			Iterator<SNCraweler> it = threadlist.iterator();
			while(it.hasNext()){
				SNCraweler sc = (SNCraweler)it.next();
				if(!sc.isAlive()){
					SNCraweler.log.info("threadlist remove:"+sc+" ## threadlist.size():"+threadlist.size());
					it.remove();
				}
				if(!it.hasNext()){
					it = threadlist.iterator();
				}
				Thread.sleep(500);
			}		
			bw.close();
			fw.close();
			SNCraweler.log.info("end");	
		}catch(Exception e){
			e.printStackTrace();
			SNCraweler.log.error(e.toString(), e);
		}finally{
			try{
				if(bw!=null)
					bw.close();
				if(fw!=null)
					fw.close();
			}catch(Exception e){
				e.printStackTrace();
				SNCraweler.log.error(e.toString(), e);
			}
		}
	}
}
