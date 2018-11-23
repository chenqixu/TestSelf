package com.cqx.test;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

import com.cqx.bean.KngReqBean;
import com.cqx.util.CommonUtils;

public class KeepNetLogTest {

	private static Random random = new Random();//用于取模
	
	/**
	 * rowkey region:输入查询条件，取md5前8位，每2位16进制转10进制相加获得region分区号
	 * @author cqx
	 * @param requestBean 查询请求bean
	 * @param flag s表示开始时间, e表示结束时间
	 * @return 返回rowkey,根据查询条件判断,有3种
	 * 1、2G/3G：根据NAT后用户公网IP地址、NAT后源端口、查询时间，查询出移动用户号码；
	 * region分区,ggsip,ggsport,requesttime(yyyymmddhhmiss),sid,msisdn
	 * 2、2G/3G：根据用户访问URL、NAT后用户公网IP地址、查询时间，查询出移动用户号码；
	 * region分区,ggsip,url,requesttime(yyyymmddhhmiss),sid,msisdn
	 * 3、2G/3G：根据用户访问URL、查询时间，查询出移动用户号码；
	 * region分区,url,requesttime(yyyymmddhhmiss),sid,msisdn
	 * */
	private String generateRowKey(KngReqBean requestBean, String flag) {
		//3种查询条件
		//1、2G/3G：根据NAT后用户公网IP地址、NAT后源端口、查询时间，查询出移动用户号码；
		//region分区,ggsip,ggsport,requesttime(yyyymmddhhmiss),sid,msisdn
		//2、2G/3G：根据用户访问URL、NAT后用户公网IP地址、查询时间，查询出移动用户号码；
		//region分区,ggsip,url,requesttime(yyyymmddhhmiss),sid,msisdn
		//3、2G/3G：根据用户访问URL、查询时间，查询出移动用户号码；
		//region分区,url,requesttime(yyyymmddhhmiss),sid,msisdn
		//4、根据号码时间进行查询,备份条件,如果对方要求清单
		String ggsip = requestBean.getGgsip();
		String ggsport = requestBean.getGgsport();
		String url = requestBean.getUrl();
		String telnumber = requestBean.getTelnumber();
		String start_time = requestBean.getStarttime_s();
		String end_time = requestBean.getStarttime_e();
		
		String time = "";
		if(flag.equals("s")){
			time = start_time;
		}else{
			time = end_time;
		}
		
		String rowkey_str = "";
		String region_str = "";
		String str = "";
		//查询条件1
		if(!CommonUtils.isEmpty(ggsip) && !CommonUtils.isEmpty(ggsport)){
			str = ggsip + ggsport;
		}
		//查询条件2
		else if(!CommonUtils.isEmpty(ggsip) && !CommonUtils.isEmpty(url)){
			str = ggsip + url;
		}
		//查询条件3
		else if(!CommonUtils.isEmpty(url)){
			str = url;
		}
		//查询条件4
		else if(!CommonUtils.isEmpty(telnumber)){
			str = "1";
		}
		
		//如果有对应查询条件
		if(!CommonUtils.isEmpty(str)){
			String md5_str = DigestUtils.md5Hex(str);
			int rowkey_region = Integer.parseInt(md5_str.substring(0, 2),16)
				+Integer.parseInt(md5_str.substring(2, 4),16)
				+Integer.parseInt(md5_str.substring(4, 6),16)
				+Integer.parseInt(md5_str.substring(6, 8),16);			
			if(rowkey_region<10){
				region_str = "000"+String.valueOf(rowkey_region);
			}else if(rowkey_region<100){
				region_str = "00"+String.valueOf(rowkey_region);
			}else if(rowkey_region<1000){
				region_str = "0"+String.valueOf(rowkey_region);
			}else{
				region_str = String.valueOf(rowkey_region);
			}			

			//查询条件1
			if(!CommonUtils.isEmpty(ggsip) && !CommonUtils.isEmpty(ggsport)){
				rowkey_str = region_str + "," + ggsip + "," + ggsport + "," + time;
			}
			//查询条件2
			else if(!CommonUtils.isEmpty(ggsip) && !CommonUtils.isEmpty(url)){
				rowkey_str = region_str + "," + ggsip + "," + url + "," + time;
			}
			//查询条件3
			else if(!CommonUtils.isEmpty(url)){
				rowkey_str = region_str + "," + url + "," + time;
			}
			//查询条件4
			else if(!CommonUtils.isEmpty(telnumber)){
				rowkey_str = generateModKey(Long.valueOf(telnumber)) + "," + telnumber + "," + time;
			}
		}
		
		return rowkey_str;
	}
	
	/**
	 * v1.0.3
	 * @description: 对号码取模
	 * @author: cqx
	 * @date: 2014-03-31
	 * @param telNumber
	 * */
	private String generateModKey(long telnumber){
		long tempLong;
		try{
			random.setSeed(telnumber);
			tempLong =  Math.abs(random.nextLong()%1024);
			if(tempLong < 10){
				return "000"+tempLong;
			}else if(tempLong < 100){
				return "00"+tempLong;
			}else if(tempLong < 1000){
				return "0"+tempLong;
			}else if(tempLong < 1024){
				return ""+tempLong;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		KeepNetLogTest knlt = new KeepNetLogTest();
		KngReqBean requestBean = new KngReqBean();
//		requestBean.setGgsip("117.136.75.162");
//		requestBean.setGgsport("8080");
//		requestBean.setUrl("http://www.baidu.com/");
		requestBean.setTelnumber("13400505847");
		requestBean.setStarttime_s("20160316104150");
		requestBean.setStarttime_e("20160316110016");
		// 起始KEY
		System.out.println(knlt.generateRowKey(requestBean, "s"));
		// 结束KEY
		System.out.println(knlt.generateRowKey(requestBean, "e"));
	}
}
