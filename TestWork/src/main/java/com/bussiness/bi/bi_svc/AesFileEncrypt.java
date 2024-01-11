package com.bussiness.bi.bi_svc;

import com.cqx.common.utils.system.ByteUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * AES文件加密解密处理
 *
 * @author chenqixu
 */
public class AesFileEncrypt {
    private static final Logger logger = LoggerFactory.getLogger(AesFileEncrypt.class);
    /**
     * 加密算法
     */
    private static final String ENCRY_ALGORITHM = "AES";
    /**
     * 加密算法/加密模式/填充类型
     * 本例采用AES加密，ECB加密模式，PKCS5Padding填充
     */
    private static final String CIPHER_MODE = "AES/ECB/PKCS5Padding";
    /**
     * 设置iv偏移量
     * 本例采用ECB加密模式，不需要设置iv偏移量
     */
    private static final String IV_ = null;
    /**
     * 设置加密字符集
     * 本例采用 UTF-8 字符集
     */
    private static final String CHARACTER = "UTF-8";
    /**
     * 设置加密密码处理长度。
     * 不足此长度补0；
     */
    private static final int PWD_SIZE = 16;

    public static byte[] EVPBytesToKey1(String _password) {
        byte[] password = _password.getBytes(); // 设置密码

        MD5Digest digest = new MD5Digest();
        digest.update(password, 0, password.length);
        byte[] derivedKeyMaterial = new byte[digest.getDigestSize()];
        digest.doFinal(derivedKeyMaterial, 0);
        return derivedKeyMaterial;
    }

    public static byte[] EVPBytesToKey2(String _password) {
        byte[] password = _password.getBytes(); // 设置密码

        PBEParametersGenerator gen = new OpenSSLPBEParametersGenerator();
        gen.init(password, new byte[]{}, 1);
        KeyParameter keyParam = (KeyParameter) gen.generateDerivedParameters(128);
        return keyParam.getKey();
    }

    public static void encryptFile(String inputFileName, String outputFileName, String password) throws Exception {
        encryptFile(inputFileName, outputFileName, password, "K");
    }

