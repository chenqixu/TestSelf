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
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;

public class HbasePut {
	
	private Configuration HBASE_CONFIG = null;
	//分区数
	public static final int PARTITION_NUM = 22;
	//分隔符
	public final String split_str = ""+((char)((int)01));
	private static Random random = new Random();//用于取模
	
	public HbasePut(){}
	
	public HbasePut(String ip){
		/*
		HBaseConfiguration是每一个hbase client都会使用到的对象，它代表的是HBase配置信息。它有两种构造方式：
		public HBaseConfiguration()
		public HBaseConfiguration(final Configuration c)
		默认的构造方式会尝试从hbase-default.xml和hbase-site.xml中读取配置。如果classpath没有这两个文件，就需要你自己设置配置。
		*/
		HBASE_CONFIG = new Configuration();
		HBASE_CONFIG.set("hbase.zookeeper.quorum", ip);
		HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
		
//		HBASE_CONFIG.set("hbase.zookeeper.dns.interface", "default");
//		HBASE_CONFIG.set("hbase.zookeeper.dns.nameserver", "default");
//		HBASE_CONFIG.set("zookeeper.session.timeout", "360000");
//		HBASE_CONFIG.set("zookeeper.znode.parent", "/hbase");
//		HBASE_CONFIG.set("zookeeper.znode.rootserver", "root-region-server");
//		HBASE_CONFIG.set("zookeeper.znode.acl.parent", "acl");
//		HBASE_CONFIG.set("hbase.zookeeper.peerport", "2888");
//		HBASE_CONFIG.set("hbase.zookeeper.leaderport", "3888");
//		HBASE_CONFIG.set("hbase.zookeeper.useMulti", "true");
//		HBASE_CONFIG.set("hbase.zookeeper.property.initLimit", "10");
//		HBASE_CONFIG.set("hbase.zookeeper.property.syncLimit", "5");
//		HBASE_CONFIG.set("hbase.zookeeper.property.dataDir", "${hbase.tmp.dir}/zookeeper");
//		HBASE_CONFIG.set("hbase.zookeeper.property.maxClientCnxns", "0");
		
//		HBaseConfiguration cfg = new HBaseConfiguration(HBASE_CONFIG);
	}
	
	/**
	 * 写入数据
	 * @throws Exception 
	 * */
	public void putdata(String tableName) throws Exception{
		/*
		HBaseConfiguration是每一个hbase client都会使用到的对象，它代表的是HBase配置信息。它有两种构造方式：
		public HBaseConfiguration()
		public HBaseConfiguration(final Configuration c)
		默认的构造方式会尝试从hbase-default.xml和hbase-site.xml中读取配置。如果classpath没有这两个文件，就需要你自己设置配置。
		*/
//		Configuration HBASE_CONFIG = new Configuration();
//		HBASE_CONFIG.set("hbase.zookeeper.quorum", "10.1.4.54");
//		HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181");
//		HBaseConfiguration cfg = new HBaseConfiguration(HBASE_CONFIG);
		/*
		HTable通过put方法来插入数据。 
		public void put(final Put put) throws IOException
		public void put(final List puts) throws IOException
		可以传递单个批Put对象或者List put对象来分别实现单条插入和批量插入。
		Put提供了3种构造方式：
		public Put(byte [] row)
		public Put(byte [] row, RowLock rowLock)
		public Put(Put putToCopy) 
		*/
		HTable table = new HTable(HBASE_CONFIG, tableName);
		//setAutoFlash: AutoFlush指的是在每次调用HBase的Put操作，是否提交到HBase Server。
		//默认是true,每次会提交。如果此时是单条插入，就会有更多的IO,从而降低性能.
		table.setAutoFlush(true);
	
		List lp = new ArrayList();	
		int count = 10000;	
		byte[] buffer = new byte[1024];	
		Random r = new Random();	
		for (int i = 1; i <= count; ++i) {
	       Put p = new Put(String.format("row%09d",i).getBytes());
	       r.nextBytes(buffer);
//	       p.add(new KeyValue());
	       p.add("f1".getBytes(), null, buffer);
	       p.add("f2".getBytes(), null, buffer);
	       p.add("f3".getBytes(), null, buffer);
	       p.add("f4".getBytes(), null, buffer);
	       /*
	       etWriteToWAL: WAL是Write Ahead Log的缩写，指的是HBase在插入操作前是否写Log。
	        默认是打开，关掉会提高性能， 但是如果系统出现故障(负责插入的Region Server挂掉)，数据可能会丢失。
	       */
//	       p.setWriteToWAL(false);
	       p.setDurability(Durability.SKIP_WAL); // 替代setWriteToWAL
	       lp.add(p);
	       if(i%1000==0){
	           table.put(lp);
	           lp.clear();
	       }
	    }
	}
	
