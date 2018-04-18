package com.cqx.process;

public class ShellTest extends Thread {
	private String cmd = "";
	private String[] cmdarr = {};
	private int shellRetCode = -1;
	private int status = 0;
	
	public ShellTest(String _cmd) {
		this.cmd = _cmd;
	}
	
	public ShellTest(String[] _cmdarr) {
		this.cmdarr = _cmdarr;
	}
	
	public void run(){
		Process process = null;
//    	InputStreamReader isr = null;
//    	BufferedReader br = null;
		try{
			if(cmd.length()>0)
				process = Runtime.getRuntime().exec(cmd);
			if(cmdarr.length>0)
				process = Runtime.getRuntime().exec(cmdarr);
/***************************************/
//			// 自行读日志
//            isr = new InputStreamReader(process.getInputStream(), "GB2312");
//            br = new BufferedReader(isr, 1024);            
//            String line;
//            while ((line = br.readLine()) != null) {
//                System.out.println("info:"+line);
//            }
//            isr.close();
//            br.close();
/***************************************/
			// 通过日志类读日志
            LogThread ltinfo = new LogThread(process.getInputStream(), "info");
            LogThread lterr = new LogThread(process.getErrorStream(), "err");
            ltinfo.start();
            lterr.start();
			// 等待直到完成
        	shellRetCode = process.waitFor();
        	this.status = 1;//1:完成
        	System.out.println("shellRetCode:"+shellRetCode+" status:"+status);
		}catch(Exception e){
			this.status = -1;//-1:异常
			e.printStackTrace();
			System.out.println(" status:"+status+" "+e.toString());
		}finally{
//        	if(isr != null){
//        		try {
//					isr.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//        	}
//        	if(br != null){
//        		try {
//        			br.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//        	}
			if(process != null){
				process.destroy();
			}
		}
	}
	
	public static void main(String[] args) {
//		String cmd = "cmd.exe /c dir d:\\";
//		String cmd = "cmd.exe /c del d:\\23.txt;dir d:\\";
//		String cmd = "cmd.exe /c dir c:\\;dir d:\\";
		String[] cmdarr = {"cmd.exe", "/c", "dir", "c:\\", "dir", "d:\\"};
//		System.out.println("[cmd]"+cmd);
		Thread t = new ShellTest(cmdarr);
		t.start();
	}
}
