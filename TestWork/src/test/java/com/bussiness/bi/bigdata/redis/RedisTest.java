package com.bussiness.bi.bigdata.redis;

import com.cqx.annotation.AbsTestFactory;
import com.cqx.annotation.MyTest.Before;
import com.cqx.annotation.MyTest.Test;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import com.cqx.common.utils.redis.client.RedisClient;
import com.cqx.common.utils.redis.RedisFactory;
import com.bussiness.bi.bigdata.utils.OtherUtils;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Map;

public class RedisTest extends AbsTestFactory {

    private static MyLogger logger = MyLoggerFactory.getLogger(RedisTest.class);
    private RedisClient rc = null;

    public static void main(String[] args) {
        new RedisTest().test();
    }

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
    @Test(status = "start")
    public void testCluster() {
        rc = RedisFactory.builder()
                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                // 10.1.8.75
                .setIp_ports("10.1.8.75:9001,10.1.8.75:9002,10.1.8.75:9003,10.1.8.75:9004,10.1.8.75:9005,10.1.8.75:9006")
                // 开发
//                .setIp_ports("10.1.4.185:6380,10.1.4.185:6381,10.1.4.185:6382,10.1.4.185:6383,10.1.4.185:6384,10.1.4.185:6385")
                // 研发
//				.setIp_ports("10.1.8.1:6380,10.1.8.1:6381,10.1.8.1:6382,10.1.8.1:7383,10.1.8.1:7384,10.1.8.1:7385,10.1.8.2:6383,10.1.8.2:6384,10.1.8.2:6385,10.1.8.2:7386,10.1.8.2:7387,10.1.8.2:7388,10.1.8.3:6386,10.1.8.3:6387,10.1.8.3:6388,10.1.8.3:7380,10.1.8.3:7381,10.1.8.3:7382")
                .build();
//        RealtimeDevice realtimeDevice = new RealtimeDevice();
//        realtimeDevice.setDeviceId("004403000003101016127868F700D909");
//        realtimeDevice.setTerminalMode("ZXV10");
//        realtimeDevice.setApkVersion("V2.1.2");
////		rc.hset("06006004", "004403000003101016127868F700D909", realtimeDevice.toJson());
//        String data = rc.hget("06006004", "004403000003101016127868F700D909");
//        System.out.println(data);
//        data = rc.hget("06006007", "ZXV10__V2.1.2");
//        System.out.println(data);
//        data = rc.hget("06006005", "18250326632");
//        System.out.println(data);
//        data = rc.get("foo");
//        System.out.println(data);
//        System.out.println("06006007 size：" + rc.hgetAll("06006007").size());

        ScanResult<Map.Entry<String, String>> scanResult = rc.hscan("06006008", "0");
        List<Map.Entry<String, String>> entryList = scanResult.getResult();
        System.out.println("size：" + entryList.size());
        for (Map.Entry<String, String> entry : entryList) {
            System.out.println("[key：" + entry.getKey() + " [value：" + entry.getValue());
        }
//		String key = "filelock:103659171521";
//		String field = "HTTP_78_20181115_151715_848.TXT";
//		rc.set("foo", "123");
//		logger.info(rc.get("foo"));
//		logger.info(String.valueOf(rc.del("foo")));
//		logger.info(rc.hget(key, field));
//		String data = rc.hget(key, field);
//		if(data==null)
//			logger.info("null...");
//		else
//			logger.info("data：{} ", data);
//		rc.hdel(key, field);
//		String lockJson = rc.hget("filelock:100833124645", "test.log");
//		FileLock lock = FileLock.deserialize(lockJson);
//		lock.setFileOffset("char=123:line=2844");
//		lockJson = lock.toString();
//		rc.hset("filelock:100833124645", "test.log", lockJson);
        rc.close();
    }
}
