package com.bussiness.bi.bigdata.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Jom4jForXmltoXslt {
	private static String str1 = "j:/Work/ETL/xml解析/data/";
	private static String str2 = "j:/Work/海南/海南MRO/data/";
	private static String str = str2;
	
	/**
	 * 利用xslt文件转换xml
	 * 
	 * @author Administrator
	 * @parame document,styleSheet
	 */
	public static Document transformDocument(Document document, File styleSheet)
			throws TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(
				styleSheet));

		DocumentSource source = new DocumentSource(document);
		DocumentResult result = new DocumentResult();
		transformer.transform(source, result);
		
		Document transformedDoc = result.getDocument();

		return transformedDoc;
	}

	/**
	 * 读取需要转换的xml文件
	 * 
	 * @return Document
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public Document getRoleXML() throws UnsupportedEncodingException,
			FileNotFoundException {
		InputStream is = null;
//		URL url = this.getClass().getClassLoader().getResource("resources.xml");
//		if (url == null)
//			return null;
//		String path = url.getFile();
		String path = str+"test1.xml";
		path = URLDecoder.decode(path, "UTF-8");
		is = new FileInputStream(new File(path));
		System.out.println("读到的menuConfig.xml位置在：" + path);
		SAXReader reader = new SAXReader();

		try {
			return reader.read(is);
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String args[]) throws IOException {
		try {
			Document doc = new Jom4jForXmltoXslt().getRoleXML();
			Document resultDoc = Jom4jForXmltoXslt.transformDocument(doc,
					new File(str+"test1.xslt"));

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");

			XMLWriter writer = new XMLWriter(new FileWriter(new File(
					str+"output")), format);
			writer.write(resultDoc);
			writer.close();
			System.out.println("用xslt转换xml文件成功!");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
