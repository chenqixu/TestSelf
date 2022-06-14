package com.bussiness.bi.bigdata.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Xml解析接口
 * */
public interface IXmlReader {
	
	public void init(List<Map<String,String>> param);

	void load(InputStream inputStream) throws IOException;
	
	public IXmlBatchMsgs getBatch(int limit);
	
	public void clear();
}
