package com.cqx.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.bd.utils.log.IDistributedLogger;

public class DistributedLogInfoFactory implements IDistributedLogger {
//	private static LogInfoFactory log = LogInfoFactory.getInstance();
	private static final Logger log = LoggerFactory.getLogger(DistributedLogInfoFactory.class);
	
	public DistributedLogInfoFactory() {
//		log.setLevel(3);
//		log.setNeedTime(false);
	}

	@Override
	public void debug(String msg) {
		log.debug(msg);
	}

	@Override
	public void debug(Throwable throwable) {
		debug(null, throwable);
	}

	@Override
	public void debug(String msg, Throwable throwable) {
		log.debug(msg, throwable);
	}

	@Override
	public void info(String msg) {
		log.info(msg);
	}

	@Override
	public void info(Throwable throwable) {
		info(null, throwable);
	}

	@Override
	public void info(String msg, Throwable throwable) {
		log.info(msg, throwable);
	}

	@Override
	public void warn(String msg) {
		log.warn(msg);
	}

	@Override
	public void warn(Throwable throwable) {
		warn(null, throwable);
	}

	@Override
	public void warn(String msg, Throwable throwable) {
		log.warn(msg, throwable);
	}

	@Override
	public void error(String msg) {
		log.error(msg);
	}

	@Override
	public void error(Throwable throwable) {
		error(null, throwable);
	}

	@Override
	public void error(String msg, Throwable throwable) {
		log.error(msg, throwable);
	}

	@Override
	public void fatal(String msg) {
	}

	@Override
	public void fatal(Throwable throwable) {		
	}

	@Override
	public void fatal(String msg, Throwable throwable) {		
	}

	@Override
	public String flush() {
		return null;
	}

	@Override
	public void setLogger(org.apache.log4j.Logger logger) {		
	}

}
