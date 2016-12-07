package com.newland.bi.bigdata.searchcrawler.main;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newland.bi.bigdata.searchcrawler.bean.PreBuy;
import com.newland.bi.bigdata.searchcrawler.bean.Sn;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.sf.json.JSONObject;

public class StockTest {

	public String getHtml(String url) {
		String result = "";
		// String url =
		// "http://hyj.suning.com/newFourPageService/treatyInfo_000000000123129015_150_9018_10124_150_1000018.hs";//"http://www.baidu.com";
		// String url = "http://product.suning.com/123129015.html";
		List list = new ArrayList();
		try {
			URL u = new URL(url);
			byte[] b = new byte[256];
			InputStream in = null;
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			// while (true)
			{
				try {
					in = u.openStream();
					int i;
					while ((i = in.read(b)) != -1) {
						bo.write(b, 0, i);
					}
					result = bo.toString("UTF-8");
//					 System.out.println(result);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return result;
	}

	public String getConf(String link) {
		Map<String, String> parameterMap = new HashMap<String, String>();
		Source source = null;
		String price = null;
		try {
			source = new Source(new URL(link));
			String s = source.getParseText().toString();
//			System.out.println(s);
			int x1 = s.indexOf("var sn = sn || {");
			int x2 = s.indexOf("};");
			String result = s.substring(x1 + 15, x2 + 1).replace(
					"+document.location.hostname", "");
			JSONObject j1 = JSONObject.fromObject(result);
			String partNumber = j1.get("partNumber").toString();
			System.out.println("partNumber:" + partNumber);
			String vendorCode = j1.get("vendorCode").toString();
			System.out.println("vendorCode:" + vendorCode);
			String shopType = j1.get("shopType").toString();
			System.out.println("shopType:" + shopType);			
//			String s1 = getHtml("http://hyj.suning.com/newFourPageService/treatyInfo_"
//							+ partNumber + "_150_9018_10124_150_1000018.hs");
//			String s1 = getHtml("http://hyj.suning.com/fourPageService/sellPointInfo_"
//					+ partNumber + "_9018.hs");		
//			String result1 = s1.substring(12, s1.length() - 1);
			String a= vendorCode;
			if(vendorCode.equals("-1")){
				a="";
			}else{
				if(shopType.equals("0")){
					a="0000000000";
				}
			}			
			String s1 = getHtml("http://www.suning.com/webapp/wcs/stores/ItemPrice/"
					+ partNumber + "_" + a+ "_9018_10124_1.html");	
			System.out.println(s1);
			int y1 = s1.indexOf("promotionPrice\":\"");
			String ss1 = s1.substring(y1);
			int y2 = ss1.indexOf("\",");
			price = ss1.substring(17, y2);
			System.out.println("price:"+price);
			
			// 获取参数表格
			List<Element> parameterTableList = source.getAllElements("id",
					"itemParameter", true);
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
				System.out.println("link:"+link+" [get itemParameter error]");
				return "";
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
//		tmp = parameterMap.get("价格");
//		if (tmp != null) {
			sb.append(price);
//		}

		phoneRecord = sb.toString();
		return phoneRecord;
	}

	public void getGPCode1(String gpList) {
		String url = "http://hq.sinajs.cn/list=";
		// http://hq.sinajs.cn/list=sh600477,sz000002,sz000409
		// String gpstr = "";
		// for(int ii=0;ii<gpList.length;ii++){
		// gpstr = gpstr + gpList[ii] + ",";
		// }
		// gpstr = gpstr.substring(0,gpstr.length()-1);
		url = url + gpList;

		List list = new ArrayList();
		try {
			URL u = new URL(url);
			byte[] b = new byte[256];
			InputStream in = null;
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			// while (true)
			{
				try {
					in = u.openStream();
					int i;
					while ((i = in.read(b)) != -1) {
						bo.write(b, 0, i);
					}
					String result = bo.toString();
					// System.out.println(result);
					String[] stocks = result.split(";");
					for (String stock : stocks) {
						String[] datas = stock.split(",");
						if (datas.length == 1)
							break;

						// System.out.println(datas.length);
						// ��ݶ����Լ���Ӧ���
						System.out.println("------------------");
						System.out.println(datas[0] + "," + datas[30]
								+ datas[31]);
						System.out.println("open:" + datas[1]);
						System.out.println("yesterday" + datas[2]);
						System.out.println("now:" + datas[3]);
						System.out.println("high:" + datas[4]);
						System.out.println("low:" + datas[5]);
						System.out.println("total:" + datas[8]);
						System.out.println("money:" + datas[9]);
						System.out.println("sell:");
						System.out.println(datas[29] + ":" + datas[28]);
						System.out.println(datas[27] + ":" + datas[26]);
						System.out.println(datas[25] + ":" + datas[24]);
						System.out.println(datas[23] + ":" + datas[22]);
						System.out.println(datas[21] + ":" + datas[20]);

						System.out.println("buy:");
						System.out.println(datas[11] + ":" + datas[10]);
						System.out.println(datas[13] + ":" + datas[12]);
						System.out.println(datas[15] + ":" + datas[14]);
						System.out.println(datas[17] + ":" + datas[16]);
						System.out.println(datas[19] + ":" + datas[18]);
					}
					// bo.reset();
					// break;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public void go_to1() {
		try {
			InetAddress ad = InetAddress.getByName("192.168.0.1");
			boolean state = ad.isReachable(5000);
			if (state)
				System.out.println(ad.getHostAddress());
			else
				System.err.println("");
		} catch (UnknownHostException e) {
			System.err.println(e.toString());
		} catch (IOException e1) {
			System.err.println(e1.toString());
		}
	}

	public void go_to2() {
		URL url = null;
		try {
			url = new URL("http://afoi.eao.123oa/");
			InputStream in = url.openStream();
			in.close();
		} catch (IOException e) {
			System.err.println(url.toString());
		}
	}

	public void go_to3() {
		String ip = "192.168.1.1";
		ip = "123.adf.dsaf";
		try {
			Process p = Runtime.getRuntime().exec("cmd /c ping -n 1 " + ip);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String temp = null;
			StringBuffer strBuffer = new StringBuffer();
			while ((temp = (in.readLine())) != null) {
				strBuffer.append(temp);
			}
			System.out.println(strBuffer);
			if (strBuffer.toString().matches(".*\\(\\d?\\d% loss\\).*")) {
				System.out.println("");
			} else {
				System.out.println("");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getPSSPrice(String c){
		Sn sn = new Sn();
		sn.setVendor("0010036797");
		sn.setVendorCode("-1");
		sn.setIsLoadPricePrice("false");
		sn.setYushouDomain("http://yushou.suning.com");
		sn.setItemDomain("http://product.suning.com");
		PreBuy preBuy = new PreBuy();
//		preBuy.setActionID("undefined");
		
//		String a= "000000000123129094";//typeof sn.passPartNumber!="undefined"?sn.curSubPartNumber:sn.partNumber;
//		String b = sn.getItemDomain()+"/pds-web/ajax/accessory_"+
//				sn.getVendorCode()+"_"+c+"_"+a+"_9018.html";
		
//		String a= sn.getVendorCode();
//		if(!a.equals("undefined")&& a.length()==10&&a.substring(0,3)=="003"){
//			a="";
//		}
//		String b=sn.getItemDomain()+"/pds-web/ajax/scrice_"+c+"_"+a+".html";
//		System.out.println(b);
		
		
		String a =sn.getVendorCode()==""?"0000000000":sn.getVendorCode();
		String b=sn.getYushouDomain()+"/jsonp/appoint/getGoodsPrice-"+c+"-"+a+"-"
		+preBuy.getActionID() +"-prcessPSSPrice.htm";
		
		System.out.println(b);
		String str = getHtml(b);
		System.out.println(str);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// st.getGPCode1("sh000001");
		// st.go_to3();

		StockTest st = new StockTest();
		String url = "http://product.suning.com/123129094-1-2.html";
//		String conf = st.getHtml(url);
//		System.out.println(conf);
//		String url = "http://product.suning.com/126053707.html";		
		String conf = st.getConf(url);
//		String conf = st.getHtml("http://product.suning.com/126053707.html");		
		System.out.println(conf);
//		System.out.println(st.getHtml("http://www.suning.com/webapp/wcs/stores/ItemPrice/000000000123129017_0000000000_9018_10124_1.html"));
//		st.getPSSPrice("000000000123129094");
		
	}
}
