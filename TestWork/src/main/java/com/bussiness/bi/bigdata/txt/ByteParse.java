package com.bussiness.bi.bigdata.txt;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

public class ByteParse {
	// 圆点字符串
	public static final String DOT_STRING = ".";
	// 逗号
	public static final String COMMA_STRING = ",";
	// 换行字符
	public static final String NEXT_LINE_STRING = "\n";
	// 切割字符串
	private static final String split_str = "|";
	private static final String evl_split_str = "\\+\\|\\+"; //"\\+,\\+"
	// 清洗数据规则
	private String FIELDS_RULE = null;
	
	public void setFIELDS_RULE(String fIELDS_RULE) {
		FIELDS_RULE = fIELDS_RULE;
	}

	/**
	 * 
	 * @description: 静态公用方法，从字节流中读入指定长度的字节，返回字节
	 * @author:xixg
	 * @date:2014-11-20
	 * @param dataInputStream
	 *            输入的字节流
	 * @param readByteLength
	 *            读入指定的字节长度
	 * @return byte[] 返回字节
	 */
	public static byte[] readBytesByLength(DataInputStream dataInputStream,
			int readByteLength) {
		byte[] byteData = new byte[readByteLength];
		try {
			// 从字节流中读入指定长度的字节，从0开始读入
			dataInputStream.read(byteData, 0, readByteLength);
		} catch (Exception e) {
			System.out.println("%%%%%从字节流中读入指定的字节出错！！");
		}
		return byteData;
	}

	/**
	 * 
	 * @description: 静态公用方法，将字节转为十六进制字符串
	 * @author:xixg
	 * @date:2014-11-20
	 * @param byteData
	 *            输入的字节流数据
	 * @return String 十六进制字符串
	 */
	public static String bytesToHexStr(byte[] byteData) {
		String hexStrResult = "";
		try {
			// 循环取出byte数组里的值，然后转为十六进制
			for (int i = 0; i < byteData.length; i++) {
				// 字节转为int
				int intValue = byteData[i] & 0xFF;
				// int转为十六进制
				String hexStr = Integer.toHexString(intValue);
				if (hexStr.length() == 1) {
					// 如果十六进制长度等于1，则前面补byte 0
					hexStr = '0' + hexStr;
				}
				// 多个字节迭代转为十六进制字符串
				hexStrResult += hexStr.toUpperCase();
			}
		} catch (Exception e) {
			System.out.println("%%%%%将字节转为十六进制字符串时出错！！");
		}
		return hexStrResult;
	}

	/**
	 * 
	 * @description: 静态公用方法，将字节转为十六进制字符串
	 * @author:xixg
	 * @date:2014-11-20
	 * @param byteData
	 *            输入的字节流数据
	 * @return String 十六进制字符串
	 */
	public static String bytesToHexStr1(byte[] byteData, int num) {
		String hexStrResult = "";
		try {
			// 循环取出byte数组里的值，然后转为十六进制
			for (int i = 0; i < num; i++) {
				// 字节转为int
				int intValue = byteData[i] & 0xFF;
				// int转为十六进制
				String hexStr = Integer.toHexString(intValue);
				if (hexStr.length() == 1) {
					// 如果十六进制长度等于1，则前面补byte 0
					hexStr = '0' + hexStr;
				}
				// 多个字节迭代转为十六进制字符串
				hexStrResult += hexStr.toUpperCase();
			}
		} catch (Exception e) {
			System.out.println("%%%%%将字节转为十六进制字符串时出错！！");
		}
		return hexStrResult;
	}

