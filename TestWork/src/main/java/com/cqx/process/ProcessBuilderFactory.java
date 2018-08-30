package com.cqx.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessBuilderFactory {
	private static ProcessBuilderFactory instance;
	
	private ProcessBuilderFactory () {}
	
	/**
	 * 单态
	 * */
	public static ProcessBuilderFactory getInstance () {
		if (instance == null) {
			synchronized (ProcessBuilderFactory.class) {
				if (instance == null) {
					instance = new ProcessBuilderFactory();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 运行命令
	 * */
	public int execCmd(String... cmd){
		ProcessBuilder builder = null;
		Process process = null;
		int resultcode = -1;
		builder = new ProcessBuilder(cmd);
		try {
			process = builder.start();
			runLog(process);
			resultcode = process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			release(process);
		}
		return resultcode;
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
	 * 资源释放
	 * */
	private void release(Process process) {
		if (process != null) {
			LogInfoFactory.info("process.destroy");
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
	                	LogInfoFactory.info("##错误日志##"+line);
	                } else {
	                	LogInfoFactory.info("##内容##"+line);
	                }
	            }
	        } catch (IOException ioe) {
	            LogInfoFactory.err("创建/读取 IO异常", ioe);
	        } finally {
	        	if(isr != null){
	        		try {
						isr.close();
					} catch (IOException e) {
						LogInfoFactory.err("InputStreamReader流关闭IO异常", e);
					}
	        	}
	        	if(br != null){
	        		try {
	        			br.close();
					} catch (IOException e) {
						LogInfoFactory.err("BufferedReader流关闭IO异常", e);
					}
	        	}
	        }
	    }
	}
}
