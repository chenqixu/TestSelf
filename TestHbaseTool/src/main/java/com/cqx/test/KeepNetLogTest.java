package com.cqx.test;

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

import com.cqx.bean.KngReqBean;
import com.cqx.util.CommonUtils;

public class KeepNetLogTest {

	private static Random random = new Random();//����ȡģ
	
	/**
	 * rowkey region:�����ѯ������ȡmd5ǰ8λ��ÿ2λ16����ת10������ӻ��region������
	 * @author cqx
	 * @param requestBean ��ѯ����bean
	 * @param flag s��ʾ��ʼʱ��, e��ʾ����ʱ��
	 * @return ����rowkey,���ݲ�ѯ�����ж�,��3��
	 * 1��2G/3G������NAT���û�����IP��ַ��NAT��Դ�˿ڡ���ѯʱ�䣬��ѯ���ƶ��û����룻
	 * region����,ggsip,ggsport,requesttime(yyyymmddhhmiss),sid,msisdn
	 * 2��2G/3G�������û�����URL��NAT���û�����IP��ַ����ѯʱ�䣬��ѯ���ƶ��û����룻
	 * region����,ggsip,url,requesttime(yyyymmddhhmiss),sid,msisdn
	 * 3��2G/3G�������û�����URL����ѯʱ�䣬��ѯ���ƶ��û����룻
	 * region����,url,requesttime(yyyymmddhhmiss),sid,msisdn
	 * */
	private String generateRowKey(KngReqBean requestBean, String flag) {
		//3�ֲ�ѯ����
		//1��2G/3G������NAT���û�����IP��ַ��NAT��Դ�˿ڡ���ѯʱ�䣬��ѯ���ƶ��û����룻
		//region����,ggsip,ggsport,requesttime(yyyymmddhhmiss),sid,msisdn
		//2��2G/3G�������û�����URL��NAT���û�����IP��ַ����ѯʱ�䣬��ѯ���ƶ��û����룻
		//region����,ggsip,url,requesttime(yyyymmddhhmiss),sid,msisdn
		//3��2G/3G�������û�����URL����ѯʱ�䣬��ѯ���ƶ��û����룻
		//region����,url,requesttime(yyyymmddhhmiss),sid,msisdn
		//4�����ݺ���ʱ����в�ѯ,��������,����Է�Ҫ���嵥
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
		//��ѯ����1
		if(!CommonUtils.isEmpty(ggsip) && !CommonUtils.isEmpty(ggsport)){
			str = ggsip + ggsport;
		}
		//��ѯ����2
		else if(!CommonUtils.isEmpty(ggsip) && !CommonUtils.isEmpty(url)){
			str = ggsip + url;
		}
		//��ѯ����3
		else if(!CommonUtils.isEmpty(url)){
			str = url;
		}
		//��ѯ����4
		else if(!CommonUtils.isEmpty(telnumber)){
			str = "1";
		}
		
		//����ж�Ӧ��ѯ����
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

			//��ѯ����1
			if(!CommonUtils.isEmpty(ggsip) && !CommonUtils.isEmpty(ggsport)){
				rowkey_str = region_str + "," + ggsip + "," + ggsport + "," + time;
			}
			//��ѯ����2
			else if(!CommonUtils.isEmpty(ggsip) && !CommonUtils.isEmpty(url)){
				rowkey_str = region_str + "," + ggsip + "," + url + "," + time;
			}
			//��ѯ����3
			else if(!CommonUtils.isEmpty(url)){
				rowkey_str = region_str + "," + url + "," + time;
			}
			//��ѯ����4
			else if(!CommonUtils.isEmpty(telnumber)){
				rowkey_str = generateModKey(Long.valueOf(telnumber)) + "," + telnumber + "," + time;
			}
		}
		
		return rowkey_str;
	}
	
	/**
	 * v1.0.3
	 * @description: �Ժ���ȡģ
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
		// ��ʼKEY
		System.out.println(knlt.generateRowKey(requestBean, "s"));
		// ����KEY
		System.out.println(knlt.generateRowKey(requestBean, "e"));
	}
}