	/**
	 * 
	 * @description: 静态公用方法，将十六进制字符串转为IP
	 * @author:xixg
	 * @date:2014-11-20
	 * @param byteData
	 *            输入的十六进制字符串
	 * @return String 返回的IP字符串
	 */
	public static String hexStrToIp(String hexStr) {
		String ipStrResult = "";
		StringBuffer sb = new StringBuffer();
		try {
			// 第一个IP字符串
			String ip1 = hexStr.substring(0, 2);
			// 第二个IP字符串
			String ip2 = hexStr.substring(2, 4);
			// 第三个IP字符串
			String ip3 = hexStr.substring(4, 6);
			// 第四个IP字符串
			String ip4 = hexStr.substring(6, 8);
			// 整个IP字符串
			sb.append(Integer.parseInt(ip1, 16)).append(
					DOT_STRING);
			sb.append(Integer.parseInt(ip2, 16)).append(
					DOT_STRING);
			sb.append(Integer.parseInt(ip3, 16)).append(
					DOT_STRING);
			sb.append(Integer.parseInt(ip4, 16));
			ipStrResult = sb.toString();
		} catch (Exception e) {
			System.out.println("%%%%%将十六进制字符串" + hexStr + "转为IP时出错！！");
		}
		return ipStrResult;
	}

	/**
	 * 
	 * @description: 静态公用方法，将十六进制字符串转为IP
	 * @author:xixg
	 * @date:2014-11-20
	 * @param byteData
	 *            输入的十六进制字符串
	 * @return String 返回的IP字符串
	 */
	public static String hexStrToInt(String hexStr) {
		String intStrResult = "";
		try {
			intStrResult = String.valueOf(Integer.parseInt(hexStr, 16));
		} catch (Exception e) {
			System.out.println("%%%%%将十六进制字符串" + hexStr + "转为int时出错！！");
		}
		return intStrResult;
	}

	/**
	 * 
	 * @description: 静态公用方法，将十六进制字符串转为时间串
	 * @author:xixg
	 * @date:2014-11-20
	 * @param byteData
	 *            输入的十六进制字符串
	 * @return String 返回的时间串
	 */
	public static String hexStrToTime(String hexStr) {
		String reStrTime = "";
		try {
			int intVal = Integer.parseInt(hexStr, 16);
			long longVal = Long.valueOf(String.valueOf(intVal) + "000");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 时间格式
			reStrTime = sdf.format(new Date(longVal));
		} catch (Exception e) {
			System.out.println("%%%%%将十六进制字符串" + hexStr + "转为Time时出错！！");
		}
		return reStrTime;
	}
	
	/**
	 * @description 动态解析字符串中的方法，并进行调用，返回结果
	 * @param jexlExp 规则字符串
	 * @param map 参数
	 * @return
	 * */
	public Object invokeMethod(String jexlExp,Map<String,Object> map){
		JexlEngine jexl=new JexlEngine();
		Expression e = jexl.createExpression(jexlExp);
		JexlContext jc = new MapContext();
		for(String key:map.keySet()){
			jc.set(key, map.get(key));
		}
		return e.evaluate(jc);
	}
	
