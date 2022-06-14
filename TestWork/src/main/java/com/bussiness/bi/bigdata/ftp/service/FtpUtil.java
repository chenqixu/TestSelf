package com.bussiness.bi.bigdata.ftp.service;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FTP工具类
 * 
 * @author Leon
 * 
 */
public class FtpUtil {
	public static void getDetailList(PrintWriter pw, String path) {
		File dir = new File(path);
		if (!dir.isDirectory()) {
			pw.println("500 No such file or directory./r/n");
		}
		File[] files = dir.listFiles();
		String modifyDate;
		for (int i = 0; i < files.length; i++) {
			modifyDate = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
					.format(new Date(files[i].lastModified()));
			if (files[i].isDirectory()) {
				pw.println("drwxr-xr-x ftp   ftp      0 " + modifyDate + " "
						+ files[i].getName());
			} else {
				pw.println("-rw-r-r--1 ftp   ftp      " + files[i].length()
						+ " " + modifyDate + " " + files[i].getName());
			}
			pw.flush();
		}
		pw.println("total:" + files.length);
	}
}
