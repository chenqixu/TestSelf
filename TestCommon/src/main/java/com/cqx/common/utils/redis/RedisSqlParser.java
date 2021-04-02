package com.cqx.common.utils.redis;

import com.cqx.common.utils.redis.client.RedisClient;
import com.cqx.common.utils.redis.impl.RedisPolicy;

import java.sql.SQLException;

/**
 * sql解析器，只支持小写
 * <pre>
 *     支持以下写法
 *     select * from string###06006005;
 *     select * from hash###06006005;
 *     select * from hash###06006005 where field=111111;
 *
 *     select key,value from string###06006005;
 *     select key,field,value from hash###06006005;
 *
 *     insert into string###06006005 values('aaa');
 *     insert into hash###06006005 values('xxx','yyyy');
 *
 *     update string###06006005 set value='ccc';
 *     update hash###06006005 set value='bbb' where field=111111;
 *
 *     delete from string###06006005;
 *     delete from hash###06006005 where field=111111;
 *     delete from hash###06006005;
 * </pre>
 *
 * @author chenqixu
 */
public class RedisSqlParser {

    private RedisClient rc;
    private String sql;
    private RedisPolicy redisPolicy;

    public RedisSqlParser(RedisClient rc) {
        this.rc = rc;
    }

    public static void main(String[] args) throws SQLException {
        RedisSqlParser redisSqlParser;
        RedisClient rc = RedisFactory.builder()
                .setMode(RedisFactory.CLUSTER_MODE_TYPE)
                .setIp_ports("10.1.4.185:6380,10.1.4.185:6381,10.1.4.185:6382,10.1.4.185:6383,10.1.4.185:6384,10.1.4.185:6385")
                .build();
        redisSqlParser = new RedisSqlParser(rc);
        redisSqlParser.parser("insert into hash###06006004 values('004403000003101016127868F700D910','{\"apkVersion\":\"V2.1.2\",\"deviceId\":\"004403000003101016127868F700D909\",\"loginCount\":0,\"terminalMode\":\"ZXV10\"}');");
    }

    private void getPolicy() throws SQLException {
        RedisPolicy[] redisPolicies = {RedisPolicy.SELECT, RedisPolicy.INSERT,
                RedisPolicy.UPDATE, RedisPolicy.DELETE};
        for (RedisPolicy _redisPolicy : redisPolicies) {
            if (sql.toLowerCase().startsWith(_redisPolicy.getCode())) {
                redisPolicy = _redisPolicy;
                break;
            }
        }
        if (redisPolicy == null) {
            String reason = "非法开头的语句not in(select、insert、update、delete)";
            String SQLState = "err";
            int vendorCode = -1;
            throw new SQLException(reason, SQLState, vendorCode);
        }
    }

    public void parser(String sql) throws SQLException {
        this.sql = sql.trim();
        //确定处理策略：select、update、insert、delete
        getPolicy();
        //使用对应策略进行解析、处理
        redisPolicy.getiRedisParser().setRc(this.rc);
        redisPolicy.getiRedisParser().setSql(this.sql);
        redisPolicy.getiRedisParser().splitSql();
        redisPolicy.getiRedisParser().deal();
    }

    public <T> T getRedisResultSet() {
        return redisPolicy.getiRedisParser().getRedisResultSet();
    }
}
