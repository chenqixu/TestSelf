package com.bussiness.bi.bigdata.hbase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseTest {
	// 用于取模
	private static Random random = new Random();
	// 分隔符
	public final String split_str = ""+((char)((int)01));
	// 取模分区
	public final int REGION_CODE = 200;

	private Configuration conf = null;  
	private HTablePool tablePool = null;
	private String local_conf_path = "H:/Work/WorkSpace/MyEclipse10/self/test/src/main/resources/newhadoop/";
	private String hn_conf_path = "H:/Work/WorkSpace/MyEclipse10/self/test/src/main/resources/hnconf/";
	
	// zookeeper配置
	private String[] zookeeper_arr = {"10.200.130.31,10.200.130.32,10.200.130.33"
			, "192.168.230.128"
			, "10.1.8.1,10.1.8.2,10.1.8.3"};
	
	public HbaseTest(){
		conf = HBaseConfiguration.create();
//		addResource();
		conf.set("hbase.zookeeper.quorum", zookeeper_arr[2]); //设置zookeeper地址
		conf.set("hbase.zookeeper.property.clientPort", "2181"); //设置zookeeper端口
		conf.set("hbase.regionserver.lease.period", "1000");
		conf.set("hbase.client.scanner.timeout.period", "1000");
		// windows本地化DLL
		System.setProperty("hadoop.home.dir", "H:/Program Files/hadoop-common-2.2.0-bin-master");
        // 创建Hbase的连接池
        tablePool = new HTablePool(conf, 5);
	}
	
	private void addResource(){
        //需要设置hadoop用户,否则没有权限
        System.setProperty("HADOOP_USER_NAME", "hadoop");
		if(conf!=null){
			String _path = local_conf_path;
			conf.addResource(new Path(_path+"core-site.xml"));
			conf.addResource(new Path(_path+"hdfs-site.xml"));
			conf.addResource(new Path(_path+"mapred-site.xml"));
			conf.addResource(new Path(_path+"yarn-site.xml"));
			conf.addResource(new Path(_path+"hbase-site.xml"));
		}
		System.out.println("[conf]"+conf);
	}
	
//	public void split(String tableName,int number,int timeout) throws Exception {
//	    Configuration HBASE_CONFIG = new Configuration(); //配置
//	    HBASE_CONFIG.set("hbase.zookeeper.quorum", "10.1.8.1,10.1.8.2,10.1.8.3"); //设置zookeeper地址
//	    HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181"); //设置zookeeper端口
//	    HBaseConfiguration cfg = new HBaseConfiguration(HBASE_CONFIG); //HBase默认配置
//	    HBaseAdmin hAdmin = new HBaseAdmin(cfg); //创建HBaseAdmin 实例
//	    HTable hTable = new HTable(cfg,tableName); //创建HTable 实例
//	    int oldsize = 0;
//	    long t =  System.currentTimeMillis();
//	    while(true){
//	       int size = hTable.getRegionsInfo().size(); //获得当前HTable的Regions数量
//	       System.out.println("the region number="+size);
//	       if(size>=number ) break;
//	       if(size!=oldsize){
//	           hAdmin.split(hTable.getTableName()); //切分表
//	           oldsize = size;
//	       }
//	       else if(System.currentTimeMillis()-t>timeout){
//	           break;
//	       }
//	       Thread.sleep(1000*10);
//	    }
//	}
	
//	public void insert() throws Exception{
//		String tableName = "gn_cdr_1";
//	    Configuration HBASE_CONFIG = new Configuration(); //配置
//	    HBASE_CONFIG.set("hbase.zookeeper.quorum", "10.1.8.1,10.1.8.2,10.1.8.3"); //设置zookeeper地址
//	    HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181"); //设置zookeeper端口
//		HTable table = new HTable(HBASE_CONFIG, tableName); //创建HTable 实例
//		boolean autoFlush = false;
//		table.setAutoFlush(autoFlush); //设置是否自动刷新
//		List<Put> lp = new ArrayList<Put>();
//		int count = 10000;
//		byte[] buffer = new byte[1024];
//		Random r = new Random();
//		boolean wal = false;
//		for (int i = 1; i <= count; ++i) {
//		   Put p = new Put(String.format("row%09d",i).getBytes()); //实例化Put
//		   r.nextBytes(buffer);
//		   p.add("f1".getBytes(), null, buffer);
//		   p.add("f2".getBytes(), null, buffer);
//		   p.add("f3".getBytes(), null, buffer);
//		   p.add("f4".getBytes(), null, buffer);
//		   p.setWriteToWAL(wal); //设置是否写入WAL，Write-Ahead Logging 预写日志系统 数据库中一种高效的日志算法
//		   lp.add(p);
//		   if(i%1000==0){
//		       table.put(lp); //写入数据到HTable
//		       lp.clear();
//		   }
//		}
//	}

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
//				System.out.println("put "+i);
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
		try{
			Scan scan = null;
			// 通过查询RowKey的范围获取所有的记录数
			//hbase的过滤器
			FilterList filterList = new FilterList();			
//			RowFilter rowFilter = new RowFilter(  
//			        CompareFilter.CompareOp.EQUAL, new SubstringComparator(",bb")); 
//			filterList.addFilter(rowFilter);
			scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			if(filterList.getFilters().size()>0){
				Filter filter = new FilterList(filterList);
				// 设置rowkey过滤
				scan.setFilter(filterList);
			}
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
	public void relase(ResultScanner rs, HTableInterface table, HTablePool tablePool){		
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
	 * @description: 生成RowKey的方法
	 */
	private String generateRowKey(String telNumber, String time) {
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
			random.setSeed(telnumber);
			long result = Math.abs(random.nextLong() % REGION_CODE);
			String resStr = result + "";//转换成String，统计结果的位数
			String modStr = REGION_CODE + "";//装成String，统计模的位数
			int resDigit = resStr.length();
			int modDigit = modStr.length();
			for(int i = 0;i< modDigit - resDigit;i++){
				resStr= "0" + resStr;
			}
			return resStr;
//			random.setSeed(telnumber);
//			tempLong =  Math.abs(random.nextLong()%REGION_CODE);
//			int SL = String.valueOf(REGION_CODE).length();
//			int j = 0;
//			for(int i=SL-1;i>=0;i--){
//				j++;
//				String _tmp = "";
//				for(int x=0;x<i;x++){
//					_tmp += "0";
//				}
//				if(_tmp.length()>0 && tempLong<Math.pow(10, j)){
//					return _tmp+tempLong;
//				}else if(_tmp.length()==0 && tempLong < REGION_CODE){
//					return ""+tempLong;
//				}
//			}
//			if(tempLong < 10){
//				return "000"+tempLong;
//			}else if(tempLong < 100){
//				return "00"+tempLong;
//			}else if(tempLong < 1000){
//				return "0"+tempLong;
//			}else if(tempLong < REGION_CODE){
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
	public void deleteByFile(String filenamepath, String table_name){
		// 读取txt
		Vector<?>[] result = readDataByfile(filenamepath);
		Vector<?> rowkey = result[0];
		// 根据rowkey删除对应记录
		deleteRow(table_name, rowkey);
	}
	
	/**
	 * 查询
	 * */
	public void query(String table_name, String telnumber, String starttime_s, String starttime_e){
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
	public void save(String filenamepath, String table_name, String telnumber,
			String starttime_s, String starttime_e){
		// 生成startRowKey，用开始时间生成
		String startRowKey = generateRowKey(telnumber, starttime_s);
		// 生成endRowKey ，用结束时间生成
		String endRowKey = generateRowKey(telnumber, starttime_e);
		// 查询出结果
		StringBuffer sb = queryData(table_name, startRowKey, endRowKey);
		// 把结果写入文件
		writeDataByQuery(filenamepath, sb);
	}
	
	/**
	 * 保存所有记录到本地
	 * */
	public void saveAll(String filenamepath, String table_name){
		for(int i=0;i<1024;i++){
			
		}
		// 生成startRowKey，用开始时间生成
		String startRowKey = "0000,";
		// 生成endRowKey ，用结束时间生成
		String endRowKey = "1023,";
		// 查询出结果
		StringBuffer sb = queryData(table_name, startRowKey, endRowKey);
		// 把结果写入文件
		writeDataByQuery(filenamepath, sb);
	}
	
	/**
	 * 加载到HBase
	 * */
	public void load(String filenamepath, String table_name){
		// 读取txt
		Vector<?>[] result = readDataByfile(filenamepath);
		// 插入数据到HBase
		insertData(table_name, result);
	}
	
	public static void main(String[] args) {
//		org.apache.htrace.Trace a;
		
		final HbaseTest ht = new HbaseTest();
		String telnumber = "13509323824";//"18876901776";//"18789726730";//"13807647215";
		String starttime_s = "20160101";//"20160401000026";
		String starttime_e = "20160102";//"20160401000718";
		String table_name = "qry_net_log_app_action_total_d_1";//"qry_netlog_4";		
//		String filenamepath = "d:/home/hbase/data/"+telnumber+"_"+starttime_s+"_"+starttime_e;
		// 取模
//		System.out.println(ht.generateModKey(Long.valueOf(telnumber)));
		// 查询并输出到控制台
		ht.query(table_name, telnumber, starttime_s, starttime_e);
		// 根据文件删除指定记录
//		ht.deleteByFile(filenamepath, table_name);		
		// 查询并保存
//		ht.save(filenamepath, table_name, telnumber, starttime_s, starttime_e);
		// 加载入库
//		ht.load(filenamepath, table_name);
		// 查询全部并保存
//		ht.saveAll(filenamepath, table_name);
		/*
		 * 在jvm中增加一个关闭的钩子，当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，
		 * 当系统执行完这些钩子后，jvm才会关闭。所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作
		 * */
		Runtime.getRuntime().addShutdownHook(
			new Thread("relase-shutdown-hook") {
				@Override
				public void run() {
					// 释放连接池资源
					ht.relase(null, null, ht.tablePool);
				}
			}
		);
	}
}
