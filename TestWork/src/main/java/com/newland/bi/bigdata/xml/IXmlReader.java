package com.newland.bi.bigdata.xml;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Xml解析接口
 * */
public interface IXmlReader {
	public void init(Map<String,Map<String,String>> param);

	public void load(File xmlFile);
	/**
	 * 
	 * @param limit
	 * @return
	 */
	public Map<String,List<String>> getBatch(int limit);
	
	public void clear();
}