	/**
	 * 创建表
	 * @param tableName
	 */
	public void createTable(String tableName) {
		System.out.println("start create table ......");
		try {
			HBaseAdmin hBaseAdmin = new HBaseAdmin(HBASE_CONFIG);
			if (hBaseAdmin.tableExists(tableName)) {// 如果存在要创建的表，那么先删除，再创建
				hBaseAdmin.disableTable(tableName);
				hBaseAdmin.deleteTable(tableName);
				System.out.println(tableName + " is exist,detele....");
			}
			HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
			tableDescriptor.addFamily(new HColumnDescriptor("column1"));
			tableDescriptor.addFamily(new HColumnDescriptor("column2"));
			tableDescriptor.addFamily(new HColumnDescriptor("column3"));
			hBaseAdmin.createTable(tableDescriptor);
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("end create table ......");
	}

	/**
	 * 插入数据
	 * @param tableName
	 * @param data_rowkey
	 */
	public void insertData(String tableName, Vector[] data_rowkey) {
		System.out.println("start insert data ......");
		Vector rowkey = data_rowkey[0];
		Vector data = data_rowkey[1];
		System.out.println("new HTablePool ......");
		HTablePool pool = new HTablePool(HBASE_CONFIG, 10);
		System.out.println("pool.getTable tableName:"+tableName+" ......");
		HTableInterface table = pool.getTable(tableName);
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
				System.out.println("put "+i);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(put!=null)
					put = null;
			}
		}
		try {
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			pool.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("end insert data ......");
	}
	
