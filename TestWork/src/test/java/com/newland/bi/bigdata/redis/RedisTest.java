package com.newland.bi.bigdata.redis;

import com.cqx.annotation.AbsTestFactory;
import com.cqx.annotation.MyTest.Before;
import com.cqx.annotation.MyTest.Test;
import com.cqx.process.LogInfoFactory;
import com.newland.bi.bigdata.utils.OtherUtils;

public class RedisTest extends AbsTestFactory {	

	private static LogInfoFactory logger = LogInfoFactory.getInstance(RedisTest.class);
	private RedisClient rc = null;
	
	@Before
	public void testBuild() {
		
	}

	/**
	 * 单节点
	 */
	@Test
	public void testSingle() {
		rc = RedisFactory.builder()
			.setMode(RedisFactory.SINGLE_MODE_TYPE)
			.setIp("10.1.4.185")
			.setPort(6379)
			.build();
		logger.info("begin...");
		logger.info(rc.get("f1"));
		logger.info(rc.get("foo"));
		logger.info("set...");
		rc.setnx("foo", "bar456", 1);
		rc.setnx("f1", "123", 1);
		OtherUtils.sleep(500);
		logger.info("sleep 500");
		logger.info(rc.get("f1"));
		logger.info(rc.get("foo"));
		OtherUtils.sleep(1000);
		logger.info("sleep 1000");
		logger.info(rc.get("f1"));
		logger.info(rc.get("foo"));
		rc.close();
	}

	/**
	 * 集群模式
	 */
	@Test(status="start")
	public void testCluster() {
		rc = RedisFactory.builder()
				.setMode(RedisFactory.CLUSTER_MODE_TYPE)
				// 开发
//				.setIp_ports("10.1.4.185:6380,10.1.4.185:6381,10.1.4.185:6382,10.1.4.185:6383,10.1.4.185:6384,10.1.4.185:6385")
				// 研发
				.setIp_ports("10.1.8.1:6380,10.1.8.1:6381,10.1.8.1:6382,10.1.8.1:7383,10.1.8.1:7384,10.1.8.1:7385,10.1.8.2:6383,10.1.8.2:6384,10.1.8.2:6385,10.1.8.2:7386,10.1.8.2:7387,10.1.8.2:7388,10.1.8.3:6386,10.1.8.3:6387,10.1.8.3:6388,10.1.8.3:7380,10.1.8.3:7381,10.1.8.3:7382")
				.build();
		String key = "filelock:103659171521";
		String field = "HTTP_78_20181115_151715_848.TXT";
//		rc.set("foo", "123");
//		logger.info(rc.get("foo"));
//		logger.info(String.valueOf(rc.del("foo")));
//		logger.info(rc.hget(key, field));
		String data = rc.hget(key, field);
		if(data==null)
			logger.info("null...");
		else
			logger.info("data：{} ", data);
//		rc.hdel(key, field);
//		String lockJson = rc.hget("filelock:100833124645", "test.log");
//		FileLock lock = FileLock.deserialize(lockJson);
//		lock.setFileOffset("char=123:line=2844");
//		lockJson = lock.toString();
//		rc.hset("filelock:100833124645", "test.log", lockJson);
		rc.close();
	}

	public static void main(String[] args) {
		new RedisTest().test();
	}
}
