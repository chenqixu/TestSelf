package com.bussiness.bi.bigdata.searchcrawler.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @descript 苏宁易购手机网络爬虫主类
 * @author 陈棋旭
 * */
public class SNCraweler5 {
	public static Logger log = Logger.getLogger(SNCraweler5.class);
	
	/**
	 * 输入url,返回静态页面流
	 * */
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
	 * 解析页面并找出手机链接
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
	 * 主函数
	 * */
	public static void main(String[] args) {
		// 加载log4j配置文件
		String path = System.getProperty("user.dir");
		PropertyConfigurator.configure(path+"/src/main/resources/conf/log4j.properties");
		// 写入文件
		FileWriter fw = null;
		BufferedWriter bw = null;
		SNCraweler5 snCrawler = new SNCraweler5();
		// 队列
		Queue<String> linkQueue = new ConcurrentLinkedQueue<String>();
		// 线程list
		List<SNCraweler5Thread> thread5list = new ArrayList<SNCraweler5Thread>();
		try{
			// 页面数量
			int pageNum = 100;
			List<String> pageList = new ArrayList<String>(pageNum);
			LinkedHashSet<String> toCrawlHashSetList = new LinkedHashSet<String>();
			LinkedList<String> toCrawlList = new LinkedList<String>();
			// 输出文件设置
			File saveFile = new File("E:/BaiduYunDownload/suning_phone1.txt");
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
				SNCraweler5.log.info("page:" + page);
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
			SNCraweler5.log.info("###手机链接总数:"+toCrawlList.size());
			//获取商品信息保存到文件
			for(String link:toCrawlList){
				linkQueue.add(link);
			}
			toCrawlList.clear();			
			// 线程数量
			int threadNum = 3;
			for(int i=0;i<threadNum;i++){
				// 加入线程list
				thread5list.add(new SNCraweler5Thread(linkQueue, bw));
			}
			// 启动所有线程
			for(int j=0;j<thread5list.size();j++){
				thread5list.get(j).start();
			}
			// 等待所有线程都结束
			Iterator<SNCraweler5Thread> it = thread5list.iterator();
			while(it.hasNext()){
				SNCraweler5Thread sc = (SNCraweler5Thread)it.next();
				if(!sc.isAlive()){
					SNCraweler5Thread.log.info("thread5list remove:"+sc+" ## thread5list.size():"+thread5list.size());
					it.remove();
				}
				if(!it.hasNext()){
					it = thread5list.iterator();
				}
				Thread.sleep(500);
			}
			bw.close();
			fw.close();
			SNCraweler5.log.info("end");	
		}catch(Exception e){
			e.printStackTrace();
			SNCraweler5.log.error(e.toString(), e);
		}finally{
			try{
				if(bw!=null)
					bw.close();
				if(fw!=null)
					fw.close();
			}catch(Exception e){
				e.printStackTrace();
				SNCraweler5.log.error(e.toString(), e);
			}
		}
	}
}
