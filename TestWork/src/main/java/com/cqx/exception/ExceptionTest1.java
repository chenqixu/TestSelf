package com.cqx.exception;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionTest1 {
	private static final Logger log = LoggerFactory.getLogger(ExceptionTest1.class);
	
	public void throw1() throws FujianBIException {
		throw new FujianBIException(ErrorCode.FJBIE000, null, "bishow");
	}
	
	public void throwIOException() throws IOException {
		throw new IOException("抛出IO异常");
	}
	
	public void test1() {
		try {
			throw1();
		} catch (Exception e) {
			log.error("具体错误：" + e.getMessage(), e);
		}
	}
	
	public void test2() {
		try {
			throwIOException();
		} catch (IOException e) {
			try {
				throw new FujianBIException(ErrorCode.FJBIE000, e, "bishow");
			} catch (Exception ex) {
				log.error("具体错误：" + ex.getMessage(), ex);
			}
		}
	}
	
	public void test3() {
		try {
			throwIOException();
		} catch (IOException e) {
			log.error("具体错误：" + e.getMessage(), new FujianBIException(ErrorCode.FJBIE000, e, "bishow"));
			log.info("test3");
		}
	}
	
	public static void main(String[] args) {
		new ExceptionTest1().test3();
	}
}