    public static void encryptFile(String inputFileName, String outputFileName, String password, String type) throws Exception {
        // 创建加密器
        // 1 获取加密密钥
        SecretKeySpec keySpec;
        switch (type) {
            case "K":
                keySpec = new SecretKeySpec(ByteUtil.hexStringToBytes(password), ENCRY_ALGORITHM);
                break;
            case "k2":
                keySpec = new SecretKeySpec(EVPBytesToKey2(password), ENCRY_ALGORITHM);
                break;
            case "k":
            case "k1":
            default:
                keySpec = new SecretKeySpec(EVPBytesToKey1(password), ENCRY_ALGORITHM);
                break;
        }

        // 2 获取Cipher实例
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        // 查看数据块位数 默认为16（byte） * 8 =128 bit
//            System.out.println("数据块位数(byte)：" + cipher.getBlockSize());
        // 3 初始化Cipher实例。设置执行模式以及加密密钥

        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // 读取输入文件内容
        // 写入加密后的结果到输出文件
        try (FileInputStream fis = new FileInputStream(inputFileName);
             FileOutputStream fos = new FileOutputStream(outputFileName);
             OutputStream os = new CipherOutputStream(fos, cipher)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void decryptFile(String inputFileName, String outputFileName, String password) throws Exception {
        // 创建加密器
        // 1 获取加密密钥
        SecretKeySpec keySpec = new SecretKeySpec(ByteUtil.hexStringToBytes(password), ENCRY_ALGORITHM);

        // 2 获取Cipher实例
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        // 查看数据块位数 默认为16（byte） * 8 =128 bit
//            System.out.println("数据块位数(byte)：" + cipher.getBlockSize());
        // 3 初始化Cipher实例。设置执行模式以及加密密钥

        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        // 读取输入文件内容
        // 写入加密后的结果到输出文件
        try (FileInputStream fis = new FileInputStream(inputFileName);
             InputStream is = new CipherInputStream(fis, cipher);
             FileOutputStream fos = new FileOutputStream(outputFileName)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 密码处理方法
     * 如果加解密出问题，
     * 请先查看本方法，排除密码长度不足补"0",导致密码不一致
     *
     * @param password 待处理的密码
     * @return
     */
    private static byte[] pwdHandler(String password) {
        byte[] data;
        if (password == null) {
            password = "";
        }
        StringBuilder sb = new StringBuilder(PWD_SIZE);
        sb.append(password);
        while (sb.length() < PWD_SIZE) {
            sb.append("0");
        }
        if (sb.length() > PWD_SIZE) {
            sb.setLength(PWD_SIZE);
        }
        data = sb.toString().getBytes(StandardCharsets.UTF_8);
        return data;
    }

    //======================>原始加密<======================

    /**
     * 原始加密
     *
     * @param clearTextBytes 明文字节数组，待加密的字节数组
     * @param pwdBytes       加密密码字节数组
     * @return 返回加密后的密文字节数组，加密错误返回null
     */
    public static byte[] encrypt(byte[] clearTextBytes, byte[] pwdBytes) throws Exception {
        // 1 获取加密密钥
        SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);

        // 2 获取Cipher实例
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        // 查看数据块位数 默认为16（byte） * 8 =128 bit
//            System.out.println("数据块位数(byte)：" + cipher.getBlockSize());

        // 3 初始化Cipher实例。设置执行模式以及加密密钥
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // 4 执行 并 返回密文字符集
        return cipher.doFinal(clearTextBytes);
    }

    /**
     * 原始解密
     *
     * @param cipherTextBytes 密文字节数组，待解密的字节数组
     * @param pwdBytes        解密密码字节数组
     * @return 返回解密后的明文字节数组，解密错误返回null
     */
    public static byte[] decrypt(byte[] cipherTextBytes, byte[] pwdBytes) throws Exception {
        // 1 获取解密密钥
        SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);

        // 2 获取Cipher实例
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        // 查看数据块位数 默认为16（byte） * 8 =128 bit
//            System.out.println("数据块位数(byte)：" + cipher.getBlockSize());

        // 3 初始化Cipher实例。设置执行模式以及加密密钥
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        // 4 执行 并 返回明文字符集
        return cipher.doFinal(cipherTextBytes);
    }

    //======================>BASE64<======================

    /**
     * BASE64加密
     *
     * @param clearText 明文，待加密的内容
     * @param password  密码，加密的密码
     * @return 返回密文，加密后得到的内容。加密错误返回null
     */
    public static String encryptBase64(String clearText, String password) throws Exception {
        // 1 获取加密密文字节数组
        byte[] cipherTextBytes = encrypt(clearText.getBytes(CHARACTER), pwdHandler(password));

        // 2 对密文字节数组进行BASE64 encoder 得到 BASE6输出的密文
        BASE64Encoder base64Encoder = new BASE64Encoder();

        // 3 返回BASE64输出的密文
        return base64Encoder.encode(cipherTextBytes);
    }

    /**
     * BASE64解密
     *
     * @param cipherText 密文，带解密的内容
     * @param password   密码，解密的密码
     * @return 返回明文，解密后得到的内容。解密错误返回null
     */
    public static String decryptBase64(String cipherText, String password) throws Exception {
        // 1 对 BASE64输出的密文进行BASE64 decodebuffer 得到密文字节数组
        BASE64Decoder base64Decoder = new BASE64Decoder();
        byte[] cipherTextBytes = base64Decoder.decodeBuffer(cipherText);

        // 2 对密文字节数组进行解密 得到明文字节数组
        byte[] clearTextBytes = decrypt(cipherTextBytes, pwdHandler(password));

        // 3 根据 CHARACTER 转码，返回明文字符串
        return new String(clearTextBytes, CHARACTER);
    }

    //======================>HEX<======================

    /**
     * HEX加密
     *
     * @param clearText 明文，待加密的内容
     * @param password  密码，加密的密码
     * @return 返回密文，加密后得到的内容。加密错误返回null
     */
    public static String encryptHex(String clearText, String password) throws Exception {
        // 1 获取加密密文字节数组
        byte[] cipherTextBytes = encrypt(clearText.getBytes(CHARACTER), pwdHandler(password));

        // 2 对密文字节数组进行 转换为 HEX输出密文 并 返回
        return byte2hex(cipherTextBytes);
    }

    /**
     * HEX解密
     *
     * @param cipherText 密文，带解密的内容
     * @param password   密码，解密的密码
     * @return 返回明文，解密后得到的内容。解密错误返回null
     */
    public static String decryptHex(String cipherText, String password) throws Exception {
        // 1 将HEX输出密文 转为密文字节数组
        byte[] cipherTextBytes = hex2byte(cipherText);

        // 2 将密文字节数组进行解密 得到明文字节数组
        byte[] clearTextBytes = decrypt(cipherTextBytes, pwdHandler(password));

        // 3 根据 CHARACTER 转码，返回明文字符串
        return new String(clearTextBytes, CHARACTER);
    }

    /**
     * 字节数组转成16进制字符串
     *
     * @param bytes
     * @return
     */
    public static String byte2hex(byte[] bytes) {
        // 一个字节的数
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        String tmp;
        for (byte aByte : bytes) {
            // 整数转成十六进制表示
            tmp = (Integer.toHexString(aByte & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成大写
    }

    /**
     * 将hex字符串转换成字节数组
     *
     * @param str
     * @return
     */
    private static byte[] hex2byte(String str) {
        if (str == null || str.length() < 2) {
            return new byte[0];
        }
        str = str.toLowerCase();
        int l = str.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = str.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }

    /**
     * 转md5 bytes
     *
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] md5Byte(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        return messageDigest.digest();
    }

    public static void main(String[] args) throws Exception {
//        String test = encryptHex("test", "1234567800000000");
//        System.out.println(test);
//        System.out.println(decryptHex(test, "1234567800000000"));
        String key = "ydjfsjbs";
        String imgMD5 = DigestUtils.md5Hex(key);
        String password = "93b396cbce94a8aef3faf3d2ffb1c2e7";
        byte[] pa1 = ByteUtil.hexStringToBytes(password);
        byte[] pa2 = md5Byte(key);
        byte[] pa3_1 = EVPBytesToKey1(password);
        byte[] pa3_2 = EVPBytesToKey2(password);
        logger.info("imgMD5={}, Arrays.equals(pa1, pa2)={}", imgMD5, Arrays.equals(pa1, pa2));
        logger.info("iArrays.equals(pa3_1, pa3_2)={}", Arrays.equals(pa3_1, pa3_2));
        String path = "d:\\tmp\\data\\";
        encryptFile(path + "1.txt"
                , path + "1.txt.bin"
                , password, "k2");
//        decryptFile(path + "12.txt.bin"
//                , path + "12.txt"
//                , password);
    }
}
