package com.bussiness.bi.bigdata.sqlldr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class ChangeCreateTosqlldr {
	// 读取建表文件夹文件的建表语句，生成sqlldr文件
	// d:\Work\ETL\上网查证\海南\数据库设计文档\建表语句\
	
	private static final String read_code = "GBK";
	private static final String write_code = "GBK";
	private static final String tab = "\r\n";
	private static final String ctl = ".ctl";
	private static final String bat = ".bat";
	private static final String log = ".log";
	private static Map<String, String> sourcemap = new HashMap<String, String>();	
	static {
		if(sourcemap==null)sourcemap = new HashMap<String, String>();	
		sourcemap.put("ps_gn_dns_event","PS_DNS_HI_YDHIQ00107_000000000096_20160107194212.txt");
		sourcemap.put("ps_gn_email_event","PS_EMAIL_HI_YDHIQ00107_000000000092_20160107154810.txt");
		sourcemap.put("ps_gn_ftp_event","PS_FTP_HI_YDHIQ00106_000000000079_20160107155531.txt");
		sourcemap.put("ps_gn_general_event","PS_G_HI_YDHIQ00107_000000000092_20160107155909.txt");
		sourcemap.put("ps_gn_http_event","PS_HTTP_HI_YDHIQ00107_000000000036_20160107155705.txt");
		sourcemap.put("ps_gn_im_event","PS_IM_HI_YDHIQ00039_000000000023_20160107155911.txt");
		sourcemap.put("ps_gn_mms_event","PS_MMS_HI_YDHIQ00106_000000000013_20160107155913.txt");
		sourcemap.put("ps_gn_p2p_event","PS_P2P_HI_YDHIQ00106_000000000066_20160107155913.txt");
		sourcemap.put("ps_gn_pdp_event","PS_PDP_HI_YDHIQ00104_000000000066_20160107155906.txt");
		sourcemap.put("ps_gn_rtsp_event","PS_RTSP_HI_YDHIQ00104_000000000020_20160107193810.txt");
		sourcemap.put("ps_gn_voip_event","PS_VOIP_HI_YDHIQ00039_000000000016_20160107195331.txt");
		
		sourcemap.put("lte_dns_event","S1U-101-20160108231000-649-01.txt");
		sourcemap.put("lte_email_event","S1U-105-20160108111000-990-01.txt");
		sourcemap.put("lte_ftp_event","S1U-104-20160106184000-405-01.txt");
		sourcemap.put("lte_general_event","S1U-100-20160108233000-606-01.txt");
		sourcemap.put("lte_http_event","S1U-103-20160108111000-015-01.txt");
		sourcemap.put("lte_im_event","S1U-108-20160108111000-986-01.txt");
		sourcemap.put("lte_mms_event","S1U-102-20160108112000-018-01.txt");
		sourcemap.put("lte_p2p_event","S1U-109-20160108111000-992-01.txt");
		sourcemap.put("lte_rstp_event","S1U-107-20160107020000-039-01.txt");
		sourcemap.put("lte_s11_event","S11-000-20160330120000-394-01.txt");
		sourcemap.put("lte_s1mme_event","S1MME-000-20160330120000-785-01.txt");
		sourcemap.put("lte_s6a_event","S6a-000-20160330120000-396-01.txt");
		sourcemap.put("lte_sgs_event","SGs-000-20160330120000-395-01.txt");
		sourcemap.put("lte_voip_event","S1U-106-20160108111000-994-01.txt");
	}
	
	public void getAllFileName(String path){
		File cp = new File(path);
		if(cp.isDirectory()){
			for(File resource : cp.listFiles()){				
				System.out.println(resource.getName());
			}
		}
	}
	
	public void readFile(String path, String write_path){
		File cp = new File(path);
		if(cp.isDirectory()){
			for(File resource : cp.listFiles()){
				String _path = resource.getPath();
				if(_path.endsWith(".sql")){
					String file_name = resource.getName();
					String table_name = file_name.substring(0, file_name.length()-4);
					change(table_name, _path, write_path+table_name);
				}
			}
		}		
	}
	
	public void change(String table_name, String file_path, String write_path){
		BufferedReader reader = null;
		BufferedWriter writer = null;
		StringBuffer sb = new StringBuffer();
		sb.append("Load DATA "+tab);
		sb.append("CHARACTERSET UTF8 "+tab);
		sb.append("INFILE '"+sourcemap.get(table_name)+"' "+tab);
		sb.append("Append INTO TABLE "+table_name+" "+tab);
		sb.append("fields terminated by \"|\" "+tab);
		sb.append("TRAILING NULLCOLS  "+tab);
		sb.append("(  ");
		try{
			File readFile = new File(file_path);
			File writeFile_ctl = new File(write_path+ctl);
			File writeFile_bat = new File(write_path+bat);
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(readFile), read_code));
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(writeFile_ctl), write_code));
			String _tmp = null;
			// 读取并写入ctl
			while((_tmp=reader.readLine())!=null){
				if(_tmp.indexOf("create table")>=0){
					writer.write(sb.toString());
					writer.write(tab);
				}else{
					writer.write(_tmp.replace("varchar2", "char").replace(";", ""));
					writer.write(tab);
				}
			}
			reader.close();
			writer.flush();
			writer.close();
			// 写入bat
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(writeFile_bat), write_code));
			sb.delete(0, sb.length());
			sb.append("set NLS_LANG=SIMPLIFIED CHINESE_CHINA.ZHS16GBK"+tab);
			sb.append("sqlldr devmart/devmart@tsbass readsize=1048576 bindsize=1000000 control=\""+table_name+ctl+"\" log=\""+table_name+log+"\" ROWS=5000 direct=y"+tab);
			sb.append("pause");
			writer.write(sb.toString());
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		ChangeCreateTosqlldr cct = new ChangeCreateTosqlldr();
		String dict_path = "D:\\home\\hndata\\sqlldr\\";
		String local_gn_path = "D:\\Work\\ETL\\上网查证\\海南\\数据库设计文档\\gn建表语句\\";
		String local_lte_path = "D:\\Work\\ETL\\上网查证\\海南\\数据库设计文档\\lte建表语句\\";
		String source_path = "D:\\home\\hndata\\source\\";
//		cct.readFile(local_lte_path, dict_path);
		cct.getAllFileName(local_gn_path);
		cct.getAllFileName(local_lte_path);
	}
}
