package com.newland.bi.bigdata.xml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlUtil {

	/** 
     * 使用XSLT转换XML文件 
     * @param srcXml    源XML文件路径 
     * @param dstXml    目标XML文件路径 
     * @param xslt      XSLT文件路径 
     */
    public static void transformXmlByXslt(String srcXml, String dstXml, String xslt) {        
        // 获取转换器工厂
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            // 获取转换器对象实例
            Transformer transformer = tf.newTransformer(new StreamSource(xslt));
            // 进行转换
            transformer.transform(new StreamSource(srcXml),
                    new StreamResult(new FileOutputStream(dstXml)));
//            transformer.transform(new StreamSource(srcXml), new StreamResult(System.out));/
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    
    public static void pes(String srcXml, String dstXml, String xslt){
    	Date date1 = new Date();
    	System.out.println("["+date1+"]transformXmlByXslt start...");
    	transformXmlByXslt(srcXml, dstXml, xslt);
    	Date date2 = new Date();
    	long syn = date2.getTime()-date1.getTime();
    	System.out.println("["+date2+"]transformXmlByXslt success. it cost "+syn/1000+" seconds.");
    }
    
    public static void main(String[] args) {
    	// SAXON XSLT2.0
    	System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
    	
    	String srcXml = "file:///j:/Work/海南/海南MRO/data/TD-LTE_MRO_HUAWEI_010199146040_475829_20170707121500.xml";
//    	srcXml = "j:/Work/海南/海南MRO/data/TD1.xml";
    	String dstXml = "j:/Work/海南/海南MRO/data/LteScRIP";
    	String xslt = "file:///j:/Work/海南/海南MRO/data/LteScRIP.xslt";
//    	pes(srcXml, dstXml, xslt);
    	dstXml = "j:/Work/海南/海南MRO/data/LteScPlrULQci1";
    	xslt = "file:///j:/Work/海南/海南MRO/data/LteScPlrULQci1.xslt";
//    	pes(srcXml, dstXml, xslt);
    	dstXml = "j:/Work/海南/海南MRO/data/LteScRSRP";
    	xslt = "file:///j:/Work/海南/海南MRO/data/LteScRSRP.xslt";
//    	pes(srcXml, dstXml, xslt);
    	dstXml = "j:/Work/海南/海南MRO/data/output1";
    	xslt = "file:///j:/Work/海南/海南MRO/data/ALL.xslt";
//    	pes(srcXml, dstXml, xslt);
    	srcXml = "file:///j:/Work/ETL/xml解析/data/ex.xml";
    	dstXml = "j:/Work/ETL/xml解析/data/ex";
    	xslt = "file:///j:/Work/ETL/xml解析/data/ex.xslt";
//    	pes(srcXml, dstXml, xslt);
    	srcXml = "file:///j:/Work/海南/海南MRO/data/TD-LTE_MRO_HUAWEI_010199146040.xml";
    	dstXml = "file:///j:/Work/海南/海南MRO/data/test1";
    	xslt = "file:///j:/Work/海南/海南MRO/data/test1.xslt";
//    	pes(srcXml, dstXml, xslt);
	}
}
