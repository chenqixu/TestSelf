package com.cqx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.util.Bytes;

public abstract class HBaseTool {
	// 配置文件
	private Configuration conf = null;
	// 表的连接池
	private HTablePool tablePool = null;
	// 表接口
	private HTableInterface hTable = null;
	// Pool大小
	private static final int POOL_SIZE = 5;
	// 分区
	private static final int REGION_PARTITION_NUM = 1024;
//	private static final int REGION_PARTITION_NUM_OLD = 200;
	// 用于取模
	private Random random = new Random();
	// 分隔符
	public final String split_str = ""+((char)((int)01));
	
	// 初始化及加载配置文件
	public HBaseTool(){
		conf = HBaseConfiguration.create();
//		// 加载配置文件
//		conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
//		conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
//		conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
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
			// 创建Hbase的连接池
			tablePool = new HTablePool(conf, POOL_SIZE);
		}
	}

	/**
	 * 根据 rowkey删除一条记录
	 * @param tablename
	 * @param rowkey
	 */
	private void deleteRow(String tablename, Vector<?> rowkey_v)  {
		HTableInterface table = null;
		Delete d1 = null;
		List<Delete> list = null;
		try {
			table = tablePool.getTable(Bytes.toBytes(tablename));
			list = new ArrayList<Delete>();
			for(int i=0;i<rowkey_v.size();i++){
				d1 = new Delete(rowkey_v.get(i).toString().getBytes());
				list.add(d1);
			}				
			table.delete(list);
			System.out.println("删除行成功!");
			d1 = null;
			list = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	    	relase(null, table, null);
			d1 = null;
			list = null;
		}
	}
	
	/**
	 * 插入数据
	 * @param tableName
	 * @param data_rowkey
	 */
	private void insertData(String tableName, Vector<?>[] data_rowkey) {
		System.out.println("start insert data ......");
		Vector<?> rowkey = data_rowkey[0];
		Vector<?> data = data_rowkey[1];
		System.out.println("new HTablePool ......");
		HTableInterface table = tablePool.getTable(Bytes.toBytes(tableName));
		System.out.println("start insert data for rowkey ......");
		for(int i=0;i<rowkey.size();i++){
			//这里用rowkey初始化
			Put put = new Put(rowkey.get(i).toString().getBytes());// 一个PUT代表一行数据，再NEW一个PUT表示第二行数据,每行一个唯一的ROWKEY，此处rowkey为put构造方法中传入的值
//			String iso = "";
			try {
				//由于数据有中文,需要转成utf-8,但utf-8由于是3个字节,如果中文是奇数就会异常,所以要先转iso-8859-1
//				iso = new String(data.get(i).toString().getBytes("UTF-8"),"ISO-8859-1");
//				put.add("info".getBytes(), "gn".getBytes(), iso.getBytes("ISO-8859-1"));//本行数据的第一列
				//put的3个参数:family,qualifier,value
				put.add("info".getBytes(), "gn".getBytes(), Bytes.toBytes(data.get(i).toString()));//本行数据的第一列
				table.put(put);
				if(i%5000==0)System.out.println("put "+i);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(put!=null)
					put = null;
			}
		}
    	relase(null, table, null);
		System.out.println("end insert data ......");
	}
	
	/**
	 * 查询
	 * */
	private StringBuffer queryData(String tableName, String startRowKey, String endRowKey){
		StringBuffer sb = new StringBuffer("");
		HTableInterface hTable = null;
		System.out.println("[tableName]"+tableName+"[startRowKey]"
				+startRowKey+"[endRowKey]"+endRowKey);
		try{
			Scan scan = null;
			// 通过查询RowKey的范围获取所有的记录数
			//hbase的过滤器
//			FilterList filterList = new FilterList();
//			Filter filter = new FilterList(filterList);
			scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			// 设置rowkey过滤
//			scan.setFilter(filterList);
			// 设置列簇
	    	scan.addFamily(Bytes.toBytes("info"));
	    	// 设置表名
	    	hTable = tablePool.getTable(Bytes.toBytes(tableName));
	    	// 查询Hbase数据库返回结果集
	    	ResultScanner rs = hTable.getScanner(scan);
	    	for(Result r:rs){
				for (KeyValue keyValue : r.raw()) {
					sb.append(new String(r.getRow()));
					sb.append(split_str);
					sb.append(Bytes.toString(keyValue.getValue()));
					sb.append("\r\n");
				}
	    	}
	    	// 关闭结果集和表
	    	relase(rs, hTable, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb;
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
	 * 最后程序关闭的时候释放表和连接池资源
	 * */
	public void relaseEnd(){
		try {
			if(hTable!=null)
				hTable.close();
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
	 * @description: 生成RowKey的方法
	 */
	public String generateRowKey(String telNumber, String time) {
		//手机号与22取模,开始使用1024取模版本
		String partitionId = "";
		partitionId = generateModKey(Long.valueOf(telNumber));
		//返回RowKey
		return partitionId + "," + telNumber + "," + time ;
	}
	
	/**
	 * @description: 对号码取模
	 * */
	private String generateModKey(long telnumber){
		long tempLong;
		try{
			// 设置随机因子
			random.setSeed(telnumber);
			tempLong =  Math.abs(random.nextLong()%REGION_PARTITION_NUM);
			if(tempLong < 10){
				return "000"+tempLong;
			}else if(tempLong < 100){
				return "00"+tempLong;
			}else if(tempLong < 1000){
				return "0"+tempLong;
			}else if(tempLong < REGION_PARTITION_NUM){
				return ""+tempLong;
			}
//			if(tempLong < 10){
//				return "00"+tempLong;
//			}else if(tempLong < 100){
//				return "0"+tempLong;
//			}else if(tempLong < REGION_PARTITION_NUM){
//				return ""+tempLong;
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 把传入的内容写入文件
	 * */
	private void writeDataByQuery(String filenamepath, StringBuffer datas){
		FileWriter fw = null;
    	BufferedWriter bw = null;
		System.out.println("filenamepath:"+filenamepath);
		if(filenamepath==null || filenamepath.trim().length()==0)return;
		try {
			File f = new File(filenamepath);
	    	fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
            bw.write(datas.toString());
            bw.flush();
            fw.close();
            bw.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bw!=null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 通过文件把数据读取到Vector
	 * */
	private Vector<?>[] readDataByfile(String filenamepath){
		Vector<String> data = new Vector<String>();
		Vector<String> row_key = new Vector<String>();
		Vector<?>[] result = {row_key, data};
		FileReader fr = null;
    	BufferedReader br = null;
    	try {
    		System.out.println("filenamepath:"+filenamepath);
			File file = new File(filenamepath);
	        fr = new FileReader(file);
	        br = new BufferedReader(fr);
	        String tmp = "";
	        while((tmp = br.readLine()) != null){
		        String[] tmp1 = tmp.split(split_str);
	        	row_key.add(tmp1[0]);
	        	data.add(tmp1[1]);
	        }
	        file = null;
	        fr.close();
	        br.close(); 
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	} finally {
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fr!=null){
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    	}
		return result;
	}
	
	/**
	 * 根据文件删除记录
	 * */
	public void deleteByFile(HbaseInputBean hib){
		String filenamepath = hib.getFilepath();
		String table_name = hib.getTableName();
		// 读取txt
		Vector<?>[] result = readDataByfile(filenamepath);
		Vector<?> rowkey = result[0];
		// 根据rowkey删除对应记录
		deleteRow(table_name, rowkey);
	}
	
	/**
	 * 查询
	 * */
	public void query(HbaseInputBean hib){
		String table_name = hib.getTableName();
		String telnumber = hib.getMsisdn();
		String starttime_s = hib.getStarttime_s();
		String starttime_e = hib.getStarttime_e();
		// 生成startRowKey，用开始时间生成
		String startRowKey = generateRowKey(telnumber, starttime_s);
		// 生成endRowKey ，用结束时间生成
		String endRowKey = generateRowKey(telnumber, starttime_e);
		// 查询出结果
		StringBuffer sb = queryData(table_name, startRowKey, endRowKey);
		System.out.println(sb.toString());
	}
		
	/**
	 * 保存到本地
	 * */
	public void save(HbaseInputBean hib){
		String filenamepath = hib.getFilepath();
		String table_name = hib.getTableName();
		String telnumber = hib.getMsisdn();
		String starttime_s = hib.getStarttime_s();
		String starttime_e = hib.getStarttime_e();
		// 生成startRowKey，用开始时间生成
		String startRowKey = generateRowKey(telnumber, starttime_s);
		// 生成endRowKey ，用结束时间生成
		String endRowKey = generateRowKey(telnumber, starttime_e);
		// 查询出结果
		StringBuffer sb = queryData(table_name, startRowKey, endRowKey);
		// 把结果写入文件
		writeDataByQuery(filenamepath, sb);
	}
	
//	/**
//	 * 保存所有记录到本地
//	 * */
//	public void saveAll(String filenamepath, String table_name){
//		for(int i=0;i<1024;i++){
//			
//		}
//		// 生成startRowKey，用开始时间生成
//		String startRowKey = "0000,";
//		// 生成endRowKey ，用结束时间生成
//		String endRowKey = "1023,";
//		// 查询出结果
//		StringBuffer sb = queryData(table_name, startRowKey, endRowKey);
//		// 把结果写入文件
//		writeDataByQuery(filenamepath, sb);
//	}
	
	/**
	 * 加载到HBase
	 * */
	public void load(HbaseInputBean hib){
		String filenamepath = hib.getFilepath();
		String table_name = hib.getTableName();
		// 读取txt
		Vector<?>[] result = readDataByfile(filenamepath);
		// 插入数据到HBase
		insertData(table_name, result);
	}
	
	/**
	 * 解析参数
	 * */
	public HbaseInputBean parseArgs(String[] args){
		// 输入参数
		HbaseInputBean hib = null;
		Options options = new Options();
		// -c conf
		Option option = new Option("c", "conf", true, "conf");
		option.setRequired(true);
		options.addOption(option);
		// -n tablename
		option = new Option("n", "tablename", true, "tablename");
		option.setRequired(true);
		options.addOption(option);
		// -m msisdn
		option = new Option("m", "msisdn", true, "msisdn");
		option.setRequired(true);
		options.addOption(option);
		// -s starttime_s
		option = new Option("s", "starttime_s", true, "starttime_s");
		option.setRequired(true);
		options.addOption(option);
		// -e starttime_e
		option = new Option("e", "starttime_e", true, "starttime_e");
		option.setRequired(true);
		options.addOption(option);
		// -f filepath
		option = new Option("f", "filepath", true, "filepath");
		option.setRequired(true);
		options.addOption(option);
		// parser
		CommandLineParser parser = new GnuParser();
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// getOptionValue
		String confpath = commandLine.getOptionValue('c');
		String tableName = commandLine.getOptionValue('n');
		String msisdn = commandLine.getOptionValue('m');
		String starttime_s = commandLine.getOptionValue('s');
		String starttime_e = commandLine.getOptionValue('e');
		String filepath = commandLine.getOptionValue('f');
		hib = new HbaseInputBean(confpath,tableName,msisdn,starttime_s,starttime_e,filepath);
		// 加载配置
		initConf(hib);
		return hib;
	}
	
	/**
	 * 使用bulkload方式加载数据
	 * */
	public boolean BulkLoad(HbaseInputBean hib){
		// 获取程序运行开始时间
		long starttime = System.currentTimeMillis();
		try {
			HTable table = new HTable(conf, hib.getTableName());			
			StringBuilder compressionConfigValue = new StringBuilder();
			HTableDescriptor tableDescriptor = table.getTableDescriptor();
			// 判断表描述是否为空，为空则退出
			if (tableDescriptor == null) {
				System.out.println("表描述是否为空");
				System.exit(-1);
			}
			Collection<?> families = tableDescriptor.getFamilies();
			int i = 0;
			HColumnDescriptor familyDescriptor;
			for (Iterator<?> i$ = families.iterator(); i$.hasNext(); compressionConfigValue
					.append(URLEncoder.encode(familyDescriptor.getCompression()
							.getName(), "UTF-8"))) {
				familyDescriptor = (HColumnDescriptor) i$.next();
				if (i++ > 0)
					compressionConfigValue.append('&');
				compressionConfigValue.append(URLEncoder.encode(
						familyDescriptor.getNameAsString(), "UTF-8"));
				compressionConfigValue.append('=');
			}
			System.out.println("|||||||||||||||||||||||||hbase.hfileoutputformat.families.compression="
							+ compressionConfigValue.toString());
			conf.set("hbase.hfileoutputformat.families.compression",
					compressionConfigValue.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// 把hfile载入到hbase中
		LoadIncrementalHFiles loader;
		String tbName = hib.getTableName();
		Path outputPath = new Path(hib.getFilepath());
		try {
			System.out.println("start execute load...");
			loader = new LoadIncrementalHFiles(conf);
			System.out.println("$$$$tableName:" + tbName + " outputPath:"
					+ outputPath);
			loader.doBulkLoad(outputPath, new HTable(conf, tbName));
			System.out.println("load success...");
			System.out.println("End time*****************"
					+ (System.currentTimeMillis() - starttime)
					+ "*********************");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		// 合并文件操作
		try{
			HBaseAdmin admin = new HBaseAdmin(conf);
			admin.compact(tbName);
			admin.close();
			System.out.println("minor compact for " + tbName);
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("minor compact for " + tbName + " failed.");
		}

//		// 加载完成删除输出文件以及输入文件
//		if (!conf.getBoolean(Constant.IS_DEBUG, true)) {
//			for (int jj = 0; jj < data.length; jj++) {
//				String inputPath = conf.get(Constant.INPUT_PATH) + data[jj]
//						+ "/" + statisTime + "/";
//				;
//				if (null != inputPath) {
//					fileSystem.delete(new Path(inputPath), true);
//				}
//			}
//			fileSystem.delete(outputPath, true);
//			System.out.println("加载完成删除输出文件以及输入文件.....");
//		}
		return true;
	}
}
