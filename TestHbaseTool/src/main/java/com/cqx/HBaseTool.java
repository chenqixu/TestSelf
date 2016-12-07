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
	// �����ļ�
	private Configuration conf = null;
	// ������ӳ�
	private HTablePool tablePool = null;
	// ��ӿ�
	private HTableInterface hTable = null;
	// Pool��С
	private static final int POOL_SIZE = 5;
	// ����
	private static final int REGION_PARTITION_NUM = 1024;
//	private static final int REGION_PARTITION_NUM_OLD = 200;
	// ����ȡģ
	private Random random = new Random();
	// �ָ���
	public final String split_str = ""+((char)((int)01));
	
	// ��ʼ�������������ļ�
	public HBaseTool(){
		conf = HBaseConfiguration.create();
//		// ���������ļ�
//		conf.addResource(new Path("/etc/hbase/conf/hbase-site.xml"));
//		conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
//		conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
	}
	
	/**
	 * ���������ļ�
	 * */
	public void initConf(HbaseInputBean hib){
		String confpath = hib.getConfpath();
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
	 * ���� rowkeyɾ��һ����¼
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
			System.out.println("ɾ���гɹ�!");
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
	 * ��������
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
			//������rowkey��ʼ��
			Put put = new Put(rowkey.get(i).toString().getBytes());// һ��PUT����һ�����ݣ���NEWһ��PUT��ʾ�ڶ�������,ÿ��һ��Ψһ��ROWKEY���˴�rowkeyΪput���췽���д����ֵ
//			String iso = "";
			try {
				//��������������,��Ҫת��utf-8,��utf-8������3���ֽ�,��������������ͻ��쳣,����Ҫ��תiso-8859-1
//				iso = new String(data.get(i).toString().getBytes("UTF-8"),"ISO-8859-1");
//				put.add("info".getBytes(), "gn".getBytes(), iso.getBytes("ISO-8859-1"));//�������ݵĵ�һ��
				//put��3������:family,qualifier,value
				put.add("info".getBytes(), "gn".getBytes(), Bytes.toBytes(data.get(i).toString()));//�������ݵĵ�һ��
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
	 * ��ѯ
	 * */
	private StringBuffer queryData(String tableName, String startRowKey, String endRowKey){
		StringBuffer sb = new StringBuffer("");
		HTableInterface hTable = null;
		System.out.println("[tableName]"+tableName+"[startRowKey]"
				+startRowKey+"[endRowKey]"+endRowKey);
		try{
			Scan scan = null;
			// ͨ����ѯRowKey�ķ�Χ��ȡ���еļ�¼��
			//hbase�Ĺ�����
//			FilterList filterList = new FilterList();
//			Filter filter = new FilterList(filterList);
			scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			// ����rowkey����
//			scan.setFilter(filterList);
			// �����д�
	    	scan.addFamily(Bytes.toBytes("info"));
	    	// ���ñ���
	    	hTable = tablePool.getTable(Bytes.toBytes(tableName));
	    	// ��ѯHbase���ݿⷵ�ؽ����
	    	ResultScanner rs = hTable.getScanner(scan);
	    	for(Result r:rs){
				for (KeyValue keyValue : r.raw()) {
					sb.append(new String(r.getRow()));
					sb.append(split_str);
					sb.append(Bytes.toString(keyValue.getValue()));
					sb.append("\r\n");
				}
	    	}
	    	// �رս�����ͱ�
	    	relase(rs, hTable, null);
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb;
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
	 * ������رյ�ʱ���ͷű�����ӳ���Դ
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
	 * @description: ����RowKey�ķ���
	 */
	public String generateRowKey(String telNumber, String time) {
		//�ֻ�����22ȡģ,��ʼʹ��1024ȡģ�汾
		String partitionId = "";
		partitionId = generateModKey(Long.valueOf(telNumber));
		//����RowKey
		return partitionId + "," + telNumber + "," + time ;
	}
	
	/**
	 * @description: �Ժ���ȡģ
	 * */
	private String generateModKey(long telnumber){
		long tempLong;
		try{
			// �����������
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
	 * �Ѵ��������д���ļ�
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
	 * ͨ���ļ������ݶ�ȡ��Vector
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
	 * �����ļ�ɾ����¼
	 * */
	public void deleteByFile(HbaseInputBean hib){
		String filenamepath = hib.getFilepath();
		String table_name = hib.getTableName();
		// ��ȡtxt
		Vector<?>[] result = readDataByfile(filenamepath);
		Vector<?> rowkey = result[0];
		// ����rowkeyɾ����Ӧ��¼
		deleteRow(table_name, rowkey);
	}
	
	/**
	 * ��ѯ
	 * */
	public void query(HbaseInputBean hib){
		String table_name = hib.getTableName();
		String telnumber = hib.getMsisdn();
		String starttime_s = hib.getStarttime_s();
		String starttime_e = hib.getStarttime_e();
		// ����startRowKey���ÿ�ʼʱ������
		String startRowKey = generateRowKey(telnumber, starttime_s);
		// ����endRowKey ���ý���ʱ������
		String endRowKey = generateRowKey(telnumber, starttime_e);
		// ��ѯ�����
		StringBuffer sb = queryData(table_name, startRowKey, endRowKey);
		System.out.println(sb.toString());
	}
		
	/**
	 * ���浽����
	 * */
	public void save(HbaseInputBean hib){
		String filenamepath = hib.getFilepath();
		String table_name = hib.getTableName();
		String telnumber = hib.getMsisdn();
		String starttime_s = hib.getStarttime_s();
		String starttime_e = hib.getStarttime_e();
		// ����startRowKey���ÿ�ʼʱ������
		String startRowKey = generateRowKey(telnumber, starttime_s);
		// ����endRowKey ���ý���ʱ������
		String endRowKey = generateRowKey(telnumber, starttime_e);
		// ��ѯ�����
		StringBuffer sb = queryData(table_name, startRowKey, endRowKey);
		// �ѽ��д���ļ�
		writeDataByQuery(filenamepath, sb);
	}
	
//	/**
//	 * �������м�¼������
//	 * */
//	public void saveAll(String filenamepath, String table_name){
//		for(int i=0;i<1024;i++){
//			
//		}
//		// ����startRowKey���ÿ�ʼʱ������
//		String startRowKey = "0000,";
//		// ����endRowKey ���ý���ʱ������
//		String endRowKey = "1023,";
//		// ��ѯ�����
//		StringBuffer sb = queryData(table_name, startRowKey, endRowKey);
//		// �ѽ��д���ļ�
//		writeDataByQuery(filenamepath, sb);
//	}
	
	/**
	 * ���ص�HBase
	 * */
	public void load(HbaseInputBean hib){
		String filenamepath = hib.getFilepath();
		String table_name = hib.getTableName();
		// ��ȡtxt
		Vector<?>[] result = readDataByfile(filenamepath);
		// �������ݵ�HBase
		insertData(table_name, result);
	}
	
	/**
	 * ��������
	 * */
	public HbaseInputBean parseArgs(String[] args){
		// �������
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
		// ��������
		initConf(hib);
		return hib;
	}
	
	/**
	 * ʹ��bulkload��ʽ��������
	 * */
	public boolean BulkLoad(HbaseInputBean hib){
		// ��ȡ�������п�ʼʱ��
		long starttime = System.currentTimeMillis();
		try {
			HTable table = new HTable(conf, hib.getTableName());			
			StringBuilder compressionConfigValue = new StringBuilder();
			HTableDescriptor tableDescriptor = table.getTableDescriptor();
			// �жϱ������Ƿ�Ϊ�գ�Ϊ�����˳�
			if (tableDescriptor == null) {
				System.out.println("�������Ƿ�Ϊ��");
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
		
		// ��hfile���뵽hbase��
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
		
		// �ϲ��ļ�����
		try{
			HBaseAdmin admin = new HBaseAdmin(conf);
			admin.compact(tbName);
			admin.close();
			System.out.println("minor compact for " + tbName);
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("minor compact for " + tbName + " failed.");
		}

//		// �������ɾ������ļ��Լ������ļ�
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
//			System.out.println("�������ɾ������ļ��Լ������ļ�.....");
//		}
		return true;
	}
}
