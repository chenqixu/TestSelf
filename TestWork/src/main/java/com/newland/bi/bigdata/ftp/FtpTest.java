package com.newland.bi.bigdata.ftp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

public class FtpTest {
	//扫描FTP是否循环子目录 V1.0.1 add
	public static boolean ifRoundSubdirectory = false;

    public static List<String> listFtpFiles(FTPClient ftpClient,String remoteFilePath,FTPFileFilter fileFilter) {
    	List<String> fileList = new ArrayList<String>();
        try {
        	//改变FTP的工作目录
        	ftpClient.changeWorkingDirectory(remoteFilePath);
            //描述指定目录，取出指定后缀名的文件，放入数组中
        	FTPFile[] fileArrays = ftpClient.listFiles(remoteFilePath, fileFilter);
            //把数组转为List
            for (int i = 0; i < fileArrays.length; i++) {
            	//转换成List
                if (fileArrays[i].isFile()) {
                	//取出文件名
                    String fileName = fileArrays[i].getName().toString();
                    fileName = new String(fileName.getBytes(), ftpClient.getControlEncoding());
                    //把文件名放入List中
                    fileList.add(fileName);
                    System.out.println("[fileList.add]"+fileName);
                }
                //扫描FTP是否循环子目录 V1.0.1
                else if(fileArrays[i].isDirectory()){
                	String newpath = remoteFilePath + fileArrays[i].getName();
                	System.out.println(newpath+"[is Path]");
                	fileList.addAll(listFtpFiles(ftpClient, newpath, fileFilter));
                }
            }
            
        }catch (Exception e) {
        	e.printStackTrace();
        }
        return fileList;
    }
    
