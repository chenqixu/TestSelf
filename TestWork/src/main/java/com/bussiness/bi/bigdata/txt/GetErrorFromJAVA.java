package com.bussiness.bi.bigdata.txt;

import java.util.List;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import com.bussiness.bi.bigdata.changecode.ChangeCode;

/**
 * 从JAVA文件中获取错误日志代码
 * @author chenqixu
 *
 */
public class GetErrorFromJAVA extends ChangeCode {

	private static MyLogger logger = MyLoggerFactory.getLogger(GetErrorFromJAVA.class);

	public void run(String scanpath) {
		setRead_code("UTF-8");
		List<String> javalist = read(scanpath);
		for (String str : javalist) {
			if (str.contains(".error(") && !str.trim().startsWith("//")) {
				logger.info(str.trim());
			}
		}
	}

	public static void main(String[] args) {
		new GetErrorFromJAVA()
				.run("D:\\Document\\Workspaces\\Git\\FujianSCM\\udap-component\\nl-udap-component-stream\\nl-udap-component-stream-file\\src\\main\\java\\com\\newland\\storm\\component\\etl\\file\\merge\\HdfsFileMergeBolt.java");
	}
}
