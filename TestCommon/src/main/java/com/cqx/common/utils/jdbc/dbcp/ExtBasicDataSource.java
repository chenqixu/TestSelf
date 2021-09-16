package com.cqx.common.utils.jdbc.dbcp;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * ExtBasicDataSource
 *
 * @author chenqixu
 */
public class ExtBasicDataSource extends BasicDataSource {
    private static final Logger logger = LoggerFactory.getLogger(ExtBasicDataSource.class);

    public void init() {
        this.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        this.setUrl("jdbc:oracle:thin:@10.1.0.242:1521/ywxx");
        this.setUsername("web");
        this.setPassword("T%vdNV#i$2");
        // 最大活动连接
        this.setMaxActive(5);//5
        // 最小空闲连接
        this.setMinIdle(2);//2
        // 最大空闲连接
        this.setMaxIdle(3);//3
        // 获取连接时最大等待时间，单位毫秒
        this.setMaxWait(5000L);//5000
        // 设置验证sql，在连接空闲的时候会做
        this.setValidationQuery("select 1 from dual");//select 1 from dual
        // 连接验证的查询超时时间，单位毫秒
//        this.setValidationQueryTimeout(10000L);//10000
        // 连接借出时是否做验证
        this.setTestOnBorrow(false);//false
        // 连接在空闲的时候是否做验证
        this.setTestWhileIdle(true);//true
        // 连接回收的时候是否做验证
        this.setTestOnReturn(false);//false
        // 是否缓存preparedStatement，支持游标的数据库有性能提升
        this.setPoolPreparedStatements(false);//false
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        this.setTimeBetweenEvictionRunsMillis(60000L);//60000
        // 配置一个连接在池中最小生存的时间，单位是毫秒
        this.setMinEvictableIdleTimeMillis(300000L);//300000
    }

    public void getExtConnection() throws SQLException {
        logger.info("【start】NumActive：{}，NumIdle：{}"
                , getNumActive()
                , getNumIdle()
        );
        try (Connection conn = getConnection()) {
            logger.info("【borrow】NumActive：{}，NumIdle：{}"
                    , getNumActive()
                    , getNumIdle()
            );
        }
        logger.info("【return】NumActive：{}，NumIdle：{}"
                , getNumActive()
                , getNumIdle()
        );
    }
}
