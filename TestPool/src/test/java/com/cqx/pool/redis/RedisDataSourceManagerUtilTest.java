package com.cqx.pool.redis;

import com.cqx.cli.util.JDBCUtil;
import com.cqx.redis.bean.RedisCfg;
import org.junit.Test;

public class RedisDataSourceManagerUtilTest {

    private RedisDataSourceManagerUtil redisDataSourceManagerUtil;
    private RedisCfg redisCfg;
    private String redis_dns = "10.1.4.185:6380,10.1.4.185:6381,10.1.4.185:6382,10.1.4.185:6383,10.1.4.185:6384,10.1.4.185:6385";

    @Test
    public void testConn() throws Exception {
        redisCfg = RedisCfg.builder().setIp_ports(redis_dns);
        JDBCUtil jdbcUtil = new JDBCUtil(RedisDataSourceManagerUtil.getRedisConnection(redisCfg));
        jdbcUtil.executeQuery("select * from hash###06006004");
        jdbcUtil.closeAll();
    }
}