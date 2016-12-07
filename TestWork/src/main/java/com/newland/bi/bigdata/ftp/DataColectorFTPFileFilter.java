package com.newland.bi.bigdata.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

public class DataColectorFTPFileFilter implements FTPFileFilter{
	//文件名包含的内容
	private String fileNameInclude;
	public DataColectorFTPFileFilter(String fileNameInclude){
		this.fileNameInclude = fileNameInclude;
	}
	//文件过滤器，只返回后缀为ctl的文件
	@Override
	public boolean accept(FTPFile ftpfile) {
		boolean returnFlag = false;
		try {
			//ftp服务器上的文件名
			String fileName = ftpfile.getName();
			//扫描FTP是否循环子目录
			if(FtpTest.ifRoundSubdirectory){
				//如果是目录,就不用过滤,文件才需要按配置要求过滤
				if(ftpfile.isDirectory())return true;
			}
			if(fileName.endsWith(fileNameInclude))return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnFlag;
	}

}
