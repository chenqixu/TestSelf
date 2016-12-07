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
 * ���������ļ����������в�ѯ
 * �Ȳ�ѯ������
 * �ٸ�������������ѯ��ʵ��
 * */
public class HbaseQueryConf {
	// �����ļ�
	private Configuration conf = null;
	// ������ӳ�
	private HTablePool tablePool = null;
	// Pool��С
	private static final int POOL_SIZE = 5;
	
	public HbaseQueryConf(){
		conf = HBaseConfiguration.create();
	}
	
	/**
	 * ���������ļ�
	 * */
	public void initConf(String confpath){
		// ɨ��conf path�µ�����xml�����ļ�
		File cp = new File(confpath);
		if(cp.isDirectory()){
			for(File resource : cp.listFiles()){
				String _path = resource.getPath();
				if(_path.endsWith(".xml")){
					conf.addResource(new Path(_path));
				}
			}
			System.out.println("[conf]"+conf);
			// ����Hbase�����ӳ�
			tablePool = new HTablePool(conf, POOL_SIZE);
		}
	}
	
	/**
	 * �ͷ���Դ
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
	 * ��ѯ������
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
			// �����д�
	    	scan.addFamily(Bytes.toBytes("info"));
	    	// ���ñ���
	    	hTable = tablePool.getTable(Bytes.toBytes(tableName));
	    	// ��ѯHbase���ݿⷵ�ؽ����
	    	ResultScanner rs = hTable.getScanner(scan);
	    	for(Result r:rs){
				for (KeyValue keyValue : r.raw()) {
					resultlist.add(new Get(keyValue.getValue()));
					System.out.println("[index]"+Bytes.toString(keyValue.getValue()));
				}
	    	}
	    	// �رս�����ͱ�
	    	relase(rs, hTable, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultlist;
	}
	
	/**
	 * ����������ѯ���
	 * */
	public void queryFact(List<Get> querylist){
		HTableInterface hTable = null;
		try{
			String tableName = conf.get("QFACTTABLE");
			String family = conf.get("FAMILY");
			String qualifier = conf.get("QUALIFIER");
	    	// ���ñ���
	    	hTable = tablePool.getTable(Bytes.toBytes(tableName));
	    	String rowkey = "";
	    	// ����rowkey��ѯ
	    	Result[] r = hTable.get(querylist);
	    	for(Result r1 : r){
	    		System.out.println("[query result]"+new String(r1.getValue(family.getBytes(),qualifier.getBytes()),"utf-8"));
	    	}
	    	// �رս�����ͱ�
	    	r = null;
	    	relase(null, hTable, null);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		HbaseQueryConf hqc = new HbaseQueryConf();
		String local_conf_path = "";
		// �����ļ�
		if(args.length==1){
			local_conf_path = args[0];
			// ��ʼ������
			hqc.initConf(local_conf_path);
			// ������������ʵ����в�ѯ
			hqc.queryFact(hqc.queryIndex());
		}else{
			System.out.println("û�������ļ����˳���");
			System.exit(-1);
		}		
	}
}
