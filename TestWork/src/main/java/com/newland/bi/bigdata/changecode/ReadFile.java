package com.newland.bi.bigdata.changecode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import com.cqx.process.LogInfoFactory;

public class ReadFile {

	private static LogInfoFactory logger = LogInfoFactory
			.getInstance(ReadFile.class);

	public static String getCharset(String path) {
		InputStream is = null;
		UniversalDetector detector = new UniversalDetector(null);
		try {
			is = new FileInputStream(path);
			byte[] bytes = new byte[1024];
			int nread;
			if ((nread = is.read(bytes)) > 0 && !detector.isDone()) {
				detector.handleData(bytes, 0, nread);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
				is = null;
			}
		}
		detector.dataEnd();
		String encode = detector.getDetectedCharset();
		/** default UTF-8 */
		if (StringUtils.isEmpty(encode)) {
			encode = "UTF-8";
		}
		detector.reset();
		logger.debug("path：{}，encode：{} ", path, encode);
		return encode;
	}

	public static void main(String[] args) throws Exception {
//		ReadFile.getCharset("D:/Document/Workspaces/Git/TestSelf/TestSpring/src/main/java/com/spring/test/servlet/GetDBConnServlet.java");
//		ReadFile.getCharset("D:/Document/Workspaces/Git/TestSelf/TestWork/src/main/java/com/newland/bi/bigdata/changecode/ReadFile.java");
		ReadFile.getCharset("D:/Document/Workspaces/Git/TestSelf/TestFrameForm/src/main/java/mainForm.java");

//		ChangeCode cc = new ChangeCode();
//		cc.setScan_path("D:/Document/Workspaces/Git/TestSelf");
//		cc.setScan_rule(".*\\.java");
//		cc.setRead_code("GBK");
//		cc.setWrite_code("UTF-8");
//		for (String path : cc.scan()) {
//			String encode = ReadFile.getCharset(path);
//			if(encode.startsWith("GB")) {
//				logger.info("path：{}，encode：{} ", path, encode);
//				cc.change(path, encode, "UTF-8");
//			} else if(encode.startsWith("UTF-8")) {
////				logger.info("path：{}，encode：{} ", path, encode);
//			} else if(encode.startsWith("WINDOWS-1252")) {
//				logger.info("path：{}，encode：{} ", path, encode);
//				cc.change(path, "GBK", "UTF-8");
//			} else {
//				logger.info("path：{}，encode：{} ", path, encode);
//			}
//		}
	}
}
