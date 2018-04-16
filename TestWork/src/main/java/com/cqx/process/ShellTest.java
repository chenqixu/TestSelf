package com.cqx.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ShellTest extends Thread {
	private String cmd = "";
	private int shellRetCode = -1;
	private int status = 0;
	
	public ShellTest(String _cmd) {
		this.cmd = _cmd;
	}
	
	public void run(){
		Process process = null;
    	InputStreamReader isr = null;
    	BufferedReader br = null;
		try{
			process = Runtime.getRuntime().exec(cmd);
            isr = new InputStreamReader(process.getInputStream(), "GB2312");
            br = new BufferedReader(isr, 1024);            
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("info:"+line);
            }
            isr.close();
            br.close();
        	shellRetCode = process.waitFor();
        	this.status = 1;//1:完成
        	System.out.println("shellRetCode:"+shellRetCode+" status:"+status);
		}catch(Exception e){
			this.status = -1;//-1:异常
			e.printStackTrace();
			System.out.println(" status:"+status+" "+e.toString());
		}finally{
        	if(isr != null){
        		try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(br != null){
        		try {
        			br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
			if(process != null){
				process.destroy();
			}
		}
	}
	
	
	class LogThread extends Thread {
        InputStream is;

        String type;

        LogThread(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
        	InputStreamReader isr = null;
        	BufferedReader br = null;
            try {
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr, 1024);
                String line;
                while ((line = br.readLine()) != null) {
                    if (type.equals("err")) {
                    	System.out.println("err:"+line);
                    } else {
                    	System.out.println("info:"+line);
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
            	if(isr != null){
            		try {
						isr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            	if(br != null){
            		try {
            			br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            }
        }
    }
	
	public static void main(String[] args) {
		String cmd = args[0];
		System.out.println("[cmd]"+cmd);
		Thread t = new ShellTest(cmd);
		t.start();
	}
}
