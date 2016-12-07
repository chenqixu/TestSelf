package com.newland.bi.bigdata.net;

import java.io.UnsupportedEncodingException;

public class NetTest1 {
	public static void main(String[] args) throws Exception {
		String surl = "m.baidu.com/rec?platform=wise&ms=1&rset=rcmd&word=%E6%B5%B7%E5%8F%A3+_+%E5%9F%8E%E5%B8%82%E5%AF%BC%E8%88%AA_%E9%AB%98%E6%A0%A1%E4%BA%BA%E6%89%8D%E7%BD%91%7C%E9%AB%98%E6%A0%A1%E6%95%99%E5%B8%88%E6%8B%9B%E8%81%98%7C%E9%AB%98%E6%A0%A1%E8%BE%85%E5%AF%BC%E5%91%98%E6%8B%9B%E8%81%98%7C%E8%81%8C%E4%B8%9A%E6%8A%80%E6%9C%AF%E5%AD%A6%E9%99%A2%E6%8B%9B%E8%81%98%7C&qid=14355453935652281976&rq=%E6%B5%B7%E5%8F%A3+_+%E5%9F%8E%E5%B8%82%E5%AF%BC%E8%88%AA_%E9%AB%98%E6%A0%A1%E4%BA%BA%E6%89%8D%E7%BD%91%7C%E9%AB%98%E6%A0%A1%E6";
		System.out.println(java.net.URLDecoder.decode(surl ,"UTF-8"));
	}
}
