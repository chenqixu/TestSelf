package com.cqx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CommonUtils {
	
	/**
	 * �ж��ַ����Ƿ�Ϊ��
	 * */
	public static boolean isEmpty(String dest){
		return "".equals(dest)||dest==null;
	}

	/**
	 * �ָ��ַ���
	 * */
	public static List<String> splitStringByComma(String valueString) {

		List<String> list = new ArrayList<String>();
		StringBuffer tempSB = new StringBuffer();
		try {
			StringTokenizer st = new StringTokenizer(valueString, ",");
			while (st.hasMoreTokens()) {
				// ���жϵ�ǰtoken֮��֪����,����Ƕ��ţ������list��
				if (valueString.charAt(tempSB.length()) == ',') {
					list.add("");
					tempSB.append(",");
					continue;
				}
				String temp = st.nextToken();
				if (temp.startsWith("'")) {
					//newһ���¶����ֹ�ڴ����
					temp = new String(temp.substring(1));
					StringBuffer sb = new StringBuffer();

					int index = temp.indexOf("'");
					if (-1 == index) {
						// ˵�����ֶ��к���,��Դ�ַ����в��ҵ�n������֮��
						int end = valueString.indexOf("'", tempSB.length() + 1);
						sb.append(new String(valueString.substring(tempSB.length(), end)));
						// ȥ��ǰ��ĵ�����
						list.add(new String(valueString.substring(tempSB.length() + 1, end)));
						tempSB.append(new String(valueString.substring(tempSB.length(), end + 1)));
						// �������ֶ������һ���ֶΣ������
						if (end + 2 > valueString.length()) {
							break;
						}
						tempSB.append(",");
						st = new StringTokenizer(new String(valueString.substring(end + 2)), ",");
						continue;
						// ������һ�������ŵ�λ�ã�����¸��ַ�����Ҳ�����ڣ���һֱ�ҵ����ڵ��ַ���λ��
					} else {
						// index��Ϊ-1��˵�������ַ����а�����һ�������ţ����õ��������������ַ�����û�ж���
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
	 * ��һ��������index.10�Ժ�,index.length-3֮ǰ�Ķ��鵽10
	 * ר�Ŵ���url�д��а�Ƕ��ŵ����
	 * */
	public static List<String> mvUrl(List<String> entityList){
		List<String> result = new ArrayList<String>();
		// ��һ��������index.10�Ժ�,index.length-3֮ǰ�Ķ��鵽10
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
