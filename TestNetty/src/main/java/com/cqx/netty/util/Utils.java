package com.cqx.netty.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 工具类
 */
public class Utils {

    private Class cls = Utils.class;

    /**
     * 通过反射创建类
     *
     * @param cls
     * @param params
     * @return
     * @throws Exception
     */
    public static IServerHandler genrate(Class cls, Map<String, String> params) throws Exception {
        IServerHandler result;
        String clsName = cls.getName();
        String classSplit = "$";
        int classSplitIndex = clsName.indexOf(classSplit);
        if (classSplitIndex > 0) {//内部类
            //获取父类
            String superClsName = clsName.substring(0, classSplitIndex);
            Class superCls = Class.forName(superClsName);
            Object superObject = superCls.newInstance();
            Constructor<?> constructor = cls.getDeclaredConstructor(superCls);
            result = (IServerHandler) constructor.newInstance(superObject);
        } else {
            result = (IServerHandler) cls.newInstance();
        }
//        result.setParams(params);
        return result;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getNow() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(now);
    }

    public void classTest() throws IllegalAccessException, InstantiationException {
        System.out.println(cls);
        System.out.println(cls.newInstance());
    }

    public void readBuf(ByteBuf byteBuf) {
        byte[] arr1 = new byte[10240];
        byte[] arr2 = new byte[10240];
        byte[] arr3 = new byte[10240];
        byteBuf.readBytes(arr1);
        byteBuf.readBytes(arr2);
        byteBuf.readBytes(arr3);
        System.out.println("arr1：" + new String(arr1));
        System.out.println("arr2：" + new String(arr2));
        System.out.println("arr3：" + new String(arr3));
    }

    /**
     * 英文数字1个字节，中文3个字节
     *
     * @return
     */
    public ByteBuf writeBuf() {
        ByteBuf buf = Unpooled.buffer(3);
        byte[] dest = new byte[10240];
        byte[] src = "test1".getBytes();
        System.arraycopy(src, 0, dest, 0, src.length);
//        System.out.println(Arrays.toString(dest));
//        System.out.println(new String(dest));
        System.out.println("t1：" + "t1".getBytes().length);
        System.out.println("你：" + "你".getBytes().length);
        System.out.println("你好：" + "你好".getBytes().length);
        System.out.println("你好!：" + "你好!".getBytes().length);
        System.out.println("t1!@：" + "t1!@".getBytes().length);
        System.out.println("t1!@：：" + "t1!@：".getBytes().length);
        buf.writeBytes(dest);
        return buf;
    }
}
