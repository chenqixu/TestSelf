package com.bussiness.bi.bigdata.txt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetSecondDomain {
	public String evaluate(String url, String host) {
		String result = host; // 匹配异常为原始字符串
		try {
			if (url!=null && url.trim().length()>0){
				// 如果没有http前缀，hive的自带函数是找不到host的，这里要手工识别
				if (host==null || host.trim().equalsIgnoreCase("NULL")) {
					// 匹配host
					Pattern p = Pattern.compile("(?<=//|)([\\w\\-\u4E00-\u9FA5]+\\.)+[\\w\u4E00-\u9FA5]+");
					Matcher m = p.matcher(url);
					// 有host
					if (m.find()) {
						// 获取host
						host = m.group();
						result = host;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
