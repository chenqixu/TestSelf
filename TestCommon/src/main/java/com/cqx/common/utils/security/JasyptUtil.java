package com.cqx.common.utils.security;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig;

/**
 * JasyptUtil
 *
 * @author chenqixu
 */
public class JasyptUtil {
    /**
     * PBE 算法
     */
    public static final String PBE_ALGORITHMS_MD5_DES = "PBEWITHMD5ANDDES";
    public static final String PBE_ALGORITHMS_MD5_TRIPLEDES = "PBEWITHMD5ANDTRIPLEDES";
    public static final String PBE_ALGORITHMS_SHA1_DESEDE = "PBEWITHSHA1ANDDESEDE";
    public static final String PBE_ALGORITHMS_SHA1_RC2_40 = "PBEWITHSHA1ANDRC2_40";

    private JasyptUtil() {
    }

    /**
     * Jasypt 加密
     *
     * @param encryptedStr 加密字符串
     * @param password     盐值
     * @return
     */
    public static String encrypt(String encryptedStr, String password) {
        return encrypt(encryptedStr, PBE_ALGORITHMS_MD5_DES, password);
    }

    /**
     * Jasypt 加密
     *
     * @param encryptedStr 加密字符串
     * @param algorithm    加密算法
     *                     PBE ALGORITHMS: [PBEWITHMD5ANDDES, PBEWITHMD5ANDTRIPLEDES, PBEWITHSHA1ANDDESEDE, PBEWITHSHA1ANDRC2_40]
     * @param password     盐值
     * @return
     */
    public static String encrypt(String encryptedStr, String algorithm, String password) {
        // StandardPBEStringEncryptor、StandardPBEBigDecimalEncryptor、StandardPBEBigIntegerEncryptor、StandardPBEByteEncryptor
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        // 指定加密算法
        config.setAlgorithm(algorithm);
        // 加密盐值
        config.setPassword(password);
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        encryptor.setConfig(config);

        // 加密
        return encryptor.encrypt(encryptedStr);
    }

    /**
     * Jasypt 解密
     *
     * @param decryptStr 解密字符串
     * @param password   盐值
     * @return
     */
    public static String decrypt(String decryptStr, String password) {
        return decrypt(decryptStr, PBE_ALGORITHMS_MD5_DES, password);
    }

    /**
     * Jasypt 解密
     *
     * @param decryptStr 解密字符串
     * @param algorithm  指定解密算法：解密算法要与加密算法一一对应
     *                   PBE ALGORITHMS: [PBEWITHMD5ANDDES, PBEWITHMD5ANDTRIPLEDES, PBEWITHSHA1ANDDESEDE, PBEWITHSHA1ANDRC2_40]
     * @param password   盐值
     * @return
     */
    public static String decrypt(String decryptStr, String algorithm, String password) {
        // StandardPBEStringEncryptor、StandardPBEBigDecimalEncryptor、StandardPBEBigIntegerEncryptor、StandardPBEByteEncryptor
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentPBEConfig config = new EnvironmentPBEConfig();

        // 指定解密算法：解密算法要与加密算法一一对应
        config.setAlgorithm(algorithm);
        // 加密秘钥
        config.setPassword(password);
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        encryptor.setConfig(config);

        // 解密
        return encryptor.decrypt(decryptStr);
    }

    public static void main(String[] args) {
        String encryptedStr = "I am the string to be encrypted";
        String algorithm = PBE_ALGORITHMS_MD5_DES;
        String password = "$2a$10$xBamigs3qOolyNpQ6M.iO.6QqcAruWm4wgjmNyV9aZWTg/UUUnu3G";

//        String str = JasyptUtil.encrypt(encryptedStr, algorithm, password);
        String str = "fXqBUFp9rjE96LdGCPnUK7HKz1ygZgysWd26ID7g4gqnbAVGkFmLIRoTnYLeF9lE7BwVLAq9UAOtASuykIpEyQ==";
        System.out.println("加密后的字符串：" + str);
        System.out.println("解密后的字符串：" + JasyptUtil.decrypt(str, algorithm, password));
    }
}
