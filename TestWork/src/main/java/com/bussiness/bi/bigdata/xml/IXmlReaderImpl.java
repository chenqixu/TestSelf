package com.bussiness.bi.bigdata.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class IXmlReaderImpl implements IXmlReader {

	// 初始化param对象中的xslt参数KEY
	public static String PARAM_XSLT = "xslt";
	// 初始化param对象中的解析名称的KEY
	public static String PARAM_NAME = "xsltName";

	// 解析结果map
	private Map<String, BufferedReader> peser_result = null;
	// 原始xml数据文件缓存
	MemoryCacheFile cacheFile = null;
	// 传入参数
	List<Map<String, String>> xslt_list = null;

	/**
	 * 初始化
	 * */
	@Override
	public void init(List<Map<String, String>> param) {
		this.xslt_list = param;
	}

	/**
	 * 加载原始xml数据文件
	 * 
	 * @throws IOException
	 */
	@Override
	public void load(InputStream inputStream) throws IOException {
		// 解析结果map初始化
		this.peser_result = new HashMap<String, BufferedReader>();
		// 构建原始xml数据文件缓存
		cacheFile = new MemoryCacheFile(inputStream);
		// 循环参数，根据XSLT解析XML数据文件
		for (Map<String, String> param : this.xslt_list) {
			// 解析获得BufferedReader
			BufferedReader reader = transformXmlByXslt(cacheFile.getInputStream(), param.get(PARAM_XSLT));
			// 如果传入的名称无法获取到，就抛出运行时异常
			if (param.get(PARAM_NAME)==null)
				throw new RuntimeException("传入的名称无法获取到,PARAM_NAME is null.");
			// 存入解析结果map
			this.peser_result.put(param.get(PARAM_NAME), reader);
		}
		// 释放原始xml数据文件输入流
		this.cacheFile.close();
		this.cacheFile = null;
	}
	
	/**
	 * 返回解析好的结果内容，这里随机给出数据
	 * 
	 * @param limit 输出行数
	 */
	@Override
	public IXmlBatchMsgs getBatch(int limit) {
		IXmlBatchMsgs batchResult = null;
		// 循环解析结果map
		for (Map.Entry<String, BufferedReader> peser : this.peser_result.entrySet()) {
			try {
				List<String> tmplist = new ArrayList<String>();
				String str = null;
				int count = 0;
				// 获得解析结果
				BufferedReader reader = peser.getValue();
				// 根据传入的行数，循环读取解析结果，写入到返回list
				while (reader != null && limit > 0 && (str = reader.readLine()) != null) {
					tmplist.add(str);
					count++;
					if (count == limit)
						break;
				}
				// 三种情况
				// 1、文件读取完成，tmplist有值，返回
				// 2、文件读取完成，tmplist没有值，继续下个循环
				// 3、文件没有读取完成，返回
				if ((str==null && tmplist.size() > 0) || (str!=null)) {
					// 设置返回值
					batchResult = new IXmlBatchMsgs();
					batchResult.setDateType(peser.getKey());
					batchResult.setRecords(tmplist);
					// 跳出循环
					break;
				} else if (str==null && tmplist.size()==0) {
					// 继续下个循环
				} else {
					// 异常情况
					throw new IOException("异常情况，无法返回");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return batchResult;
	}

	/**
	 * 释放对象，标记为NULL，当内存不够进行GC的时候才真正释放
	 * */
	@Override
	public void clear() {
		// 关闭解析结果map中的reader
		for (Map.Entry<String, BufferedReader> peser : this.peser_result.entrySet()) {
			BufferedReader reader = peser.getValue();
			if (reader!=null) {
				try {
					reader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 释放解析结果map对象
		this.peser_result = null;
		// 释放初始化param对象
		this.xslt_list = null;

	}

	/* ====================工具类==================== */

	/**
	 * 使用XSLT转换XML文件
	 * 
	 * @param srcXml
	 *            源XML文件
	 * @param xslt
	 *            解析XSLT文件内容
	 */
	private BufferedReader transformXmlByXslt(InputStream inputStream, String xslt) {
		BufferedReader reader = null;
		InputStream ips = null;
		ByteArrayOutputStream ops = null;
		// 判断源文件是否有内容 & 判断样式文件有值
		if (inputStream != null && xslt != null && !xslt.trim().equals("")) {
			ops = new ByteArrayOutputStream();
			// SAXON XSLT2.0
			System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
			// 获取转换器工厂
			TransformerFactory tf = TransformerFactory.newInstance();
			try {
				// 获取转换器对象实例
				Transformer transformer = tf.newTransformer(new StreamSource(StringToInputStream(xslt)));
				// 进行转换
				transformer.transform(new StreamSource(inputStream), new StreamResult(ops));
				// 输出流转输入流
				ips = new ByteArrayInputStream(ops.toByteArray());
				// 输入流转reader
				reader = new BufferedReader(new InputStreamReader(ips));
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} finally {
				// 最后关闭输入流和输出流
				if (ops != null) {
					try {
						ops.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            	if (ips!=null) {
            		try {
            			ips.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            }
    	}
        return reader;
    }
    
    /**
     * 将字符串转为输入流
     * */
	private InputStream StringToInputStream(String param){
		InputStream tInputStringStream = null;
		// 判断字符是否有值
		if (param!=null && !param.trim().equals("")) {
			try {
				// 将字符串转为输入流
				tInputStringStream = new ByteArrayInputStream(param.getBytes());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return tInputStringStream;
	}
}