	/**
	 * 根据表名,列簇,列簇下的列 插入数据
	 * @param tableName 表名
	 * @param ColumnFamily 列簇名称
	 * @param rowKey 列簇值
	 * @param qualifiers 列簇下的列
	 * @param data_value 列簇下的列的值
	 * */
	public void putData(String tableName, String ColumnFamily, Vector<String> rowKey,
			Vector<String> qualifiers, Vector<Vector<String>> data_value){
		HTablePool pool = new HTablePool(HBASE_CONFIG, 10);
		HTableInterface table = pool.getTable(tableName);
		for(int i=0;i<rowKey.size();i++){
			//一个PUT代表一行数据，再NEW一个PUT表示第二行数据,每行一个唯一的ROWKEY，
			//此处rowkey为put构造方法中传入的值
			Put put = new Put(rowKey.get(i).toString().getBytes());
			try {
				//put的3个参数:family,qualifier,value
				for(int j=0;j<qualifiers.size();j++){//同个列簇的几个列
					put.add(ColumnFamily.getBytes(), qualifiers.get(j).getBytes(),
							Bytes.toBytes(data_value.get(i).get(j).toString()));
				}				
				table.put(put);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(put!=null)
					put = null;
			}
		}
		try {
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			pool.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除一张表
	 * @param tableName
	 */
	public void dropTable(String tableName) {
		try {
			HBaseAdmin admin = new HBaseAdmin(HBASE_CONFIG);
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据 rowkey删除一条记录
	 * @param tablename
	 * @param rowkey
	 */
	public void deleteRow(String tablename, Vector rowkey_v)  {
		HTable table = null;
		Delete d1 = null;
		List<Delete> list = null;
		try {
			table = new HTable(HBASE_CONFIG, tablename);
			list = new ArrayList<Delete>();
			for(int i=0;i<rowkey_v.size();i++){
				d1 = new Delete(rowkey_v.get(i).toString().getBytes());
				list.add(d1);
			}				
			table.delete(list);
			System.out.println("删除行成功!");
			table.close();
			d1 = null;
			list = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(table!=null)
				try {
					table.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			d1 = null;
			list = null;
		}
	}

	 /**
	  * 组合条件删除
	  * @param tablename
	  * @param rowkey
	  */
	public void deleteByCondition(String tablename, String rowkey)  {
			//目前还没有发现有效的API能够实现 根据非rowkey的条件删除 这个功能能，还有清空表全部数据的API操作
	}


	/**
	 * 查询所有数据
	 * @param tableName
	 */
	@SuppressWarnings("deprecation")
	public void QueryAll(String tableName) {
		try {
			HTable table =  new HTable(HBASE_CONFIG, tableName);
			ResultScanner rs = table.getScanner(new Scan());
			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue keyValue : r.raw()) {
					System.out.println("列：" + new String(keyValue.getFamily())
							+ "====值:" + new String(keyValue.getValue(), "utf-8"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		HTablePool pool = new HTablePool(HBASE_CONFIG, 10);
//		HTableInterface table = null;
//		ResultScanner rs = null;
//		try {
//			Scan scan = new Scan();
//			table = pool.getTable(tableName);
//			rs = table.getScanner(scan);
//			for (Result r : rs) {
//				System.out.println("获得到rowkey:" + new String(r.getRow()));
//				for (KeyValue keyValue : r.raw()) {
//					System.out.println("列：" + new String(keyValue.getFamily())
//							+ "====值:" + new String(keyValue.getValue(), "utf-8"));
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			if(rs!=null)
//				rs.close();// 最后还得关闭
//			try {
//				if(table!=null)
//					pool.putTable(table);//将不使用的表重新放入到HTablePool中的操作
//			} catch (IOException e) {
//				e.printStackTrace();
//			} //实际应用过程中，pool获取实例的方式应该抽取为单例模式的，不应在每个方法都重新获取一次(单例明白？就是抽取到专门获取pool的逻辑类中，具体逻辑为如果pool存在着直接使用，如果不存在则new)
//		}
	}

	/**
	 * 单条件查询,根据rowkey查询唯一一条记录
	 * @param tableName
	 * @param rowkey
	 */
	public void QueryByCondition1(String tableName, String rowkey) {
		HTable table = null;
		try {
			table = new HTable(HBASE_CONFIG, tableName);
			Get scan = new Get(rowkey.getBytes());// 根据rowkey查询
			Result r = table.get(scan);
			System.out.println("获得到rowkey:" + new String(r.getRow()));
			for (KeyValue keyValue : r.raw()) {
				System.out.println("列：" + new String(keyValue.getFamily())
						+ "====值:" + new String(keyValue.getValue(), "utf-8"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 单条件按查询，查询多条记录
	 * @param tableName
	 */
	public void QueryByCondition2(String tableName) {
		try {
			HTable table = new HTable(HBASE_CONFIG, tableName);
			Filter filter = new SingleColumnValueFilter(Bytes
					.toBytes("info"), null, CompareOp.EQUAL, Bytes
					.toBytes("aaa")); // 当列column1的值为aaa时进行查询
			Scan s = new Scan();
			s.setFilter(filter);
			ResultScanner rs = table.getScanner(s);
			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue keyValue : r.raw()) {
					System.out.println("列：" + new String(keyValue.getFamily())
							+ "====值:" + new String(keyValue.getValue(), "utf-8"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 组合条件查询
	 * @param tableName
	 */
	public void QueryByCondition3(String tableName) {
		try {
			HTable table = new HTable(HBASE_CONFIG, tableName);

			List<Filter> filters = new ArrayList<Filter>();

			Filter filter1 = new SingleColumnValueFilter(Bytes
					.toBytes("column1"), null, CompareOp.EQUAL, Bytes
					.toBytes("aaa"));
			filters.add(filter1);

			Filter filter2 = new SingleColumnValueFilter(Bytes
					.toBytes("column2"), null, CompareOp.EQUAL, Bytes
					.toBytes("bbb"));
			filters.add(filter2);

			Filter filter3 = new SingleColumnValueFilter(Bytes
					.toBytes("column3"), null, CompareOp.EQUAL, Bytes
					.toBytes("ccc"));
			filters.add(filter3);

			FilterList filterList1 = new FilterList(filters);

			Scan scan = new Scan();
			scan.setFilter(filterList1);
			ResultScanner rs = table.getScanner(scan);
			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue keyValue : r.raw()) {
					System.out.println("列：" + new String(keyValue.getFamily())
							+ "====值:" + new String(keyValue.getValue(), "utf-8"));
				}
			}
			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 组合条件查询,rowkey包含某些字符串的模糊查询
	 * @param tableName
	 */
	public void QueryByCondition4(String tableName, String ip, String port, String url, 
			String column_family, String startRowKey, String endRowKey) {
		Scan scan = null;
		ResultScanner rs = null;
		HTable hTable = null;
		try {
			List<Filter> filters = new ArrayList<Filter>();			
			if(ip!=null && ip.trim().length()>0){
				Filter filter1 = new RowFilter(CompareOp.EQUAL, new SubstringComparator(ip));
				filters.add(filter1);
			}
			if(port!=null && port.trim().length()>0){
				Filter filter2 = new RowFilter(CompareOp.EQUAL, new SubstringComparator(port));
				filters.add(filter2);
			}
			if(url!=null && url.trim().length()>0){
				Filter filter3 = new RowFilter(CompareOp.EQUAL, new SubstringComparator(url));
				filters.add(filter3);
			}
			FilterList filterList1 = new FilterList(filters);
			//通过查询RowKey的范围获取所有的记录数
			scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			//设置rowkey过滤
			scan.setFilter(filterList1);
			//设置列簇
	    	scan.addFamily(Bytes.toBytes(column_family));
	    	//设置表名
	    	hTable = new HTable(HBASE_CONFIG, tableName);
	    	//查询Hbase数据库返回结果集
	    	rs = hTable.getScanner(scan);
			//循环结果
			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue keyValue : r.raw()) {
					System.out.println("列：" + new String(keyValue.getFamily())
							+ "====值:" + new String(keyValue.getValue(), "utf-8"));
				}
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null)
				rs.close();
			if(hTable!=null)
				try {
					hTable.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * 组合条件查询,rowkey包含某些字符串的模糊查询
	 * @param tableName
	 */
	public void QueryByCondition5(String tableName, String ip, String port, String url, 
			String column_family, String startRowKey, String endRowKey) {
		Scan scan = null;
		ResultScanner rs = null;
		HTable hTable = null;
		try {
			//hbase的过滤器
			FilterList filterList = new FilterList();

			//CompareFilter.CompareOp.EQUAL表示精确匹配
			//这里在前面+半角逗号和后面+半角逗号来进行精确匹配
			//当ip不为空时		
			if(ip!=null && ip.trim().length()>0){
				RowFilter rowFilter = new RowFilter(  
				        CompareFilter.CompareOp.EQUAL, new SubstringComparator(","+ip+",")); 
				filterList.addFilter(rowFilter);
			}
			//当端口不为空时
			if(port!=null && port.trim().length()>0){
				RowFilter rowFilter = new RowFilter(  
				        CompareFilter.CompareOp.EQUAL, new SubstringComparator(","+port+",")); 
				filterList.addFilter(rowFilter);
			}
			//当url不为空时
			if(url!=null && url.trim().length()>0){
				RowFilter rowFilter = new RowFilter(  
				        CompareFilter.CompareOp.EQUAL, new SubstringComparator(url)); 
				filterList.addFilter(rowFilter);
			}
			
			//通过查询RowKey的范围获取所有的记录数
			scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			//设置rowkey过滤
			scan.setFilter(filterList);
			//设置列簇
	    	scan.addFamily(Bytes.toBytes(column_family));
	    	//设置表名
	    	hTable = new HTable(HBASE_CONFIG, tableName);
	    	//查询Hbase数据库返回结果集
	    	rs = hTable.getScanner(scan);
			//循环结果
			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue keyValue : r.raw()) {
					System.out.println("列：" + new String(keyValue.getFamily())
							+ "====值:" + new String(keyValue.getValue(), "utf-8"));
				}
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null)
				rs.close();
			if(hTable!=null)
				try {
					hTable.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	
	/**
	 * 组合条件查询,rowkey包含某些字符串的模糊查询
	 * @param tableName
	 */
	public void QueryByCondition6(String tableName, String column_family, String startRowKey, String endRowKey) {
		Scan scan = null;
		ResultScanner rs = null;
		HTable hTable = null;
		try {
			//hbase的过滤器
			FilterList filterList = new FilterList();
			
			//通过查询RowKey的范围获取所有的记录数
			scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			//设置rowkey过滤
			scan.setFilter(filterList);
			//设置列簇
	    	scan.addFamily(Bytes.toBytes(column_family));
	    	//设置表名
	    	hTable = new HTable(HBASE_CONFIG, tableName);
	    	//查询Hbase数据库返回结果集
	    	rs = hTable.getScanner(scan);
	    	int row = 0;
			//循环结果
			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				row++;
				for (KeyValue keyValue : r.raw()) {
					System.out.println("列：" + new String(keyValue.getFamily())
							+ "====值:" + new String(keyValue.getValue(), "utf-8"));
				}
				if(row>100)break;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null)
				rs.close();
			if(hTable!=null)
				try {
					hTable.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * 通过RowKey范围, 过滤器来查询
	 * @param tableName 表名
	 * @param column_family 列簇
	 * @param startRowKey 开始的RowKey
	 * @param endRowKey 结束的RowKey
	 * @param filterList 过滤器
	 * */
	public StringBuffer findAllByFilterWithSN(String tableName, String column_family, String startRowKey
			, String endRowKey, FilterList filterList) {
		ResultScanner rs = null;
		HTable hTable = null;
		StringBuffer sb = new StringBuffer("");
		try{
			Scan scan = null;
			//通过查询RowKey的范围获取所有的记录数
			Filter filter = new FilterList(filterList);
			scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			//设置rowkey过滤
			scan.setFilter(filter);
			//设置列簇
	    	scan.addFamily(Bytes.toBytes(column_family));
	    	//设置表名
	    	hTable = new HTable(HBASE_CONFIG, tableName);
	    	//查询Hbase数据库返回结果集
	    	rs = hTable.getScanner(scan);
	    	for (Result r : rs) {
				for (KeyValue keyValue : r.raw()) {
					sb.append(new String(r.getRow()));
					sb.append(split_str);
					sb.append(Bytes.toString(keyValue.getValue()));
					sb.append("\r\n");
				}
			}
			rs.close();
			hTable.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null)
				rs.close();
			if(hTable!=null)
				try {
					hTable.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return sb;
	}
	
	/**
	 * 
	 * @description: 生成RowKey的方法
	 * @param telNumber
	 * @param time
	 * @return String
	 */
	private String generateRowKey(String telNumber, String time) {
		//手机号与22取模，然后得到一个长度为2的数，如果为个位数前面补零
		String partitionId = frontZeroFill((int)(Long.valueOf(telNumber) % PARTITION_NUM),2);
		//返回RowKey
		return partitionId + "," + telNumber + "," + time ;
	}
	public String generateRowKey(String telNumber, String time, int month) {
		//手机号与22取模，然后得到一个长度为2的数，如果为个位数前面补零 2014.03.31之前版本(对22取模),2014.04.01开始使用1024取模版本
//		String partitionId = CommonUtils.frontZeroFill((int)(Long.valueOf(telNumber) % Constants.PARTITION_NUM),2);
		String partitionId = "";
//		if(month<=3){//3月,3月之前
//			partitionId = frontZeroFill((int)(Long.valueOf(telNumber) % PARTITION_NUM),2);
//		}else{//4月,4月之后
			partitionId = generateModKey(Long.valueOf(telNumber));
//		}
		//返回RowKey
		return partitionId + "," + telNumber + "," + time ;
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
	
	/**
	 * 
	 * @Title: frontZeroFill 
	 * @Description: 数据前补零，补后数字的总长度为指定的长度，以字符串的形式返回
	 * @param @param src
	 * @param @param length 字符总长度
	 * @param @return   
	 * @return String    
	 * @throws
	 */
	public String frontZeroFill(int src,int length){
	  String dest = String.format("%0"+length+"d", src);
	  return  dest;
	 }
	
	/**
	 * 把传入的内容写入文件
	 * */
	public void writeDataByQuery(String filename, StringBuffer datas){
		FileWriter fw = null;
    	BufferedWriter bw = null;
    	String filenames = System.getProperty("user.dir");
		if("\\".equals(File.separator)){
			filenames = System.getProperty("user.dir")+"\\data\\"+filename+".txt";
		}else if("/".equals(File.separator)){
			filenames = System.getProperty("user.dir")+"/data/"+filename+".txt";
		}
		System.out.println("filenames:"+filenames);
		try {
			File f = new File(filenames);
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
	public Vector[] readDataByfile(String filename){
		Vector<String> data = new Vector<String>();
		Vector<String> row_key = new Vector<String>();
		Vector[] result = {row_key, data};
		FileReader fr = null;
    	BufferedReader br = null;
    	try {
        	String filenames = System.getProperty("user.dir");
    		if("\\".equals(File.separator)){
    			filenames = System.getProperty("user.dir")+"\\data\\"+filename+".txt";
    		}else if("/".equals(File.separator)){
    			filenames = System.getProperty("user.dir")+"/data/"+filename+".txt";
    		}
    		System.out.println("filenames:"+filenames);
			File file = new File(filenames);
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
	
	public static void main(String[] args) {
		try{			
			String telnumber = "";//号码
			String start_s = "";//开始时间
			String start_e = "";//结束时间
			String table_name = "";//表名
			String ip = "";//zookeeper ip
			String month = "";//月份
			if(args.length==5){
				telnumber = args[0];
				start_s = args[1];
				start_e = args[2];
				table_name = args[3];
				ip = args[4];
			}else{
				telnumber = "13509323824";
				start_s = "20140202000000";
				start_e = "20140209000000";
				table_name = "gn_cdr_2";
				ip = "10.1.4.54";
			}
			month = table_name.substring(7, table_name.length());
			HbasePut hp = new HbasePut(ip);
			
			//生成startRowKey，用开始时间生成
			String startRowKey = hp.generateRowKey(telnumber, start_s, Integer.valueOf(month));
			//生成endRowKey ，以开始时间加上1天后形成的时间生成
			String endRowKey = hp.generateRowKey(telnumber, start_e, Integer.valueOf(month));
			//hbase的过滤器
			FilterList filterList = new FilterList();

			//查询,导出txt
//			StringBuffer resultsb = hp.findAllByFilterWithSN(table_name, "info", startRowKey, endRowKey, filterList);
//			hp.writeDataByQuery(telnumber+"_"+start_s+"_"+start_e, resultsb);
			System.out.println("read txt begin");
			//读取txt,入库
			Vector[] result = hp.readDataByfile(telnumber+"_"+start_s+"_"+start_e);
			Vector rowkey = result[0];
//			System.out.println("deleteRow begin");
//			hp.deleteRow(table_name, rowkey);
			System.out.println("insertData begin");
			hp.insertData(table_name, result);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