	/**
	 * @description 切割规则，识别规则中的方法，调用执行
	 * @param args 输入参数
	 * @return
	 * */
	public String evaluate(Map<String, DataInputStream> args){
		StringBuffer sb = new StringBuffer("");
		String tmp = "";
		String[] arrtmp = null;
		ByteParse t = new ByteParse();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("t", t);
		Iterator<?> iter = args.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String)entry.getKey();
			Object val = entry.getValue();
			map.put(key, val);
		}
		map.put(split_str, split_str);
		map.put("(", "(");
		map.put(")", ")");
		if(FIELDS_RULE!=null && FIELDS_RULE.trim().length()>0){
			tmp = FIELDS_RULE.replace("\\", "");
			arrtmp = tmp.split(evl_split_str, -1);
			for(String s : arrtmp){
				if(s.indexOf("readBytesByLength")>=0
						|| s.indexOf("bytesToHexStr")>=0
						|| s.indexOf("hexStrToIp")>=0
						|| s.indexOf("hexStrToInt")>=0
						|| s.indexOf("hexStrToTime")>=0
						){
					// 替换成t.
					s = s.replace("readBytesByLength(", "t.readBytesByLength(");
					s = s.replace("bytesToHexStr(", "t.bytesToHexStr(");
					s = s.replace("hexStrToIp(", "t.hexStrToIp(");
					s = s.replace("hexStrToInt(", "t.hexStrToInt(");
					s = s.replace("hexStrToTime(", "t.hexStrToTime(");
					// 执行表达式
					sb.append(invokeMethod(s,map));
					sb.append(split_str);
				}else{
					sb.append(map.get(s));
					sb.append(split_str);
				}
			}
		}
		if(sb.length()>0)sb.deleteCharAt(sb.length()-split_str.length());
		return sb.toString();
	}
	
	/**
     * 将16进制字符串转换为byte[]，仅仅用于验证
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    /**
     * 将byte数组转为输入流，仅仅用于验证
     * */
	public static InputStream StringToInputStream(byte[] param){
		InputStream tInputStringStream = null;
		// 判断字符是否有值
		if (param!=null && param.length>0) {
			try {
				// 将字符串转为输入流
				tInputStringStream = new ByteArrayInputStream(param);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return tInputStringStream;
	}
	
	/**
	 * 合并数组，仅仅用于验证
	 * */
	public static byte[] byteMerger(byte[] bt1, byte[] bt2){ 
	    byte[] bt3 = new byte[bt1.length+bt2.length]; 
	    int i=0;
	    for(byte bt: bt1){
	    	bt3[i]=bt;
	    	i++;
	    }	     
	    for(byte bt: bt2){
	    	bt3[i]=bt;
	    	i++;
	    }
	    return bt3; 
	}
	
	public static void main(String[] args) {
		// 验证，造数据：ip 10.1.2.212 端口 258
//		System.out.println(Integer.toHexString(10));
//		System.out.println(Integer.toHexString(1));
//		System.out.println(Integer.toHexString(2));
//		System.out.println(Integer.toHexString(212));
//		System.out.println(toBytes("0a").length);
//		System.out.println(toBytes("01").length);
//		System.out.println(toBytes("02").length);
//		System.out.println(toBytes("d4").length);
		byte[] b1 = byteMerger(toBytes("0a"), toBytes("01"));
		byte[] b2 = byteMerger(b1, toBytes("02"));
		byte[] b3 = byteMerger(b2, toBytes("d4"));
		byte[] b4 = byteMerger(b3, toBytes("01"));
		byte[] b5 = byteMerger(b4, toBytes("02"));
		DataInputStream dis = new DataInputStream(StringToInputStream(b5));		
		
		ByteParse bp = new ByteParse();
		bp.setFIELDS_RULE("hexStrToIp(bytesToHexStr(readBytesByLength(dataInputStream, 4)))+|+hexStrToInt(bytesToHexStr(readBytesByLength(dataInputStream, 2)))");
//		bp.setFIELDS_RULE("hexStrToIp(bytesToHexStr(readBytesByLength(stream, 4)))"
//+"+hexStrToInt(bytesToHexStr(readBytesByLength(stream, 2)))"
//+"+hexStrToIp(bytesToHexStr(readBytesByLength(stream, 4)))"
//+"+hexStrToInt(bytesToHexStr(readBytesByLength(stream, 2)))"
//+"+hexStrToIp(bytesToHexStr(readBytesByLength(stream, 4)))"
//+"+hexStrToInt(bytesToHexStr(readBytesByLength(stream, 2)))"
//+"+hexStrToTime(bytesToHexStr1(readBytesByLength(stream, 8), 4))"
//+"+hexStrToInt(bytesToHexStr(readBytesByLength(stream, 4)))");
		Map<String, DataInputStream> map = new HashMap<String, DataInputStream>();
		map.put("dataInputStream", dis);
		System.out.println(bp.evaluate(map));
	}
}
