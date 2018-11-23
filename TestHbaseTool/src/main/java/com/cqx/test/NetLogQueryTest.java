package com.cqx.test;

import java.util.List;

import com.cqx.util.CommonUtils;

public class NetLogQueryTest {
	public static void main(String[] args) {
		StringBuffer a = new StringBuffer();
		a.append("13876955257,");
		a.append("三星,");
		a.append("三星 SM-G9208,");
		a.append("358182061697367,");
		a.append("CMNET,");
		a.append("4G,");
		a.append("20160310081918,");
		a.append("20160310081918,");
		a.append("2690,");
		a.append("117562,");
		a.append("huiyi.ecloud.10086.cn/msweb/api/docpicdl,,,,");
		a.append(",");
		a.append("中国移动手机营业厅,");
		
		//切割返回的这一列，流量取整
		List<String> entityList = CommonUtils.splitStringByComma(a.toString());
		//14个字段
		if(entityList.size() < 14){
			System.out.println("less then 14");
			System.exit(-1);
		}
		if(entityList.size() > 14){
			System.out.println("more then 14");
			//做一个处理，把index.10以后,index.length-3之前的都归到10
			entityList = CommonUtils.mvUrl(entityList);
			System.out.println(entityList.size());
			System.out.println(entityList);
			System.exit(-1);
		}
		System.out.println("ok");
	}
}
