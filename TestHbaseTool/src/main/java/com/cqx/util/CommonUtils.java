package com.cqx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CommonUtils {
	
	/**
	 * 判断字符串是否为空
	 * */
	public static boolean isEmpty(String dest){
		return "".equals(dest)||dest==null;
	}

	/**
	 * 分割字符串
	 * */
	public static List<String> splitStringByComma(String valueString) {

		List<String> list = new ArrayList<String>();
		StringBuffer tempSB = new StringBuffer();
		try {
			StringTokenizer st = new StringTokenizer(valueString, ",");
			while (st.hasMoreTokens()) {
				// 先判断当前token之后知否是,如果是逗号，则加入list中
				if (valueString.charAt(tempSB.length()) == ',') {
					list.add("");
					tempSB.append(",");
					continue;
				}
				String temp = st.nextToken();
				if (temp.startsWith("'")) {
					//new一个新对象防止内存溢出
					temp = new String(temp.substring(1));
					StringBuffer sb = new StringBuffer();

					int index = temp.indexOf("'");
					if (-1 == index) {
						// 说明该字段中含有,从源字符串中查找第n个逗号之后
						int end = valueString.indexOf("'", tempSB.length() + 1);
						sb.append(new String(valueString.substring(tempSB.length(), end)));
						// 去掉前后的单引号
						list.add(new String(valueString.substring(tempSB.length() + 1, end)));
						tempSB.append(new String(valueString.substring(tempSB.length(), end + 1)));
						// 如果这个字段是最后一个字段，则结束
						if (end + 2 > valueString.length()) {
							break;
						}
						tempSB.append(",");
						st = new StringTokenizer(new String(valueString.substring(end + 2)), ",");
						continue;
						// 查找下一个单引号的位置，如果下个字符串中也不存在，则一直找到存在的字符串位置
					} else {
						// index不为-1则说明，该字符串中包含下一个单引号，即该单引号括起来的字符串中没有逗号
						list.add(new String(temp.substring(0, temp.length() - 1)));
						tempSB.append(temp).append("',");
						continue;
					}
				}
				list.add(temp);
				tempSB.append(temp).append(",");
			}
		} catch (Exception ex) {
			return null;
		}
		
		if(valueString.endsWith(",")){
			int commaIndex = valueString.lastIndexOf(",");
			while(commaIndex > 1){
				if(valueString.charAt(commaIndex--) == ','){
					list.add("");
				}else{
					break;
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 做一个处理，把index.10以后,index.length-3之前的都归到10
	 * 专门处理url中带有半角逗号的情况
	 * */
	public static List<String> mvUrl(List<String> entityList){
		List<String> result = new ArrayList<String>();
		// 做一个处理，把index.10以后,index.length-3之前的都归到10
		if(entityList.size() > 14){
			int all_size = entityList.size();
			int url_start_size = 10;
			int url_end_size = all_size-1-3;
			String _url = "";
			for(int i=0;i<entityList.size();i++){
				if(i<=url_start_size-1){
					result.add(entityList.get(i));
				}else if(i<=url_end_size){
					_url += entityList.get(i);
				}else{
					if(_url.length()>0){
						result.add(_url);
						_url = "";
					}
					result.add(entityList.get(i));
				}
			}
		}
		return result;
	}
}
