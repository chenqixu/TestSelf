package com.cqx.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//import org.apache.hadoop.util.ShutdownHookManager;

public class ProcessBuilderTest {

	private LogInfoFactory log = LogInfoFactory.getInstance();
	private ProcessBuilder builder = null;
	private List<String> list = null;
	private Process process = null;
	
//	public ProcessBuilderTest(){
//		ShutdownHookManager.get().isShutdownInProgress();
//	}
	
	/**
	 * 增加cmd到list中
	 * */
	public void addCmd(String cmd) {
		if(list==null){
			list = new ArrayList<String>();
		}
		list.add(cmd);
	}
	
	/**
	 * 使用ProcessBuilder运行cmdlist
	 * */
	public void execList() {
		try {
			if (list.size()==0) return;
			builder = new ProcessBuilder();
			process = builder.command(list).start();
			runLog();
			int resultcode = process.waitFor();
			log.info("##resultcode##"+resultcode);
			list.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			release();
		}
	}
	
	/**
	 * ProcessBuilder运行cmd
	 * */
	public void execCmd(String... cmd){
		builder = new ProcessBuilder(cmd);
		BufferedReader stdInput = null;
		BufferedReader stdError = null;
		try {
			log.info("builder.start");
			process = builder.start();
			stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			stdError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
			String echo;
			while ((echo = stdInput.readLine()) != null) {
				log.info("##正确日志##"+echo);
			}
			while ((echo = stdError.readLine()) != null) {
				if (echo.contains(" ERROR ")) {
					log.info("##错误日志##"+ echo);
				} else {
					log.info("##日志##"+ echo);
				}
			}
//			runLog();
			int resultcode = process.waitFor();
			log.info("##resultcode##"+resultcode);
		} catch (IOException e) {
			log.err("IO异常", e);
		} catch (InterruptedException e) {
			log.err("中断异常", e);
		} finally {
//			if(stdInput!=null)
				try {
					stdInput.close();
				} catch (IOException e) {
					log.err("process.getInputStream流关闭IO异常", e);
				}
			if(stdError!=null)
				try {
					stdError.close();
				} catch (IOException e) {
					log.err("process.getErrorStream流关闭IO异常", e);
				}
			release();
		}
	}
	
	/**
	 * runtime运行cmd
	 * */
	public void runtimeCmd(String cmd) {
		try {
			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec(cmd);
			runLog();
			int resultcode = process.waitFor();
			log.info("##resultcode##"+resultcode);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			release();
		}
	}
	
	/**
	 * 运行日志
	 * */
	private void runLog() {
        LogThread ltinfo = new LogThread(process.getInputStream(), "info");
        LogThread lterr = new LogThread(process.getErrorStream(), "err");
        ltinfo.start();
        lterr.start();
	}
	
	/**
	 * 资源释放
	 * */
	public void release() {
		if(process != null){
			log.info("process.destroy");
			process.destroy();
		}
	}
	
	public static void main(String[] args) {
		ProcessBuilderFactory1 pbf1 = new ProcessBuilderFactory1();
		pbf1.execCmd("cmd.exe","/c","dir","f:\\");
		
//		ProcessBuilderTest pbt = new ProcessBuilderTest();
//		pbt.execCmd("cmd.exe","/c","dir","f:\\");
//		pbt.execCmd("ftp 10.1.4.185");
		
//		pbt.addCmd("cmd.exe");
//		pbt.addCmd("/c");
//		pbt.addCmd("dir");
//		pbt.addCmd("f:\\");
//		pbt.execList();
		
//		pbt.addCmd("cmd.exe");
//		pbt.addCmd("/c");
//		pbt.addCmd("del");
//		pbt.addCmd("d:\\23.txt");
//		pbt.execList();
	}
}
