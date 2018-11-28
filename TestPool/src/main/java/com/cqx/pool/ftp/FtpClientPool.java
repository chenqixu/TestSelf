package com.cqx.pool.ftp;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * @author chenqixu
 * @description Ftp客户端连接池
 * @date 2018/11/28 17:10
 */
public class FtpClientPool extends GenericKeyedObjectPool<FtpCfg, NlFtpClient> {

    private static FtpClientPool pool;
    private static int MaxPerKeySize = 8; // 每个KEY最多允许有几个连接
    private static int MaxTotalSize = 100;// 连接池里所有key连接的总和数量。 建议是预估KEY值的数量*x
    // 这个设置必须在第一次调用getInstance()之前

    public static void setPoolMaxTotalSize(int size) {
        MaxTotalSize = size;
    }

    // 这个设置必须在第一次调用getInstance()之前
    public static void setPoolMaxPerKeySize(int size) {
        MaxPerKeySize = size;
    }

    public synchronized static FtpClientPool getInstance() {

        if (pool == null) {
            GenericKeyedObjectPoolConfig poolConfig = new GenericKeyedObjectPoolConfig();
            poolConfig.setMaxTotalPerKey(MaxPerKeySize); // 每个FTP配置的最大连接数量.极端情况一个FTP采集到落地
            poolConfig.setMaxTotal(MaxTotalSize); // 所有KEY的连接总和时多少个
            poolConfig.setMinIdlePerKey(1);
            poolConfig.setMaxIdlePerKey(3); // 每个KEY空闲最多4个。
            poolConfig.setTestOnCreate(true);
            poolConfig.setTestOnBorrow(true); // 由于有定时做空闲检测，就不在借出的时候再去做判断了
            poolConfig.setTestOnReturn(false);
            poolConfig.setTestWhileIdle(true); // 开启空闲检测
            poolConfig.setTimeBetweenEvictionRunsMillis(20 * 1000); // 20秒检测一次空闲线程的存活情况
            poolConfig.setMaxWaitMillis(10 * 1000); // 如果没有空闲连接，等待10秒超时
            poolConfig.setMinEvictableIdleTimeMillis(600 * 1000);// 如果对象超过600秒没有被使用，则将其释放。 由于有心跳检测，不用担心连接失效
            pool = new FtpClientPool(new FtpClientFactory(), poolConfig);
        }
        return pool;
    }

    private FtpClientPool(FtpClientFactory factory, GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }
}
