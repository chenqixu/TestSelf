package com.cqx.exception.base;

import java.io.IOException;

import com.cqx.annotation.AbsTestFactory;
import com.cqx.annotation.MyTest.Test;
import com.cqx.process.LogInfoFactory;

public class DistCollectExceptionTest extends AbsTestFactory {	

	private static LogInfoFactory log = LogInfoFactory.getInstance(DistCollectExceptionTest.class);
	
	@Test(status="start")
	public void testException() {
		try {
			throw new IOException("无法获取FTP连接.连接配置:FtpCfg[host=10.1.8.78]");
//			throw new DistCollectException(DistCollectErrorCode.COLLECTION_FTP_ERROR, "无法获取FTP连接.连接配置:FtpCfg[host=10.1.8.78]");
		} catch (Exception e) {
			log.error("异常测试：", new DistCollectException(DistCollectErrorCode.COLLECTION_FTP_ERROR, e));
//			log.error("test.", e);
		}
	}

	public static void main(String[] args) {
		new DistCollectExceptionTest().test();
	}
}
