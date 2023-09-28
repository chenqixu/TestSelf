package com.bussiness.bi.bi_svc;

/**
 * NlEncryptUtil
 * <pre>
 *     新版本的连接密码的加密解密
 * </pre>
 *
 * @author chenqixu
 */
public class NlEncryptUtil {

    public NlEncryptUtil() {
    }

    public static String encryptByDES(String data) {
        return EncryptUtil.encrypt(data);
    }

    public static String decryptByDES(String data) {
        return EncryptUtil.decrypt(data);
    }

    public static String encryptByAES(String data) {
        String re_str = null;

        try {
            re_str = AesEncrypt.encrypt(data);
        } catch (Exception var3) {
            System.err.println("AES算法加密字符串异常：" + var3);
        }

        return re_str;
    }

    public static String encryptByAES(String data, String key) {
        String re_str = null;

        try {
            re_str = AesEncrypt.encrypt(data, key);
        } catch (Exception var4) {
            System.err.println("AES算法加密字符串异常：" + var4);
        }

        return re_str;
    }

    public static String decryptByAES(String data) {
        String re_str = null;

        try {
            re_str = AesEncrypt.decrypt(data);
        } catch (Exception var3) {
            System.err.println("AES算法解密字符串异常：" + var3);
        }

        return re_str;
    }

    public static String decryptByAES(String data, String key) {
        String re_str = null;

        try {
            re_str = AesEncrypt.decrypt(data, key);
        } catch (Exception var4) {
            System.err.println("AES算法解密字符串异常：" + var4);
        }

        return re_str;
    }

    public static String encryptByAES256(String data) {
        String re_str = null;

        try {
            re_str = AesEncrypt.encrypt256(data);
        } catch (Exception var3) {
            System.err.println("AES算法加密字符串异常：" + var3);
        }

        return re_str;
    }

    public static String encryptByAES256(String data, String key) {
        String re_str = null;

        try {
            re_str = AesEncrypt.encrypt256(data, key);
        } catch (Exception var4) {
            System.err.println("AES算法加密字符串异常：" + var4);
        }

        return re_str;
    }

    public static String decryptByAES256(String data) {
        String re_str = null;

        try {
            re_str = AesEncrypt.decrypt256(data);
        } catch (Exception var3) {
            System.err.println("AES算法解密字符串异常：" + var3);
        }

        return re_str;
    }

    public static String decryptByAES256(String data, String key) {
        String re_str = null;

        try {
            re_str = AesEncrypt.decrypt256(data, key);
        } catch (Exception var4) {
            System.err.println("AES算法解密字符串异常：" + var4);
        }

        return re_str;
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            String type = args[0];
            String passwd = args[1];
            System.out.println("输入参数，类型：" + type + "，密码：" + passwd);
            switch (type.toLowerCase()) {
                case "encrypt":
                    System.out.println("加密后的数据=" + encryptByAES(passwd));
                    break;
                case "decrypt":
                    System.out.println("解密后的数据=" + decryptByAES(passwd));
                    break;
                default:
                    System.out.println("类型无法识别：类型必须是 encrypt/decrypt，加密解密");
                    break;
            }
        } else {
            System.out.println("需要2个参数，1：encrypt/decrypt，加密解密；2：密码；");
        }
    }
}
