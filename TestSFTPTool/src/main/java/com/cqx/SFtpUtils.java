package com.cqx;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SFtpUtils {
	private Session sshSession = null;
	private Channel channel = null;

	/**
	 * 获得SFTP连接通道
	 * @param ftpServerUser 用户
	 * @param ftpServerIp 服务器IP
	 * @param ftpServerPort 服务器端口
	 * @param ftpServerPassword 密码
	 */
	public ChannelSftp getChannel(String ftpServerUser, String ftpServerIp,
			int ftpServerPort, String ftpServerPassword) {
		ChannelSftp channelSftp = null;
		try {
			// 创建JSch对象
			JSch jsch = new JSch();
			// 根据用户名，主机ip，端口获取一个Session对象
			sshSession = jsch.getSession(ftpServerUser, ftpServerIp,
					ftpServerPort);
			// 设置密码
			sshSession.setPassword(ftpServerPassword);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			// 为Session对象设置properties
			sshSession.setConfig(sshConfig);
			// 通过Session建立链接
			sshSession.connect();
			// 打开SFTP通道
			channel = sshSession.openChannel("sftp");
			// 建立SFTP通道的连接
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			if(channel!=null&&channel.isConnected()){
				System.out.println(ftpServerUser+"@"+ftpServerIp+" SFTP login success.");
			}else{
				channelSftp = null;
			}
		} catch (Exception e) {
			System.out.println("%%%%%失败连接SFTP服务器：" + ftpServerIp + "，端口号："
					+ ftpServerPort + "，SFTP用户名：" + ftpServerUser + "，SFTP密码："
					+ ftpServerPassword);
		}
		return channelSftp;
	}
	
	/**
	 * 通过配置文件规则过滤出需要采集的文件列表，并下载到指定目录
	 * */
	public void dowload(ChannelSftp chSftp, String localFilePath, String remoteFilePath, String filenames){
//		Pattern pat;
//        Matcher mat;
		try{
			if(chSftp!=null && chSftp.isConnected()){
	        	//改变SFTP的工作目录
				chSftp.cd(remoteFilePath);
	        	//列出当前目录的所有文件，存放在Vector中
	        	Vector fileVector = chSftp.ls(remoteFilePath);
	        	System.out.println("SFTP列出当前目录("+remoteFilePath+")的所有文件大小:"+fileVector.size());
	        	//迭代Vector
	        	Iterator it = fileVector.iterator(); 
	        	//循环取出Vector中的文件名
	        	while(it.hasNext()){
	                boolean matched = false;
	        		//取出文件名
		            String fileName = ((LsEntry)it.next()).getFilename();
		            if(fileName.equals(filenames)){
		            	matched = true;
		            }
			        // 匹配规则，则从远程服务器下载文件到本地服务器
			        if(matched){
			        	SftpATTRS attr = chSftp.stat(fileName);
//			            long fileSize = attr.getSize();
			            // 同步的采集
			            chSftp.get(fileName, localFilePath+fileName);
//			            // 异步的采集，带进度条
//			            SFtpFileProgressMonitor sfpm = new SFtpFileProgressMonitor(fileSize);
//			        	chSftp.get(fileName, localFilePath+fileName, sfpm);
//			        	// 轮询直到采集完成
//			        	while(!sfpm.isEnd()){
//			        		Thread.sleep(500);
//			        	}
                		System.out.println("采集远程服务器文件["+fileName+"]完成.");
			        	// 成功下载文件，就移除远程服务器上的文件
                		break;
			        }
	        	}	        	
			}
		}catch(Exception e){
			System.out.println("%%%%%SFTP采集远程服务器："+remoteFilePath+"的文件时出错！！");
		}
	}

	/**
	 * 关闭SFTP通道连接和会话连接
	 */
	public void closeSftpConnection() {
		try {
			if (channel != null) {
				channel.disconnect();
			}
			if (sshSession != null) {
				sshSession.disconnect();
			}
			System.out.println("SFTP close.");
		} catch (Exception e) {
			System.out.println("%%%%%关闭SFTP连接出错！！！");
		}
	}
	
	/**
	 * 使用带有进度条的监控进行SFTP上传
	 * @param chSftp SFTP通道
	 * @param local_file 本地文件
	 * @param remote_file 远程文件
	 * */
	public void upload(ChannelSftp chSftp, String local_file, String remote_file){
		if(chSftp!=null && chSftp.isConnected()){
			File file = new File(local_file);
	        long fileSize = file.length();
	        System.out.println("SFTP upload begin . [local_file]"+local_file+"[remote_file]"+remote_file);
	        try {
	        	SFtpFileProgressMonitor sfpm = new SFtpFileProgressMonitor(fileSize);
				chSftp.put(local_file, remote_file, sfpm, ChannelSftp.OVERWRITE);
	        	// 轮询直到采集完成
	        	while(!sfpm.isEnd()){
	        		Thread.sleep(500);
	        	}
		        System.out.println("SFTP upload end . [local_file]"+local_file+"[remote_file]"+remote_file);
			} catch (Exception e) {
				System.out.println("%%%%%ChannelSftp.put ERROR(SFTP上传文件失败)！！！");
			}
		}
	}
	
//	public static void main(String[] args) {
//		InitConfFile.init("H:/Work/WorkSpace/luna/edc-bigdata-DSP-BOX-Encryption/src/main/resources/conf/filedcfg.xml");
//		SFtpUtils s = new SFtpUtils();
//		ChannelSftp chSftp = s.getChannel("hadoop", "10.1.8.1", 22, "hadoopA1!");
//        String local_file = "d:/FJBASS-代码检查表(Exadata).xls"; // 本地文件名
//        String remote_file = "/home/hadoop/data/DSP-BOX-DATA/FJBASS-代码检查表(Exadata).tmp"; // 目标文件名
//        // 上传文件
//        s.upload(chSftp, local_file, remote_file);
//        try {
//        	// 修改名称
//			chSftp.rename("/home/hadoop/data/DSP-BOX-DATA/FJBASS-代码检查表(Exadata).tmp"
//					,"/home/hadoop/data/DSP-BOX-DATA/FJBASS-代码检查表(Exadata).xls");
//		} catch (SftpException e) {
//			e.printStackTrace();
//		}
////        s.dowload(chSftp, "d:/Work/ETL/大数据盒子/DSP-BOX/data/", "/home/hadoop/data/DSP-BOX-DATA/");
//        s.closeSftpConnection();
//	}
}
