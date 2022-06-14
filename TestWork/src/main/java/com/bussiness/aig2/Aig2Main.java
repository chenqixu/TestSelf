package com.bussiness.aig2;

public class Aig2Main {
	public static void main(String[] args) {
		String passwd = "123456";
		System.out.println("[source]"+passwd+" [md5]"+MD5.toMD5(passwd));
		passwd = "admin";
		System.out.println("[source]"+passwd+" [md5]"+MD5.toMD5(passwd));
		passwd = "234wer";
		System.out.println("[source]"+passwd+" [md5]"+MD5.toMD5(passwd));
		passwd = "000000";
		System.out.println("[source]"+passwd+" [md5]"+MD5.toMD5(passwd));
	}
}
