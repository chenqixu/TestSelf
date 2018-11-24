package com.newland.bi.bigdata.txt;

import java.util.List;

import com.cqx.process.LogInfoFactory;
import com.newland.bi.bigdata.changecode.ChangeCode;

public class NullTextRead extends ChangeCode {

	private static LogInfoFactory logger = LogInfoFactory.getInstance(NullTextRead.class);
	
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
