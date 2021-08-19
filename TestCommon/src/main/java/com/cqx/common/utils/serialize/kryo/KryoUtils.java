package com.cqx.common.utils.serialize.kryo;

import com.cqx.common.utils.serialize.kryo.registrar.ArrayListSerializer;
import com.cqx.common.utils.serialize.kryo.registrar.SqlDateSerializer;
import com.cqx.common.utils.serialize.kryo.registrar.TimestampSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * KryoUtils
 *
 * @author chenqixu
 */
public class KryoUtils {
    private final static ConcurrentHashMap<Class<?>, Kryo> cachedKryo = new ConcurrentHashMap<>();
    private static ThreadLocal<Input> threadKryoInput = new ThreadLocal<Input>() {
        @Override
        protected Input initialValue() {
            return new Input(1);
        }
    };
    /**
     * Output中的bufferSize就是单次序列化的上限，如果byte[]长度超过这个上限，就会异常
     */
    private static ThreadLocal<Output> threadKryoOutput = new ThreadLocal<Output>() {
        @Override
        protected Output initialValue() {
            return new Output(new ByteArrayOutputStream(), 100000);
//            return new Output(new ByteArrayOutputStream(), 2000000);
//            return new Output(2000, 2000000000);
        }
    };
    private static ThreadLocal<Kryo> runtimeSerializationKryo = new ThreadLocal<Kryo>() {
        @Override
        protected synchronized Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
            kryo.register(ArrayList.class, new ArrayListSerializer());
            kryo.register(java.sql.Date.class, new SqlDateSerializer());
            kryo.register(java.sql.Timestamp.class, new TimestampSerializer());
            //引用，对A对象序列化时，默认情况下kryo会在每个成员对象第一次序列化时写入一个数字，
            // 该数字逻辑上就代表了对该成员对象的引用，如果后续有引用指向该成员对象，
            // 则直接序列化之前存入的数字即可，而不需要再次序列化对象本身。
            // 这种默认策略对于成员存在互相引用的情况较有利，否则就会造成空间浪费
            // （因为没序列化一个成员对象，都多序列化一个数字），
            // 通常情况下可以将该策略关闭，kryo.setReferences(false);
            kryo.setReferences(false);
            // 设置是否注册全限定名，
            kryo.setRegistrationRequired(false);
            // 设置初始化策略，如果没有默认无参构造器，那么就需要设置此项,使用此策略构造一个无参构造器
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    private static <T> Kryo getKryo(Class<T> clazz) {
        Kryo kryo = cachedKryo.get(clazz);
        if (kryo == null) {
            kryo = new Kryo();
            kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
            kryo.register(ArrayList.class, new ArrayListSerializer());
            kryo.register(java.sql.Date.class, new SqlDateSerializer());
            kryo.register(java.sql.Timestamp.class, new TimestampSerializer());
//            kryo.register(clazz, new JavaSerializer());
            //引用，对A对象序列化时，默认情况下kryo会在每个成员对象第一次序列化时写入一个数字，
            // 该数字逻辑上就代表了对该成员对象的引用，如果后续有引用指向该成员对象，
            // 则直接序列化之前存入的数字即可，而不需要再次序列化对象本身。
            // 这种默认策略对于成员存在互相引用的情况较有利，否则就会造成空间浪费
            // （因为没序列化一个成员对象，都多序列化一个数字），
            // 通常情况下可以将该策略关闭，kryo.setReferences(false);
            kryo.setReferences(false);
            // 设置是否注册全限定名，
            kryo.setRegistrationRequired(false);
            // 设置初始化策略，如果没有默认无参构造器，那么就需要设置此项,使用此策略构造一个无参构造器
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        }
        return kryo;
    }

    public static <T> byte[] serializeObject(Object obj, Class<T> cls) {
        try (Output _kryoOut = threadKryoOutput.get()) {
            Kryo _kryo = runtimeSerializationKryo.get();
//            _kryo.writeClassAndObject(_kryoOut, obj);
            _kryo.writeObjectOrNull(_kryoOut, obj, cls);
            return _kryoOut.toBytes();
        }
    }

//    public static <T> T deserializeObject(byte[] ser, Class<T> cls) {
//        try (Input _kryoInput = threadKryoInput.get()) {
//            _kryoInput.setBuffer(ser);
//            Kryo _kryo = getKryo(cls);
//            return _kryo.readObject(_kryoInput, cls);
//        }
//    }

    public static <T> T deserializeObject(byte[] ser, Class<T> cls) throws IOException {
        try (Input _kryoInput = threadKryoInput.get()) {
            _kryoInput.setBuffer(ser);
            Kryo _kryo = runtimeSerializationKryo.get();
            return _kryo.readObjectOrNull(_kryoInput, cls);
//            return _kryo.readClassAndObject(_kryoInput);
        }
    }
}
