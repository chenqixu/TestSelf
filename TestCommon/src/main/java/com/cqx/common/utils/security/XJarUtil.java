package com.cqx.common.utils.security;

import io.xjar.XCryptos;

/**
 * xjar是针对springboot的加密工具，加密了class文件
 *
 * @author chenqixu
 */
public class XJarUtil {

    public static void main(String[] args) {
        try {
            //设置密码
            String password = "1qaz2wsx3edc_ilaw_userCenter";
//            XKey xKey = XKit.key(password);
            String src = "d:\\tmp\\shudun\\security-recognition-protection-service-encry.jar";
            String dest = "d:\\tmp\\shudun\\security-recognition-protection-service-decrypted.jar";
//            XBoot.decrypt(src, dest, xKey);
            XCryptos.decrypt(src, dest, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
