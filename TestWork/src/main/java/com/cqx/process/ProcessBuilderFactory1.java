package com.cqx.process;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessBuilderFactory1 {
	private static MyLogger log = MyLoggerFactory.getLogger(ProcessBuilderFactory1.class);
	private boolean status = false;
	protected int resultcode = 0;
	protected ProcessBuilder builder = null;
	protected Process process = null;
	protected ProcessBuilderLogThread ltinfo = null;
	protected ProcessBuilderLogThread lterr = null;
		
	public boolean isStatus() {
		return status;
	}

	/**
	 * 运行命令
	 * */
	public int execCmd(String... cmd){
		status = false;
		int result = -1;
		builder = new ProcessBuilder(cmd);
		try {
			process = builder.start();
			runLog(process);
			result = waitFor();
			status = true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			release();
		}
		if(resultcode==-1)result=resultcode;
		return result;
	}	
	
	/**
	 * 运行日志
	 * */
	private void runLog(Process process) {
		ProcessBuilderLogThread ltinfo = new ProcessBuilderLogThread(process.getInputStream(), "info");
		ProcessBuilderLogThread lterr = new ProcessBuilderLogThread(process.getErrorStream(), "err");
        ltinfo.start();
        lterr.start();
	}
	
	/**
	 * 等待处理完成
	 * */
	private int waitFor() throws InterruptedException{
		// 资源释放前必须等待日志线程结束
		if(ltinfo!=null)
			try {
				ltinfo.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		if(lterr!=null)
			try {
				lterr.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		return process.waitFor();
	}
	
	/**
	 * 资源释放
	 * */
	private void release() {
		if (process != null) {
			log.info("process.destroy");
			process.destroy();
		}
	}
	
	/**
	 * 日志线程
	 * */
	private class ProcessBuilderLogThread extends Thread {
	    private InputStream is;
	    private String type;

	    public ProcessBuilderLogThread(InputStream is, String type) {
	        this.is = is;
	        this.type = type;
	    }

	    public void run() {
	    	InputStreamReader isr = null;
	    	BufferedReader br = null;
	        try {
	            isr = new InputStreamReader(is, "GB2312");
	            br = new BufferedReader(isr, 1024);
	            String line;
	            while ((line = br.readLine()) != null) {
	                if (type.equals("err")) {
	                	resultcode = -1;
	                	log.info("##错误日志##"+line);
	                } else {
	                	log.info("##内容##"+line);
	                }
	            }
	        } catch (IOException ioe) {
	        	log.error("创建/读取 IO异常", ioe);
	        } finally {
	        	if(isr != null){
	        		try {
						isr.close();
					} catch (IOException e) {
						log.error("InputStreamReader流关闭IO异常", e);
					}
	        	}
	        	if(br != null){
	        		try {
	        			br.close();
					} catch (IOException e) {
						log.error("BufferedReader流关闭IO异常", e);
					}
	        	}
	        }
	    }
	}
}
