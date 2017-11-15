package com.newland.bi.bi_svc;

/*
 * Copyright (c) 2006 福建新大陆软件工程有限公司 版权所有
 * Newland Co. Ltd. All rights reserved.
 *
 * This software is the confidential and proprietary
 * information of Newland Co. Ltd.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with Newland Co. Ltd
 */
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * EncryptUtil.java
 * description: 
 * @author 汪钦堤
 * history 1.0.0 上午08:41:30 created by wangqindi
 */
public class EncryptUtil {
	private static final String PASSWORD_CRYPT_KEY = "__nlbi__";

	private final static String DES = "DES";

	/**
	 * 加密
	 * 
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回加密后的数据
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(src);
	}

	/**
	 * 解密
	 * 
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回解密后的原始数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// 现在，获取数据并解密
		// 正式执行解密操作
		return cipher.doFinal(src);
	}

	/**
	 * 解密
	 * @param data 解密数据
	 * @param key 密钥
	 * @return
	 */
	public final static String decrypt(String data ,String key) {
		try {
			return new String(decrypt(hex2byte(data.getBytes()),
					key.getBytes()));
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 * 密码解密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public final static String decrypt(String data) {
		try {
			return decrypt(data,PASSWORD_CRYPT_KEY);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 加密
	 * @param data 待加密数 
	 * @param key 密钥
	 * @return
	 */
	public final static String encrypt(String data,String key) {
		try {
			return byte2hex(encrypt(data.getBytes(), key
					.getBytes()));
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 密码加密
	 * 
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public final static String encrypt(String password) {
		try {
			return encrypt(password,PASSWORD_CRYPT_KEY);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 二行制转字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
	
	/**
	 * 字符串转asc码串
	 * @param str
	 * @return
	 */
	public static String strToAsc(String str){
		String returnStr = "";
		for(int i=0;i<str.length();i++){
			int asc = (int)str.charAt(i);
			if(i==0){
				returnStr = String.valueOf(asc);
			}else{
				returnStr = returnStr + "008008" +String.valueOf(asc);
			}
		}
		return returnStr;
	}
	
	/**
	 * asc码串转字符串
	 * @param str
	 * @return
	 */
	public static String ascToStr(String str){
		String returnStr = "";
		String []ascStr = str.split("008008");
		for(int i=0;i<ascStr.length;i++){
			int strInt = Integer.valueOf(ascStr[i]).intValue();
			char strs = (char)strInt;
			returnStr = returnStr + strs;
		}
		return returnStr;
	}
	
	

	public static void main(String[] args) {
		String pwd = "测试中文das" +
				"ddddd" +
				"dddd                         dffffffffffffffffffff             <dd>ewrewr</dd>dfaaaaaaa";
//		System.out.println("测试数据=" + pwd);
//		String data = strToAsc(encrypt(pwd));
//		System.out.println("加密后的数据data=" + data);
//		System.out.println("解密后=" + decrypt(ascToStr(data)));
		pwd = "000000";
//		String data = encrypt(pwd,"1324abckd");
		String data = encrypt(pwd, PASSWORD_CRYPT_KEY);
		System.out.println("加密后的数据data=" + data);
		System.out.println("解密后=" + decrypt(data, PASSWORD_CRYPT_KEY));
	}
}

