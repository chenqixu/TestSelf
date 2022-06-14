package com.bussiness.bi.bigdata.txt;

import java.util.List;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import com.bussiness.bi.bigdata.changecode.ChangeCode;

public class NullTextRead extends ChangeCode {

	private static MyLogger logger = MyLoggerFactory.getLogger(NullTextRead.class);
	
	public void run(String scanpath) {
		setRead_code("UTF-8");
		List<String> javalist = read(scanpath);
		logger.info("##begin##");
		for (String str : javalist) {
			logger.info(str.trim());
		}
		logger.info("##end##");
	}

	public static void main(String[] args) {
		new NullTextRead()
				.run("D:\\tmp\\null.txt");
	}

}
