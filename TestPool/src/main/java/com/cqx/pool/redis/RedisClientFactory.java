package com.cqx.pool.redis;

import com.cqx.cli.util.BeanUtil;
import com.cqx.cli.util.JDBCUtil;
import com.cqx.redis.bean.RedisCfg;
import com.cqx.redis.jdbc.RedisConnection;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * RedisClientFactory
 *
 * @author chenqixu
 */
public class RedisClientFactory implements KeyedPooledObjectFactory<RedisCfg, RedisConnection> {

    private static final Logger logger = LoggerFactory.getLogger(RedisClientFactory.class);
    public final static int CONNECT_TIME_WAIT = 15 * 1000; // 15秒获取连接超时

    /**
     * 这个方法是用来创建一个对象，当在GenericObjectPool类中调用borrowObject方法时，
     * 如果当前对象池中没有空闲的对象，GenericObjectPool会调用这个方法，
     * 创建一个对象，并把这个对象封装到PooledObject类中，并交给对象池管理。
     */
    @Override
    public PooledObject<RedisConnection> makeObject(RedisCfg redisCfg) throws Exception {
        logger.info("makeObject###redis连接工厂新创建redis连接:{}", redisCfg);
        JDBCUtil jdbcUtil = new JDBCUtil(BeanUtil.rediscfgTocmd(redisCfg));
        Connection connection = jdbcUtil.getConn();
        logger.info("makeObject###redis连接工厂成功新创建redis连接:{}", connection);
        return new DefaultPooledObject(connection);
    }

    /**
     * 销毁对象，当对象池检测到某个对象的空闲时间(idle)超时，
     * 或使用完对象归还到对象池之前被检测到对象已经无效时，
     * 就会调用这个方法销毁对象。对象的销毁一般和业务相关，
     * 但必须明确的是，当调用这个方法之后，对象的生命周期必须结果。
     * 如果是对象是线程，线程必须已结束，如果是socket，socket必须已close，
     * 如果是文件操作，文件数据必须已flush，且文件正常关闭。
     */
    @Override
    public void destroyObject(RedisCfg redisCfg, PooledObject<RedisConnection> clientObject) throws Exception {
        Connection connection = clientObject.getObject();
        logger.info("destroyObject###redis连接工厂释放一个redis连接{} key:{}", connection.toString(), redisCfg);
        connection.close();
    }

    /**
     * 检测一个对象是否有效。在对象池中的对象必须是有效的，
     * 这个有效的概念是，从对象池中拿出的对象是可用的。
     * 比如，如果是socket,那么必须保证socket是连接可用的。
     * 在从对象池获取对象或归还对象到对象池时，会调用这个方法，判断对象是否有效，如果无效就会销毁
     */
    @Override
    public boolean validateObject(RedisCfg redisCfg, PooledObject<RedisConnection> clientObject) {
        boolean result = false;
        Connection connection = clientObject.getObject();
        try {
            result = !connection.isClosed();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("validateObject###redis连接工厂验证一个redis连接{} key:{} 结果:{}", connection.toString(), redisCfg, result);
        return result;
    }

    /**
     * 激活一个对象或者说启动对象的某些操作。比如，如果对象是socket，
     * 如果socket没有连接，或意外断开了，可以在这里启动socket的连接。
     * 它会在检测空闲对象的时候，如果设置了测试空闲对象是否可以用，
     * 就会调用这个方法，在borrowObject的时候也会调用。
     * 另外，如果对象是一个包含参数的对象，可以在这里进行初始化。让使用者感觉这是一个新创建的对象一样。
     */
    @Override
    public void activateObject(RedisCfg redisCfg, PooledObject<RedisConnection> clientObject) throws Exception {
        Connection connection = clientObject.getObject();
        logger.info("activateObject###redis连接工厂激活一个redis连接{} key:{}", connection.toString(), redisCfg);
    }

    /**
     * 钝化一个对象。在向对象池归还一个对象是会调用这个方法。
     * 这里可以对对象做一些清理操作。比如清理掉过期的数据，下次获得对象时，不受旧数据的影响。
     * 一般来说activateObject和passivateObject是成对出现的。
     * 前者是在对象从对象池取出时做一些操作，后者是在对象归还到对象池做一些操作，
     * 可以根据自己的业务需要进行取舍。
     */
    @Override
    public void passivateObject(RedisCfg redisCfg, PooledObject<RedisConnection> clientObject) throws Exception {
        Connection connection = clientObject.getObject();
        logger.info("passivateObject###redis连接工厂钝化一个redis连接{} key:{}", connection.toString(), redisCfg);
    }
}
