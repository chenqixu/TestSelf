package com.cqx.common.utils.localcache.lmdb;

import com.cqx.common.utils.localcache.lmdb.serializer.AbstractLmdbSerializer;
import com.cqx.common.utils.localcache.lmdb.serializer.StringSerializer;
import org.lmdbjava.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * LmdbUtil<br>
 * JAVA外部函数接口：JNI，JNA，JNR
 *
 * @author chenqixu
 */
public class LmdbUtil {
    private static final Logger logger = LoggerFactory.getLogger(LmdbUtil.class);
    private String lmdbPath;
    private Env<ByteBuffer> env;
    private int lmdbMaxSize = 10;//10MB
    private int lmdbDatabaseCount = 10;
    private Dbi<ByteBuffer> db;
    private AbstractLmdbSerializer<String> lmdbKeySerializer;
    private AbstractLmdbSerializer<String> lmdbValueSerializer;

    public LmdbUtil(String lmdbPath) {
        this(lmdbPath, false);
    }

    public LmdbUtil(String lmdbPath, boolean safe) {
        //lmdb存储路径
        this.lmdbPath = lmdbPath;
        logger.info("lmdbPath：{}", lmdbPath);
        //lmdb序列化反序列化方式
        lmdbKeySerializer = new StringSerializer();
        lmdbValueSerializer = new StringSerializer();
        try {
            //创建lmdb的环境变量
            if (!safe) {
                logger.info("使用默认模式：PROXY_OPTIMA");
                env = createEnv_PROXY_OPTIMAL();
            } else {
                if (lmdbPath.endsWith("/") || lmdbPath.endsWith("\\"))
                    throw new LmdbException("PROXY_SAFE模式需要指定到文件！");
                logger.info("使用PROXY_SAFE模式");
                env = createEnv_PROXY_SAFE();
            }
            logger.info("初始化Lmdb成功......");
        } catch (Exception e) {
            throw new LmdbException(String.format("初始化Lmdb %s 异常", lmdbPath), e);
        }
    }

    private Env<ByteBuffer> createEnv_PROXY_OPTIMAL() {
        final File path = new File(lmdbPath);
        //ByteBufferProxy.PROXY_OPTIMAL
        //A proxy that uses Java's "unsafe" class to directly manipulate byte buffer fields and JNR-FFF allocated memory pointers.
        //使用Java的 "unsafe" 类直接操作字节缓冲区字段和JNR-FFF分配的内存指针的代理
        return Env.create(ByteBufferProxy.PROXY_OPTIMAL)
                //设置map大小
                .setMapSize(lmdbMaxSize * 1024 * 1024)
                //设置数据库数
                .setMaxDbs(lmdbDatabaseCount)
                //设置允许的最大数据库数
                .setMaxReaders(lmdbDatabaseCount * 2)
                .open(path);
    }

    private Env<ByteBuffer> createEnv_PROXY_SAFE() {
        final File path = new File(lmdbPath);
        //ByteBufferProxy.PROXY_SAFE
        //The safe, reflective ByteBuffer proxy for this system. Guaranteed to never be null.
        //此系统的安全、反射ByteBuffer代理。保证永不为空
        //A proxy that uses Java reflection to modify byte buffer fields, and official JNR-FFF methods to manipulate native pointers.
        //一个代理，使用Java反射修改字节缓冲区字段，并使用官方JNR-FFF方法操作本机指针
        return Env.create(ByteBufferProxy.PROXY_SAFE)
                //设置map大小
                .setMapSize(lmdbMaxSize * 1024 * 1024)
                //设置数据库数
                .setMaxDbs(lmdbDatabaseCount)
                //设置允许的最大数据库数
                .setMaxReaders(lmdbDatabaseCount * 2)
                //在0.6.3版本下，以0664模式打开环境
                .open(path
                        //使用可写mmap
                        , EnvFlags.MDB_WRITEMAP
                        //提交后不进行fsync
                        , EnvFlags.MDB_NOSYNC
                        //没有环境目录
                        , EnvFlags.MDB_NOSUBDIR
                );
    }

    public void initDbi(String dbName) {
        //openDbi
        //使用UTF-8数据库名打开Dbi的方便方法
        db = env.openDbi(dbName
                //DbiFlags.MDB_CREATE
                //如果指定的数据库不存在，则创建该数据库
                //在只读事务或只读环境中不允许使用此选项
                , DbiFlags.MDB_CREATE);
    }

