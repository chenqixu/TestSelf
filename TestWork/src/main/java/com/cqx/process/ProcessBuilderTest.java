package com.cqx.process;

import java.util.ArrayList;
import java.util.List;

public class ProcessBuilderTest {
	private ProcessBuilder builder = new ProcessBuilder();
	private List<String> list = new ArrayList<String>();
	private Process process = null;
	public void addcmd(String cmd) {
		list.add(cmd);
	}
	public void execlist() {
		try {
			if (list.size()==0) return;
			process = builder.command(list).start();
			runlog();
			process.waitFor();
			list.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			release();
		}
	}
	public void runtime(String cmd) {
		try {
			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec(cmd);
			runlog();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			release();
		}
	}
	private void runlog() {
        LogThread ltinfo = new LogThread(process.getInputStream(), "info");
        LogThread lterr = new LogThread(process.getErrorStream(), "err");
        ltinfo.start();
        lterr.start();
	}
	public void release() {
		if(process != null){
			process.destroy();
		}
	}
	
	public static void main(String[] args) {
		ProcessBuilderTest pbt = new ProcessBuilderTest();
		pbt.addcmd("cmd.exe");
		pbt.addcmd("/c");
		pbt.addcmd("dir");
		pbt.addcmd("f:\\");
		pbt.execlist();
		pbt.addcmd("cmd.exe");
		pbt.addcmd("/c");
		pbt.addcmd("del");
		pbt.addcmd("d:\\23.txt");
		pbt.execlist();
	}
}
