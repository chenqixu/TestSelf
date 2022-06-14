package com.bussiness.bi.bigdata.thread;

import java.io.FileNotFoundException;

import com.enterprisedt.net.ftp.FTPException;

public class CollectionException {
	public static void catchFtpException(FTPException e) throws Exception {
		if(e!=null){
			if(e.getMessage().contains(" not found."))
				throw new FileNotFoundException(e.getMessage());
		}
	}
}