    /**
     * 获取读写事务
     *
     * @return
     */
    private Txn<ByteBuffer> txnWrite() {
        return env.txnWrite();
    }

    /**
     * 获取只读事务
     *
     * @return
     */
    private Txn<ByteBuffer> txnRead() {
        return env.txnRead();
    }

    /**
     * 开启游标
     *
     * @param db
     * @param txn
     * @return
     */
    private Cursor<ByteBuffer> openCursor(Dbi<ByteBuffer> db, Txn<ByteBuffer> txn) {
        return db.openCursor(txn);
    }

    /**
     * 关闭游标
     *
     * @param cursor
     */
    private void closeCursor(Cursor<ByteBuffer> cursor) {
        if (cursor != null) cursor.close();
    }

    /**
     * 写入&lt;key, value&gt;
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        check();
        //获取读写事务
        try (Txn<ByteBuffer> txn = txnWrite()) {
            db.put(txn, lmdbKeySerializer.serialize(key), lmdbValueSerializer.serialize(value));
            //事务提交
            txn.commit();
        }
    }

    /**
     * 通过key获取value
     *
     * @param key
     * @return
     */
    public String get(String key) {
        check();
        try (Txn<ByteBuffer> txn = txnRead()) {
            ByteBuffer byteBuffer = db.get(txn, lmdbKeySerializer.serialize(key));
            return lmdbValueSerializer.deserialize(byteBuffer);
        }
    }

    /**
     * 使用了游标，写入键值对
     *
     * @param key
     * @param value
     */
    public void putValueToDb(String key, String value) {
        check();
        //获取读写事务
        try (Txn<ByteBuffer> txn = txnWrite()) {
            //创建光标句柄
            //游标与特定事务和数据库相关联。关闭数据库句柄时不能使用游标。
            //也不是在交易结束时，除非光标.更新（Txn）。
            //它可以被丢弃光标.关闭().
            //写事务中的游标可以在事务结束前关闭，否则将在事务结束时关闭。
            //只读事务中的游标必须在其事务结束之前或之后显式关闭。
            //它可以重复使用光标.更新（Txn）最后关闭它。
            Cursor<ByteBuffer> cursor = openCursor(db, txn);
            cursor.put(lmdbKeySerializer.serialize(key), lmdbValueSerializer.serialize(value));
            closeCursor(cursor);
            //事务提交
            txn.commit();
        }
    }

    /**
     * 使用了游标，通过键获取对应的值
     *
     * @param key
     * @return
     */
    public String getValueByKey(String key) {
        check();
        //获取只读事务
        try (Txn<ByteBuffer> txn = txnRead()) {
            //打开游标
            Cursor<ByteBuffer> cursor = openCursor(db, txn);
            //GetOp.MDB_SET_KEY
            //在指定键处定位，返回键+数据
            cursor.get(lmdbKeySerializer.serialize(key), GetOp.MDB_SET_KEY);
            //获取value
            ByteBuffer byteBuffer = cursor.val();
            //关闭游标
            closeCursor(cursor);
            //反序列
            return lmdbValueSerializer.deserialize(byteBuffer);
        }
    }

    /**
     * 使用了游标，获取所有键值对
     *
     * @return
     */
    public Map<String, String> getAllValueByDbName() {
        check();
        Map<String, String> map = new HashMap<>();
        try (Txn<ByteBuffer> txn = txnRead()) {
            Cursor<ByteBuffer> cursor = openCursor(db, txn);
            while (cursor.next()) {
                map.put(lmdbKeySerializer.deserialize(cursor.key()), lmdbValueSerializer.deserialize(cursor.val()));
            }
            closeCursor(cursor);
        }
        return map;
    }

    public long getDbCount() {
        return getAllValueByDbName().size();
    }

    private synchronized void check() {
        if (db == null) {
            //初始化一个默认数据库
            logger.info("初始化一个默认数据库default");
            initDbi("default");
        }
    }

    public String getDbName() {
        check();
        return new String(db.getName(), StandardCharsets.UTF_8);
    }

    public void release() {
        if (db != null) db.close();
    }
}
