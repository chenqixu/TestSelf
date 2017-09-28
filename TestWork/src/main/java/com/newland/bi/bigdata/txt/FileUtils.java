package com.newland.bi.bigdata.txt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import com.newland.bi.bigdata.xml.IXmlReader;
import com.newland.bi.bigdata.xml.IXmlReaderImpl;

public class FileUtils {
	public static BufferedReader reader = null;
	private static Map<String, BufferedReader> peser_result = null;
	public static InputStream StringToInputStream(String param){
		InputStream tInputStringStream = null;
		if(param!=null && !param.trim().equals("")){
			try{
				tInputStringStream = new ByteArrayInputStream(param.getBytes());
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
		return tInputStringStream;
	}
	
	public static void getBatch(OutputStream ops, int num){
		try{
			InputStream ips = new ByteArrayInputStream(((ByteArrayOutputStream)ops).toByteArray());
			BufferedReader reader = new BufferedReader(new InputStreamReader(ips));
			String str = "";
			int count = 0;
			while((str=reader.readLine())!=null){
				System.out.println(str);
				count++;
				if(count==num)break;
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void getBatch(BufferedReader _reader, int num){
		try{
			String str = "";
			int count = 0;
			while(_reader!=null && num>0 && (str=_reader.readLine())!=null){
				System.out.println(str);
				count++;
				if(count==num)break;
			}
			System.out.println("[read end]"+str);
			if(str==null && _reader!=null){
				System.out.println("[close]"+str+"[reader]"+_reader);
				_reader.close();
				System.out.println("[close]"+str+"[reader]"+_reader);
				_reader = null;
				System.out.println("[close]"+str+"[reader]"+_reader);
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static void transformXmlByXslt(File srcXml, String xslt) {
		if(srcXml!=null && srcXml.length()>0 && xslt!=null && !xslt.trim().equals("")){
			System.out.println("ok");
	    }
	 }
	
	public static void test(){
		peser_result = new HashMap<String, BufferedReader>();
		peser_result.put("a", new BufferedReader(new InputStreamReader(new ByteArrayInputStream("123".getBytes()))));
		peser_result.put("b", new BufferedReader(new InputStreamReader(new ByteArrayInputStream("456".getBytes()))));
		peser_result.put("c", new BufferedReader(new InputStreamReader(new ByteArrayInputStream("789".getBytes()))));
	}
	
	public static void close(){
		for(Map.Entry<String, BufferedReader> peser : peser_result.entrySet()){
			BufferedReader reader = peser.getValue();
			if(reader!=null){
				try{
					reader.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		peser_result = null;
	}
	
	public static void main(String[] args) {
//		test();
//		close();
//		for(Map.Entry<String, BufferedReader> peser : peser_result.entrySet()){
//			BufferedReader reader = peser.getValue();
//			System.out.println(reader);
//		}
//		transformXmlByXslt(new File("d:/2.txt"), null);
//		try{
//			OutputStream os = new ByteArrayOutputStream();
//			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
//			for(int i=0;i<20;i++){
//				bw.write("test"+i);
//				bw.newLine();
//			}
//			bw.flush();
//			os.flush();
//			InputStream ips = new ByteArrayInputStream(((ByteArrayOutputStream)os).toByteArray());
//			reader = new BufferedReader(new InputStreamReader(ips));
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			getBatch(reader, 5);
//			bw.close();
//			os.close();
//			ips.close();
////			reader.close();
//		}catch(IOException ex){
//			ex.printStackTrace();
//		}
		// 参数
		Map<String, Map<String, String>> param = new HashMap<String, Map<String, String>>();
		Map<String, String> xsltparam1 = new HashMap<String, String>();
		xsltparam1.put(IXmlReaderImpl.PARAM_XSLT, "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+System.getProperty("line.separator")+
				"<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"+System.getProperty("line.separator")+
				"<xsl:output method=\"text\" encoding=\"utf-8\"/>"+System.getProperty("line.separator")+
				"<xsl:variable name=\"filename\" select=\"document-uri(/)\"/>"+System.getProperty("line.separator")+
				"<xsl:variable name=\"filename2\" select=\"substring-after($filename, '_')\" />"+System.getProperty("line.separator")+
				"<xsl:variable name=\"filename3\" select=\"substring-after($filename2, '_')\" />"+System.getProperty("line.separator")+
				"<xsl:variable name=\"equipment\" select=\"substring-before($filename3, '_')\" />"+System.getProperty("line.separator")+
				"<xsl:template match=\"/\">"+System.getProperty("line.separator")+
				"<xsl:for-each select=\"bulkPmMrDataFile/eNB\">"+System.getProperty("line.separator")+
				"<xsl:variable name=\"enbid\" select =\"@id\"/>"+System.getProperty("line.separator")+
				"<xsl:for-each select=\"measurement[smr='MR.LteScRIP']\">"+System.getProperty("line.separator")+
				"<xsl:for-each select=\"object/v\">"+System.getProperty("line.separator")+
				"<xsl:value-of select=\"$enbid\"></xsl:value-of>|<xsl:value-of select=\"../../../../fileHeader/@reportTime\"></xsl:value-of>|<xsl:value-of select=\"../../../../fileHeader/@startTime\"></xsl:value-of>|<xsl:value-of select=\"../../../../fileHeader/@endTime\"></xsl:value-of>|<xsl:value-of select=\"../@id\"></xsl:value-of>|<xsl:value-of select=\"../@MmeUeS1apId\"></xsl:value-of>|<xsl:value-of select=\"../@MmeGroupId\"></xsl:value-of>|<xsl:value-of select=\"../@MmeCode\"></xsl:value-of>|<xsl:value-of select=\"../@TimeStamp\"></xsl:value-of>|<xsl:value-of select=\"$equipment\"></xsl:value-of>|<xsl:value-of select=\"translate(.,' ','|')\"></xsl:value-of>|"+System.getProperty("line.separator")+
				"</xsl:for-each>"+System.getProperty("line.separator")+
				"</xsl:for-each>"+System.getProperty("line.separator")+
				"</xsl:for-each>"+System.getProperty("line.separator")+
				"</xsl:template>"+System.getProperty("line.separator")+
				"</xsl:stylesheet>");
		param.put("x1", xsltparam1);
		Map<String, String> xsltparam2 = new HashMap<String, String>();
		xsltparam2.put(IXmlReaderImpl.PARAM_XSLT, "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+System.getProperty("line.separator")+
				"<xsl:stylesheet version=\"2.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"+System.getProperty("line.separator")+
				"<xsl:output method=\"text\" encoding=\"utf-8\"/>"+System.getProperty("line.separator")+
				"<xsl:variable name=\"filename\" select=\"document-uri(/)\"/>"+System.getProperty("line.separator")+
				"<xsl:variable name=\"filename2\" select=\"substring-after($filename, '_')\" />"+System.getProperty("line.separator")+
				"<xsl:variable name=\"filename3\" select=\"substring-after($filename2, '_')\" />"+System.getProperty("line.separator")+
				"<xsl:variable name=\"equipment\" select=\"substring-before($filename3, '_')\" />"+System.getProperty("line.separator")+
				"<xsl:template match=\"/\">"+System.getProperty("line.separator")+
				"<xsl:for-each select=\"bulkPmMrDataFile/eNB\">"+System.getProperty("line.separator")+
				"<xsl:variable name=\"enbid\" select =\"@id\"/>"+System.getProperty("line.separator")+
				"<xsl:for-each select=\"measurement[smr='MR.LteScPlrULQci1 MR.LteScPlrULQci2 MR.LteScPlrULQci3 MR.LteScPlrULQci4 MR.LteScPlrULQci5 MR.LteScPlrULQci6 MR.LteScPlrULQci7 MR.LteScPlrULQci8 MR.LteScPlrULQci9 MR.LteScPlrDLQci1 MR.LteScPlrDLQci2 MR.LteScPlrDLQci3 MR.LteScPlrDLQci4 MR.LteScPlrDLQci5 MR.LteScPlrDLQci6 MR.LteScPlrDLQci7 MR.LteScPlrDLQci8 MR.LteScPlrDLQci9']\">"+System.getProperty("line.separator")+
				"<xsl:for-each select=\"object/v\">"+System.getProperty("line.separator")+
				"<xsl:value-of select=\"$enbid\"></xsl:value-of>|<xsl:value-of select=\"../../../../fileHeader/@reportTime\"></xsl:value-of>|<xsl:value-of select=\"../../../../fileHeader/@startTime\"></xsl:value-of>|<xsl:value-of select=\"../../../../fileHeader/@endTime\"></xsl:value-of>|<xsl:value-of select=\"../@id\"></xsl:value-of>|<xsl:value-of select=\"../@MmeUeS1apId\"></xsl:value-of>|<xsl:value-of select=\"../@MmeGroupId\"></xsl:value-of>|<xsl:value-of select=\"../@MmeCode\"></xsl:value-of>|<xsl:value-of select=\"../@TimeStamp\"></xsl:value-of>|<xsl:value-of select=\"$equipment\"></xsl:value-of>|<xsl:value-of select=\"translate(.,' ','|')\"></xsl:value-of>|"+System.getProperty("line.separator")+
				"</xsl:for-each>"+System.getProperty("line.separator")+
				"</xsl:for-each>"+System.getProperty("line.separator")+
				"</xsl:for-each>"+System.getProperty("line.separator")+
				"</xsl:template>"+System.getProperty("line.separator")+
				"</xsl:stylesheet>");
		param.put("x2", xsltparam2);
		// 数据文件
		File xmlFile = new File("j:/Work/海南/海南MRO/data/TD-LTE_MRO_HUAWEI_010199146040_475829_20170707121500.xml");
		// 读取行数
		int limit = 1000;
		
		// 初始化解析接口
		IXmlReader ixri = new IXmlReaderImpl();
		// 传参
		ixri.init(param);
		// 加载数据文件
		ixri.load(xmlFile);
		// 输出解析后的内容
		System.out.println(ixri.getBatch(limit).get("x1"));
		System.out.println(ixri.getBatch(limit).get("x2"));
		System.out.println(ixri.getBatch(limit).get("x1"));
		System.out.println(ixri.getBatch(limit).get("x2"));
		System.out.println(ixri.getBatch(limit).get("x1"));
		System.out.println(ixri.getBatch(limit).get("x2"));
		// 释放对象
		ixri.clear();
	}
}
