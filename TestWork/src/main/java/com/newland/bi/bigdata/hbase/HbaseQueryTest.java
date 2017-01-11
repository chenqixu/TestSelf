package com.newland.bi.bigdata.hbase;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;


public class HbaseQueryTest {
	// 常量
	public static final int POOL_SIZE = 15;
	public static final long REGION_PARTITION_NUM = 1024;
	public static final String COLUMN_FAMILY = "info";
	public static final String TABLE_NAME = "qry_netlog";
	private static Random random = new Random();//用于取模
	
	private static Configuration conf = null;  
	private static HTablePool tablePool = null;
	private static HTableInterface hTable = null;
	static {
	    conf = HBaseConfiguration.create();
	    // 以下4个配置设置仅供开发测试
		conf.set("hbase.zookeeper.quorum", "10.1.8.1,10.1.8.2,10.1.8.3"); //设置zookeeper地址
		conf.set("hbase.zookeeper.property.clientPort", "2181"); //设置zookeeper端口
		conf.set("hbase.regionserver.lease.period", "1000");
		conf.set("hbase.client.scanner.timeout.period", "1000");
	    //创建Hbase的连接池
	    tablePool = new HTablePool(conf, POOL_SIZE);
	}
	
	/**
	  * 查询处理(使用hbase api，传入条件，返回结果)
	  */
	private List<CdrTableEntity> findUserListByFilterWithSN(String startRowKey,
				String endRowKey, FilterList filterList, String tableName, NgReqBean requestBean)
				throws IOException {
		Scan scan = null;
		//通过查询RowKey的范围获取所有的记录数
		scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
		if(filterList.getFilters().size()>0){
			Filter filter = new FilterList(filterList);
			// 设置rowkey过滤
			scan.setFilter(filterList);
			
		}
		//设置列簇
	  	scan.addFamily(Bytes.toBytes(COLUMN_FAMILY));
//	  	//设置最大记录数，没有作用
//	  	scan.setMaxResultSize(100);
	  	//设置表名
	  	hTable = tablePool.getTable(Bytes.toBytes(tableName));
	  	ResultScanner rs = hTable.getScanner(scan);
	  	List<CdrTableEntity> list  = new ArrayList<CdrTableEntity>();
	  	//循环取迭代器查询结果
	  	for(Result r:rs){
	  		CdrTableEntity entity = mappingToEntity(r);
	   		 if(null == entity){
	   			 continue;
	   		 }
	   		//将查询结果转为相应对象然后存入List
	   		list.add(entity);
	  	}
	  	//关闭结果集
	  	rs.close();
		return list;
	}
	
	/**
	  * 结果处理（循环对结果进行处理，截取value）
	  */
	private CdrTableEntity mappingToEntity(Result rs) {
		// 获取rowkey
		String rowKey = new String(rs.getRow());
		// 如果rowkey为空就返回
		if(StringUtils.isEmpty(rowKey))
			return null;
		// 返回bean
		CdrTableEntity entity = new CdrTableEntity();
		entity.setRowKey(rowKey);	
		for (KeyValue kv : rs.raw()) {
			// 列名
			String qualifier = new String(kv.getQualifier());
			// 列值
			String value = Bytes.toString(kv.getValue());
			// ...可以在这里对列值进行自定义处理
			// ...设置列值到bean
			entity.setValue(value);
			System.out.println("[value]"+value);
		}
		// 返回bean
		return entity;
	}
	
	/**
	  *  拼接查询条件，进行查询
	  */
	public List<CdrTableEntity> queryCdrDetailInfos(NgReqBean requestBean) throws IOException, ParseException{
		//获取查询的月份，通过月份确定表名
		int month = Integer.valueOf(requestBean.getStarttime_s().toString().substring(4,6));
		List<CdrTableEntity> cdrOriginalInfs = null;
		//生成startRowKey，用开始时间生成
		String startRowKey = generateRowKey(requestBean,requestBean.getStarttime_s()+"",REGION_PARTITION_NUM);
		//生成endRowKey ，用结束时间生成
		String endRowKey = generateRowKey(requestBean,requestBean.getStarttime_e()+"",REGION_PARTITION_NUM);
		//查询Hbase数据库中的表名
		String tableName = TABLE_NAME + "_" + month;
		//hbase的过滤器
		FilterList filterList = new FilterList();
//		//分页
//		PageFilter pf;		
		// 被叫号码的过滤
		if(!StringUtils.isEmpty(requestBean.getCall_ed())){
			String filterStr = ",B"+requestBean.getCall_ed();
				RowFilter rowFilter = new RowFilter(  
				        CompareFilter.CompareOp.EQUAL, new SubstringComparator(filterStr)); 
				filterList.addFilter(rowFilter);
		}
		//通过Dao查询获得结果List<CdrTableEntity>
		cdrOriginalInfs = findUserListByFilterWithSN(startRowKey, endRowKey, filterList, tableName, requestBean);
		return cdrOriginalInfs;
	}
	
	/**
	 * 
	 * 生成RowKey
	 */
	private String generateRowKey(NgReqBean requestBean, String time, long modNUM) {
		//手机号取模
		String partitionId = "";
		partitionId = generateModKey(Long.valueOf(requestBean.getTelnumber()), modNUM);
		//返回RowKey
		return partitionId + "," + requestBean.getTelnumber() + "," + time ;
	}

	/**
	 * 对号码取模
	 * */
	private String generateModKey(long telnumber, long modNum){
		try{
			random.setSeed(telnumber);
			long result = Math.abs(random.nextLong() % modNum);
			String resStr = result + "";//转换成String，统计结果的位数
			String modStr = modNum + "";//装成String，统计模的位数
			int resDigit = resStr.length();
			int modDigit = modStr.length();
			for(int i = 0;i< modDigit - resDigit;i++){
				resStr= "0" + resStr;
			}
			return resStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 测试
	 * */
	public static void main(String[] args) {
		HbaseQueryTest hqt = new HbaseQueryTest();
		NgReqBean request = new NgReqBean();
		request.setTelnumber("13509323824");
		request.setStarttime_s("20160401000026");
		request.setStarttime_e("20160401000718");
		try {
			hqt.queryCdrDetailInfos(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class CdrTableEntity {
	String rowKey = "";
	String value = "";
	public String getRowKey() {
		return rowKey;
	}
	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}

class NgReqBean {
	String starttime_s = "";
	String starttime_e = "";
	String telnumber = "";
	String call_ed = "";
	public String getStarttime_s() {
		return starttime_s;
	}
	public void setStarttime_s(String starttime_s) {
		this.starttime_s = starttime_s;
	}
	public String getStarttime_e() {
		return starttime_e;
	}
	public void setStarttime_e(String starttime_e) {
		this.starttime_e = starttime_e;
	}
	public String getTelnumber() {
		return telnumber;
	}
	public void setTelnumber(String telnumber) {
		this.telnumber = telnumber;
	}
	public String getCall_ed() {
		return call_ed;
	}
	public void setCall_ed(String call_ed) {
		this.call_ed = call_ed;
	}
}