    public static boolean mkMultiDirectory(FTPClient client, String dir){
    	String path = dir.replace("//", "/");
    	String[] dirs = path.split("/");
    	for (int i = 0; i < dirs.length; i++) {
			String tmpdir = dirs[i];
			try{
				if (!StringUtils.isEmpty(tmpdir)) {
					//判断是否存在,存在就change
					//不存在就创建,然后change
					if (!client.changeWorkingDirectory(tmpdir)) {
						client.makeDirectory(tmpdir);
						client.changeWorkingDirectory(tmpdir);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
    	return true;
    }
    
    public static void test1(){
		ifRoundSubdirectory = true;
    	FTPClient ftpClient = null;
    	String ftpServerIp = "192.168.230.101";//"10.1.8.6";
    	int ftpServerPort = 21;
    	String ftpServerUser = "hadoop";
    	String ftpServerPassword = "hadoop";//"hadoopA1!";
    	String remoteFilePath = "/home/hadoop/data/DPIData";//"/data/HW_LteXdrCollect/";
    	String localFile = "d:/home/collector/sourceDir/1.csv";
    	List<String> fileList = new ArrayList<String>();
    	try {
    		ftpClient = new FTPClient();    		
    		ftpClient.connect(ftpServerIp, ftpServerPort);
			ftpClient.login(ftpServerUser, ftpServerPassword);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.setDataTimeout(300000);	        

	        ftpClient.changeWorkingDirectory("/");
			String path1 = "/home/hadoop/data/DPIData/20160106/01/";
			String path2 = "/home/hadoop/data/DPIData/";
			String path3 = "/home/hadoop/data/DPIDataBak/";
			String filename = "106_20160106163100_000.txt";
			int begin = path1.indexOf(path2);
			String subdiretory = path1.substring(begin+path2.length());			
			String newpath = path3+subdiretory;
			System.out.println("[newpath]"+newpath);
//			boolean change = ftpClient.changeWorkingDirectory(newpath);
//			System.out.println(change);
//			boolean makepath = mkMultiDirectory(ftpClient, newpath);
//			System.out.println(makepath);
//			change = ftpClient.changeWorkingDirectory(newpath);
//			System.out.println(change);
			boolean rename = ftpClient.rename(path1+filename, newpath+filename);
			System.out.println("[path1+filename]"+path1+filename);
			System.out.println("[newpath+filename]"+newpath+filename);
			System.out.println("[rename]"+rename);
	        
        	//改变FTP的工作目录
//        	ftpClient.changeWorkingDirectory(remoteFilePath);
//	        FTPFileFilter fileFilter = new DataColectorFTPFileFilter("chk");
//	        fileList = listFtpFiles(ftpClient, remoteFilePath, fileFilter);
//	        System.out.println(fileList.size());
//	        ftpClient.retrieveFileStream(remoteFilePath+"/1.csv");
//	        ftpClient.retrieveFile(remoteFilePath+"/1.csv", new FileOutputStream(new File(localFile)));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(ftpClient!=null){
				try {
					ftpClient.logout();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ftpClient=null;
		}
    }
    
    public static void test2(){
    	FTPClient ftpClient = null;
    	String ftpServerIp = "10.1.8.75";
    	int ftpServerPort = 21;
    	String ftpServerUser = "edc_base";
    	String ftpServerPassword = "edc_base";
    	try {
    		ftpClient = new FTPClient();    		
    		ftpClient.connect(ftpServerIp, ftpServerPort);
			ftpClient.login(ftpServerUser, ftpServerPassword);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.setDataTimeout(300000);
	        
	        ftpClient.changeWorkingDirectory("/home/edc_base/wjc/test/mArtificialIntelligence/m/20171215091512");
        	//扫描FTP的工作目录
	        for(FTPFile a : ftpClient.listFiles()){
	        	System.out.println(a.getName());
	        }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(ftpClient!=null){
				try {
					ftpClient.logout();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ftpClient=null;
		}
    }
    
    public static void test3(){
    	FTPClient ftpClient = null;
    	String ftpServerIp = "10.1.8.81";
    	int ftpServerPort = 21;
    	String ftpServerUser = "edc_base";
    	String ftpServerPassword = "edc_base";
    	try {
    		ftpClient = new FTPClient();    		
    		ftpClient.connect(ftpServerIp, ftpServerPort);
			ftpClient.login(ftpServerUser, ftpServerPassword);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.setDataTimeout(300000);
	        
	        ftpClient.changeWorkingDirectory("/test/cqx");
//        	//扫描FTP的工作目录
//	        for(FTPFile a : ftpClient.listFiles()){
//	        	System.out.println(a.getName());
//	        }
	        for(FTPFile a : ftpClient.listFiles("MA_001_001*")){
	        	System.out.println(a.getName());
	        }
//	        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
//	        ftpClient.sendCommand(FTPCmd.PWD);
////	        ftpClient.sendCommand(FTPCmd.TYPE, "binary");
////	        ftpClient.sendCommand(FTPCmd.USERNAME, "hadoop");
////	        ftpClient.sendCommand(FTPCmd.PASSWORD, "hadoop");
////	        ftpClient.sendCommand(FTPCmd.HELP);
////	        ftpClient.sendCommand(FTPCmd.PORT);
//	        ftpClient.sendCommand(FTPCmd.PASV);
//	        ftpClient.sendCommand(FTPCmd.LIST, "/home");
////	        ftpClient.sendCommand(FTPCmd.QUIT);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(ftpClient!=null){
				try {
					ftpClient.logout();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ftpClient=null;
		}
    }
    
    public static void test4() throws Exception{
    	String ftpServerIp = "10.1.8.81";
    	int ftpServerPort = 21;
    	String ftpServerUser = "edc_base";
    	String ftpServerPassword = "edc_base";
    	int timeout = 30000;
    	String contorlcharset = "UTF-8";
    	FtpUtil ftpUtil = new FtpUtil(ftpServerIp, ftpServerUser, ftpServerPassword,
    			ftpServerPort, timeout);
        ftpUtil.setContorlCharset(contorlcharset);
        ftpUtil.connectServer();
        ftpUtil.setPssiveMode();
        ftpUtil.setBinaryMode();
        int count = 0;
        for(com.enterprisedt.net.ftp.FTPFile ftpf : ftpUtil.getClient().dirDetails("/test/cqx/M*")){
//        	System.out.println(ftpf.getName());
        	count++;
        }
        System.out.println("[count]"+count);
        ftpUtil.disconnect();
    }
	
	public static void main(String[] args) throws Exception{
//		FtpTest.test3();
		FtpTest.test4();
	}
}
