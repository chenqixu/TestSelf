package com.newland.bi.bigdata.ftp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class SFtpTest {
	public static final String SFTP_CHANNEL = "sftp";
	public static final boolean ifRoundSubdirectory = true;
	public static final String dataSourceFileSuffixName = ".csv";

    /**
     * 
     * @description:  列出FTP服务器上指定目录下的指定文件名列表
     * @author:xixg
     * @date:2014-02-13
     * @param remoteFilePath 远端文件路径
     * @return List<String>
     */
    public static List<DownloadFileBean> sftpListFilesBean(SftpConnection sftpConnection,
    		String remoteFilePath) {
    	List<DownloadFileBean> fileList = new ArrayList<DownloadFileBean>();
        try {
        	ChannelSftp channelSftp = sftpConnection.getChannelSftp();
        	//改变SFTP的工作目录
        	channelSftp.cd(remoteFilePath);
        	//列出当前目录的所有文件，存放在Vector中
        	Vector fileVector = channelSftp.ls(remoteFilePath);
        	//迭代Vector
        	Iterator it = fileVector.iterator(); 
        	//循环取出Vector中的文件名
        	while(it.hasNext()) 
        	{
        		LsEntry lse = (LsEntry)it.next();
        		//是目录 并且需要循环子目录
        		if(ifRoundSubdirectory && lse.getAttrs().isDir()){
        			if(lse.getFilename().equals(".") || lse.getFilename().equals("..")){        				
        			}else{
            			String newpath = remoteFilePath+"/"+lse.getFilename();
            			System.out.println("[newpath]"+newpath);
            			fileList.addAll(sftpListFilesBean(sftpConnection, newpath));
        			}
        		}
        		//否则就是文件
        		else{
            		//取出文件名
    	            String fileName = lse.getFilename(); 
    	            //过滤出符合要求的文件名
    	            if(fileName.endsWith(dataSourceFileSuffixName)){
    	            	fileList.add(new DownloadFileBean(fileName, remoteFilePath));
    	            }
        		}
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        } 
        return fileList;
    }
	
	public static void main(String[] args) {
		SftpConnection sftpConnection = new SftpConnection();
		ChannelSftp channelSftp = null;
		Session sshSession = null;
    	String ftpServerIp = "192.168.230.101";//"10.1.8.6";
    	int ftpServerPort = 22;
    	String ftpServerUser = "hadoop";
    	String ftpServerPassword = "hadoop";//"hadoopA1!";
    	String remoteFilePath = "/home/hadoop/data/DPIData";//"/data/HW_LteXdrCollect/";
    	String localFile = "d:/home/collector/sourceDir/1.csv";
    	try {
    		JSch jsch = new JSch();
    		jsch.getSession(ftpServerUser, ftpServerIp, ftpServerPort);
    		sshSession = jsch.getSession(ftpServerUser, ftpServerIp, ftpServerPort);
    		sshSession.setPassword(ftpServerPassword);
    		Properties sshConfig = new Properties();
    		sshConfig.put("StrictHostKeyChecking", "no");
    		sshSession.setConfig(sshConfig);
    		sshSession.connect();
    		Channel channel = sshSession.openChannel(SFTP_CHANNEL);
    		channel.connect();
    		channelSftp = (ChannelSftp) channel;
    		sftpConnection.setChannelSftp(channelSftp);
    		sftpConnection.setSshSession(sshSession);
    		sftpListFilesBean(sftpConnection, remoteFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(channelSftp != null){
    			channelSftp.disconnect();
    		}
			channelSftp = null;
			if(sshSession != null){
    			sshSession.disconnect();
    		}
			sshSession = null;
		}
	}
}
