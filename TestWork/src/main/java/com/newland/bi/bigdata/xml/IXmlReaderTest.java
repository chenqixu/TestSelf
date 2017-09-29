package com.newland.bi.bigdata.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class IXmlReaderTest {	
	public static void main(String[] args) throws Exception {
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
//		Reader reader = new FileReader(xmlFile);
		// 读取行数
		int limit = 1000;
		
		// 初始化解析接口
		IXmlReader ixri = new IXmlReaderImpl();
		// 传参
		ixri.init(param);
		// 加载数据文件
		ixri.load(xmlFile);
//		ixri.load(reader);
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
