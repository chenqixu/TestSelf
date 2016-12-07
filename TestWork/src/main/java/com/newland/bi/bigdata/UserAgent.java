package com.newland.bi.bigdata;

public class UserAgent {
	public static void main(String[] args) throws Exception {
		String ua = "\347\275\221\346\230\223\344\272\221\351\237\263\344\271\220 3.7.3 rv:636 (iPhone; iOS 10.0.2; zh_CN)";
//		ua = "\310\316\316\361\307\353\307";
//		System.out.println(new String(ua.getBytes("ISO-8859-1")));
//		System.out.println(new String(ua.getBytes("GBK")));
//		System.out.println(new String(ua.getBytes("UTF-8")));
//		System.out.println(new String(ua.getBytes("UTF-8"), "GBK"));
		System.out.println(new String(ua.getBytes("ISO-8859-1"), "UTF-8"));
	}
}
