package com.newland.bi.bigdata.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryCacheFile {
	private Logger logger = LoggerFactory.getLogger(MemoryCacheFile.class);
	/**
	 * 将InputStream中的字节读取到byte数组，然后输出
	 */
	private byte[] fileBytes = null;

	private List<InputStream> returnedStream; // 外部返回的stream。close时统一关闭

	/**
	 * 构造一个cache
	 * 
	 * @param inputStream
	 * @throws IOException 
	 */
	public MemoryCacheFile(InputStream inputStream) throws IOException {
		if (inputStream == null)
			return;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[2 * 1024 * 1024];
		int len;
		try {
			while ((len = inputStream.read(buffer)) > -1) {
				byteArrayOutputStream.write(buffer, 0, len);
			}
			byteArrayOutputStream.flush();
			fileBytes = byteArrayOutputStream.toByteArray();
			logger.info("缓存解压后的文件大小:"+fileBytes.length);
		} finally {
			try {
				byteArrayOutputStream.close();
				byteArrayOutputStream=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				inputStream.close();
				inputStream=null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		returnedStream = new ArrayList<InputStream>();
	}

	/**
	 * 获取一个新的输入流
	 * 
	 * @return
	 */
	public InputStream getInputStream() {
		if (fileBytes == null)
			return null;

		InputStream in = new ByteArrayInputStream(fileBytes);
		returnedStream.add(in);
		return in;
	}

	public void close() {
		for (InputStream in : returnedStream) {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
		fileBytes = null;

	}
}
