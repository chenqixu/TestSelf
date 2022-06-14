package com.bussiness.bi.bigdata.redis;

import com.cqx.common.utils.redis.client.RedisClient;
import com.cqx.common.utils.redis.RedisFactory;
import com.cqx.common.utils.redis.RedisSqlParser;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RedisSqlParserTest {

    private RedisSqlParser redisSqlParser;

    @Before
    public void build() {
        RedisClient rc = RedisFactory.builder()
//                .setMode(RedisFactory.SINGLE_MODE_TYPE)
                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                .setIp_ports("10.1.4.185:6380,10.1.4.185:6381,10.1.4.185:6382,10.1.4.185:6383,10.1.4.185:6384,10.1.4.185:6385")
//                .setIp_ports("192.168.230.128:6379")
//                .setIp("192.168.230.128")
//                .setPort(6379)
                .build();
        redisSqlParser = new RedisSqlParser(rc);
    }

    @Test
    public void parser() throws SQLException {
        redisSqlParser.parser("select * from hash###06006004;");
//        redisSqlParser.parser("select value from hash###06006004 where field=a1;");
//        redisSqlParser.parser("select * from hash###06006005 where field=18250326632;");
//        redisSqlParser.parser("select * from hash###06006007 where field=ZXV10__V2.1.2;");
//        redisSqlParser.parser("select * from hash###06006004;");
//        redisSqlParser.parser("select * from string###foo;");
//        redisSqlParser.parser("insert into hash###06006004 values('a1',' ');");
        ResultSet rs = redisSqlParser.getRedisResultSet();
        while (rs != null && rs.next()) {
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                System.out.print(rs.getString(i + 1));
                System.out.print("|");
            }
            System.out.println();
        }
    }
}