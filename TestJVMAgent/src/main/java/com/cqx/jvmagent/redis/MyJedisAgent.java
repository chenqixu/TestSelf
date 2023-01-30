package com.cqx.jvmagent.redis;

import com.cqx.common.utils.file.FileUtil;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * 使用自己的类替换jedis中想要替换的类，使用代理进行注入<br>
 * 需要在执行命令中加上-javaagent:路径/jar包
 *
 * @author chenqixu
 */
public class MyJedisAgent {

    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader
                    , String className
                    , Class<?> classBeingRedefined
                    , ProtectionDomain protectionDomain
                    , byte[] classfileBuffer) throws IllegalClassFormatException {
                if (className.equals("redis/clients/jedis/JedisPool")) {
                    // as found in the repository
                    // Consider removing the transformer for future class loading
                    // 简单粗暴方式
                    return FileUtil.getClassBytes("I:\\Document\\Workspaces\\Git\\FujianBI\\etl-jstorm\\nl-rt-jstorm-fujianbi-common\\target\\classes\\redis\\clients\\jedis\\JedisPool.class");
                    // 从jar包获取的比较合适的方式，注意：需要jar包隔离，代理和业务不要在同一个jar中
//                    ClassUtil classUtil = new ClassUtil();
//                    return classUtil.getClassfileBuffer("JedisPool", "redis/clients/jedis/JedisPool");
                } else {
                    // skips instrumentation for other classes
                    return null;
                }
            }
        });
    }
}
