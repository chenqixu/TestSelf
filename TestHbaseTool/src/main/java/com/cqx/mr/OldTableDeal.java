package com.cqx.mr;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;

import com.cqx.HbaseInputBean;

public class OldTableDeal {
	// 配置文件
	private Configuration conf = null;
	// 分区
	private static final int REGION_PARTITION_NUM = 200;
	// 用于取模
	private Random random = new Random();
	// 分隔符
	public final String split_str = ""+((char)((int)01));

	// 初始化及加载配置文件
	public OldTableDeal(){
		conf = HBaseConfiguration.create();		
	}
	
	/**
	 * 加载配置文件
	 * */
	public void initConf(HbaseInputBean hib){
		String confpath = hib.getConfpath();
		// 扫描conf path下的所有xml配置文件
		File cp = new File(confpath);
		if(cp.isDirectory()){
			for(File resource : cp.listFiles()){
				String _path = resource.getPath();
				if(_path.endsWith(".xml")){
					conf.addResource(new Path(_path));
				}
			}
			System.out.println("[conf]"+conf);
		}
	}
	
	public void getHBaseData(int numOfDays) throws IOException,
		InterruptedException, ClassNotFoundException {
		
		
	}
	
	public static void main(String[] args) {
		
	}
}
