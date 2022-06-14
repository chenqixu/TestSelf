package com.bussiness.bi.bigdata.searchcrawler.main;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.sf.json.JSONObject;

/**
 * @descript 苏宁易购手机网络爬虫爬取线程
 * @author 陈棋旭
 * */
public class SNCraweler5Thread extends Thread {
	public static Logger log = Logger.getLogger(SNCraweler5Thread.class);
	
	private String link = null;
	private Queue<String> linkQueue = null;
	private BufferedWriter out = null;
	private String threadName = null;
	
	public SNCraweler5Thread(Queue<String> linkQueue, BufferedWriter bw){
		this.linkQueue = linkQueue;
		this.out = bw;
		this.threadName = Thread.currentThread().getName();
	}
	
	/**
	 * 输入url，返回请求结果
	 * */
	public String getHtml(String url) {
		String result = "";
		try {
			URL u = new URL(url);
			byte[] b = new byte[256];
			InputStream in = null;
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			try {
				in = u.openStream();
				int i;
				while ((i = in.read(b)) != -1) {
					bo.write(b, 0, i);
				}
				result = bo.toString("UTF-8");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
		return result;
	}

	/**
	 * 获取手机价格、配置参数
	 * */
	public String getConf(String link) {
		Map<String, String> parameterMap = new HashMap<String, String>();
		Source source = null;
		String price = null;
		try {
			source = new Source(new URL(link));
			String s = source.getParseText().toString();
			log.debug("##["+threadName+"] link:"+link+" [response content:]:"+s);
			// 截取页面sn参数
			int x1 = s.indexOf("var sn = sn || {");
			int x2 = s.indexOf("};");
			// 移除变量document.location.hostname，否则json无法正常解析
			String result = s.substring(x1 + 15, x2 + 1).replace(
					"+document.location.hostname", "");
			// 使用json解析sn参数，获得partNumber，vendorCode，shopType
			JSONObject j1 = JSONObject.fromObject(result);
			String partNumber = j1.get("partNumber").toString();
			String vendorCode = j1.get("vendorCode").toString();
			String shopType = j1.get("shopType").toString();
			log.info("##["+threadName+"] link:"+link+" partNumber:" + partNumber+" vendorCode:" + vendorCode+" shopType:" + shopType);			
			// 模拟脚本方法，实现价格查询
			// 脚本路径
			// http://res.suning.cn/??/project/pdsWeb/js/iFourth-min.js,/project/pdsWeb/js/itemComDataLoad-min.js,/project/pdsWeb/js/item-min.js,/project/ip-web/SFE.city.js?v=2015092300
			// 脚本方法
//			function getMobileItemSaleStatus(c,f,e){var a=sn.vendorCode;
//			if(sn.vendorCode=="-1"){a=""
//			}else{if(sn.shopType=="0"){a="0000000000"
//			}}var b="http://"+sn.domain+"/webapp/wcs/stores/ItemPrice/"+c+"_"+a+"_"+sn.cityId+"_"+sn.districtId+"_2.html";
//			$.ajax({url:b,type:"get",cache:true,dataType:"jsonp",jsonp:"callback",jsonpCallback:f,success:function(){},error:function(){e()
//			}})
//			}			
			String a= vendorCode;
			if(vendorCode.equals("-1")){
				a="";
			}else{
				if(shopType.equals("0")){
					a="0000000000";
				}
			}
			// 查询手机价格api
			String s1 = getHtml("http://www.suning.com/webapp/wcs/stores/ItemPrice/"
					+ partNumber + "_" + a+ "_9018_10124_1.html");	
			log.info("##["+threadName+"]"+s1);
			int y1 = s1.indexOf("promotionPrice\":\"");
			String ss1 = s1.substring(y1);
			int y2 = ss1.indexOf("\",");
			price = ss1.substring(17, y2);
			log.info("##["+threadName+"] price:"+price);
			
			// 获取参数表格
			List<Element> parameterTableList = source.getAllElements("id",
					"itemParameter", true);
			// 有的手机可能没有参数，所以需要异常处理
			if(parameterTableList!=null && parameterTableList.size()>0){
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
			}else{
				log.info("##["+threadName+"] link:"+link+" [get itemParameter error]");
				return "";
			}
		} catch (MalformedURLException e) {
			log.error(e.toString(), e);
		} catch (IOException e) {
			log.error(e.toString(), e);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

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
		sb.append(price);

		phoneRecord = sb.toString();
		return phoneRecord;
	}
	
	public void run(){
		try{
			while((link=linkQueue.poll())!=null){
				// 通过连接获得手机价格和参数
				String conf = getConf(link);
				log.info("##["+threadName+"] conf:"+conf);
				// 写入文本
				synchronized (out) {
					out.write(conf);
					out.newLine();
					out.flush();
				}
				Thread.sleep(1000);
			}
		}catch(Exception e){
			log.error("##["+threadName+"]"+e.toString(), e);
		}
	}
}
