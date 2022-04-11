package com.cqx.common.utils.security;

import org.junit.Test;

public class SM4UtilTest {

    @Test
    public void generateKey() throws Exception {
        byte[] keys = SM4Util.generateKey();
        System.out.println(keys.length);
        for (byte b : keys) {
            System.out.println(b);
        }
    }

    @Test
    public void test() {
        try {
            System.out.println("开始****************************");
            String json = "{\"name\":\"测试SM4加密解密\",\"描述\":\"CSDN哈哈哈\"}";
            System.out.println("加密前：" + json);
            // 自定义的32位16进制秘钥
            String key = "86C63180C2806ED1F47B859DE501215B";
//            key = "1234567890";
            String cipher = SM4Util.encryptEcb(key, json);//sm4加密
            System.out.println("加密后：" + cipher);
            System.out.println("校验：" + SM4Util.verifyEcb(key, cipher, json));//校验加密前后是否为同一数据
            json = SM4Util.decryptEcb(key, cipher);//解密
            System.out.println("解密后：" + json);
            System.out.println("结束****************************");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        // 数据
        // b03e1b2e9b286283d7ca48ba33475722
        // 秘钥
        // 202204xiaoyao2bp
        try {
            System.out.println("开始****************************");
            String json = "13509323824";
//            System.out.println("加密前：" + json);
//            // 自定义的32位16进制秘钥
            String key = "244ab6bff7f9ca363a4a66e72c73cdd9";
//            String cipher = SM4Util.encryptEcb(key, json);// sm4加密
//            System.out.println("加密后：" + cipher);
//            System.out.println("校验：" + SM4Util.verifyEcb(key, cipher, json));// 校验加密前后是否为同一数据
//            json = SM4Util.decryptEcb(key, cipher);// 解密

            json = SM4Util.decryptEcb(key, "b548973bd61071bd860d9f903b95ae0f");// 解密
            System.out.println("解密后：" + json);
            System.out.println("结束****************************");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}