package com.cqx.common.utils.localcache.lmdb.serializer;

import java.nio.ByteBuffer;

/**
 * AbstractLmdbSerializer
 *
 * @author chenqixu
 */
public abstract class AbstractLmdbSerializer<T> implements LmdbSerializer<T> {

    /**
     * 线程内部存储类，用于线程间的数据隔离
     */
    ThreadLocal<ByteBuffer> threadLocal = new ThreadLocal<ByteBuffer>() {
        @Override
        protected ByteBuffer initialValue() {
            /**
             * 是不使用JVM堆栈而是通过操作系统来创建内存块用作缓冲区，
             * 它与当前操作系统能够更好的耦合，因此能进一步提高I/O操作速度。
             * 但是分配直接缓冲区的系统开销很大，因此只有在缓冲区较大并长期存在，
             * 或者需要经常重用时，才使用这种缓冲区
             */
            return ByteBuffer.allocateDirect(512);
        }
    };

    @Override
    public ByteBuffer serialize(T t) {
        //序列化，从bean序列化成字节数组
        byte[] bytes = beanToBytes(t);
        //获取分配好的ByteBuffer，这里使用thrreadLocal是为了复用
        ByteBuffer byteBuffer = threadLocal.get();
        //清理，为了复用
        byteBuffer.clear();
        //装载数据，并翻转此缓冲区
        byteBuffer.put(bytes).flip();
        return byteBuffer;
    }

    @Override
    public T deserialize(ByteBuffer byteBuffer) {
        if (byteBuffer == null) return null;
        //初始化数组大小，不知道capacity()什么效果
        byte[] bytes = new byte[byteBuffer.remaining()];
        //从缓存写入数组
        byteBuffer.get(bytes);
        //反序列化，从字节数组反序列化成bean
        return bytesToBean(bytes);
    }

    abstract byte[] beanToBytes(T t);

    abstract T bytesToBean(byte[] bytes);
}
