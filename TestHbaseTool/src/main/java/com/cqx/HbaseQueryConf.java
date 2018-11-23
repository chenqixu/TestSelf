package com.cqx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * 根据配置文件的条件进行查询
 * 先查询索引表
 * 再根据索引表结果查询事实表
 * */
public class HbaseQueryConf {
	// 配置文件
	private Configuration conf = null;
	// 表的连接池
	private HTablePool tablePool = null;
	// Pool大小
	private static final int POOL_SIZE = 5;
	
	public HbaseQueryConf(){
		conf = HBaseConfiguration.create();
	}
	
	/**
	 * 加载配置文件
	 * */
	public void initConf(String confpath){
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
			// 创建Hbase的连接池
			tablePool = new HTablePool(conf, POOL_SIZE);
		}
	}
	
	/**
	 * 释放资源
	 * */
	private void relase(ResultScanner rs, HTableInterface table, HTablePool tablePool){		
		try {
			if(rs!=null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if(table!=null)
				table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(tablePool!=null)
				tablePool.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询索引表
	 * */
	public List<Get> queryIndex(){
		List<Get> resultlist = new ArrayList<Get>();
		HTableInterface hTable = null;
		try{
			String start = "000000";
			String end = "235959";
			String tableName = conf.get("QINDEXTABLE");
			String qdate = conf.get("QDATE");
			String qhost = conf.get("QHOST");
			String startRowKey = qdate + "," + qhost + "," + qdate + start;
			String endRowKey = qdate + "," + qhost + "," + qdate + end;
			System.out.println("[startRowKey]"+startRowKey+" [endRowKey]"+endRowKey);
			Scan scan = null;
			scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			// 设置列簇
	    	scan.addFamily(Bytes.toBytes("info"));
	    	// 设置表名
	    	hTable = tablePool.getTable(Bytes.toBytes(tableName));
	    	// 查询Hbase数据库返回结果集
	    	ResultScanner rs = hTable.getScanner(scan);
	    	for(Result r:rs){
				for (KeyValue keyValue : r.raw()) {
					resultlist.add(new Get(keyValue.getValue()));
					System.out.println("[index]"+Bytes.toString(keyValue.getValue()));
				}
	    	}
	    	// 关闭结果集和表
	    	relase(rs, hTable, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultlist;
	}
	
	/**
	 * 根据索引查询结果
	 * */
	public void queryFact(List<Get> querylist){
		HTableInterface hTable = null;
		try{
			String tableName = conf.get("QFACTTABLE");
			String family = conf.get("FAMILY");
			String qualifier = conf.get("QUALIFIER");
	    	// 设置表名
	    	hTable = tablePool.getTable(Bytes.toBytes(tableName));
	    	String rowkey = "";
	    	// 根据rowkey查询
	    	Result[] r = hTable.get(querylist);
	    	for(Result r1 : r){
	    		System.out.println("[query result]"+new String(r1.getValue(family.getBytes(),qualifier.getBytes()),"utf-8"));
	    	}
	    	// 关闭结果集和表
	    	r = null;
	    	relase(null, hTable, null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		HbaseQueryConf hqc = new HbaseQueryConf();
		String local_conf_path = "";
		// 配置文件
		if(args.length==1){
			local_conf_path = args[0];
			// 初始化配置
			hqc.initConf(local_conf_path);
			// 根据索引对事实表进行查询
			hqc.queryFact(hqc.queryIndex());
		}else{
			System.out.println("没有配置文件，退出。");
			System.exit(-1);
		}		
	}
}
