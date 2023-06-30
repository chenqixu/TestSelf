package com.bussiness.bi.bi_svc;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AesEncrypt
 *
 * @author chenqixu
 */
public class AesEncrypt {
    private static final String PASSWORD_CRYPT_KEY = "__cmccedc__";
    private static final String AES = "AES";
    private static final String AES_JS_KEY = "-nl*edc/cm%cc-!$";
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    public AesEncrypt() {
    }

    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(key);
        kgen.init(128, random);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(2, skeySpec);
        return cipher.doFinal(src);
    }

    public static final String decrypt(String data, String key) throws Exception {
        try {
            if (null == key || "".equals(key)) {
                key = "__cmccedc__";
            }

            return new String(decrypt((new BASE64Decoder()).decodeBuffer(data), key.getBytes("utf-8")), "utf-8");
        } catch (Exception var3) {
            var3.printStackTrace();
            throw new Exception(var3.getMessage());
        }
    }

    public static final String decrypt(String data) throws Exception {
        try {
            return decrypt(data, "__cmccedc__");
        } catch (Exception var2) {
            var2.printStackTrace();
            throw new Exception(var2.getMessage());
        }
    }

    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(key);
        kgen.init(128, random);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, skeySpec);
        return cipher.doFinal(src);
    }

    public static final String encrypt(String data, String key) throws Exception {
        try {
            if (null == key || "".equals(key)) {
                key = "__cmccedc__";
            }

            return (new BASE64Encoder()).encode(encrypt(data.getBytes("utf-8"), key.getBytes("utf-8")));
        } catch (Exception var3) {
            var3.printStackTrace();
            throw new Exception(var3.getMessage());
        }
    }

    public static final String encrypt(String password) throws Exception {
        try {
            return encrypt(password, "__cmccedc__");
        } catch (Exception var2) {
            var2.printStackTrace();
            throw new Exception(var2.getMessage());
        }
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for (int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }

        return hs.toUpperCase();
    }

    public static byte[] hex2byte(byte[] b) {
        if (b.length % 2 != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        } else {
            byte[] b2 = new byte[b.length / 2];

            for (int n = 0; n < b.length; n += 2) {
                String item = new String(b, n, 2);
                b2[n / 2] = (byte) Integer.parseInt(item, 16);
            }

            return b2;
        }
    }

    public static String strToAsc(String str) {
        String returnStr = "";

        for (int i = 0; i < str.length(); ++i) {
            int asc = str.charAt(i);
            if (i == 0) {
                returnStr = String.valueOf(asc);
            } else {
                returnStr = returnStr + "0x99" + asc;
            }
        }

        return returnStr;
    }

    public static String ascToStr(String str) {
        String returnStr = "";
        String[] ascStr = str.split("0x99");

        for (int i = 0; i < ascStr.length; ++i) {
            int strInt = Integer.valueOf(ascStr[i]);
            char strs = (char) strInt;
            returnStr = returnStr + strs;
        }

        return returnStr;
    }

    private static String base64Encode(byte[] bytes) {
        String base64Str = (new BASE64Encoder()).encode(bytes);
        return base64Str;
    }

    private static byte[] base64Decode(String base64Code) throws Exception {
        return isEmpty(base64Code) ? null : (new BASE64Decoder()).decodeBuffer(base64Code);
    }

    private static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, new SecretKeySpec(encryptKey.getBytes(), "AES"));
        return cipher.doFinal(content.getBytes("utf-8"));
    }

    public static String encrypt256(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    public static String encrypt256(String content) throws Exception {
        return encrypt256(content, "-nl*edc/cm%cc-!$");
    }

    private static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(2, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes, "utf-8");
    }

    public static String decrypt256(String encryptStr, String decryptKey) throws Exception {
        return isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

    public static String decrypt256(String encryptStr) throws Exception {
        return decrypt256(encryptStr, "-nl*edc/cm%cc-!$");
    }

    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
