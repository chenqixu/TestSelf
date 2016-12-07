package com.newland.bi.bigdata.ftp;

import java.io.File;

public class FileTest {
	public static void main(String[] args) {
		String path1 = "d:/home/collector/DPIdata/20150106/01/";
		String path2 = "d:/home/collector/DPIdata/";
		String path3 = "d:/home/collector/DPIdataBak/";
		
		int begin = path1.indexOf(path2);
		String subdiretory = path1.substring(begin+path2.length());
		System.out.println(subdiretory);
		System.out.println(path3+subdiretory);
		System.out.println(new File(path1).isDirectory());
		System.out.println(new File(path3+subdiretory).exists());
				
	}
}
