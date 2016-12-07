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
	 * ���SFTP����ͨ��
	 * @param ftpServerUser �û�
	 * @param ftpServerIp ������IP
	 * @param ftpServerPort �������˿�
	 * @param ftpServerPassword ����
	 */
	public ChannelSftp getChannel(String ftpServerUser, String ftpServerIp,
			int ftpServerPort, String ftpServerPassword) {
		ChannelSftp channelSftp = null;
		try {
			// ����JSch����
			JSch jsch = new JSch();
			// �����û���������ip���˿ڻ�ȡһ��Session����
			sshSession = jsch.getSession(ftpServerUser, ftpServerIp,
					ftpServerPort);
			// ��������
			sshSession.setPassword(ftpServerPassword);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			// ΪSession��������properties
			sshSession.setConfig(sshConfig);
			// ͨ��Session��������
			sshSession.connect();
			// ��SFTPͨ��
			channel = sshSession.openChannel("sftp");
			// ����SFTPͨ��������
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			if(channel!=null&&channel.isConnected()){
				System.out.println(ftpServerUser+"@"+ftpServerIp+" SFTP login success.");
			}else{
				channelSftp = null;
			}
		} catch (Exception e) {
			System.out.println("%%%%%ʧ������SFTP��������" + ftpServerIp + "���˿ںţ�"
					+ ftpServerPort + "��SFTP�û�����" + ftpServerUser + "��SFTP���룺"
					+ ftpServerPassword);
		}
		return channelSftp;
	}
	
	/**
	 * ͨ�������ļ�������˳���Ҫ�ɼ����ļ��б������ص�ָ��Ŀ¼
	 * */
	public void dowload(ChannelSftp chSftp, String localFilePath, String remoteFilePath, String filenames){
//		Pattern pat;
//        Matcher mat;
		try{
			if(chSftp!=null && chSftp.isConnected()){
	        	//�ı�SFTP�Ĺ���Ŀ¼
				chSftp.cd(remoteFilePath);
	        	//�г���ǰĿ¼�������ļ��������Vector��
	        	Vector fileVector = chSftp.ls(remoteFilePath);
	        	System.out.println("SFTP�г���ǰĿ¼("+remoteFilePath+")�������ļ���С:"+fileVector.size());
	        	//����Vector
	        	Iterator it = fileVector.iterator(); 
	        	//ѭ��ȡ��Vector�е��ļ���
	        	while(it.hasNext()){
	                boolean matched = false;
	        		//ȡ���ļ���
		            String fileName = ((LsEntry)it.next()).getFilename();
		            if(fileName.equals(filenames)){
		            	matched = true;
		            }
			        // ƥ��������Զ�̷����������ļ������ط�����
			        if(matched){
			        	SftpATTRS attr = chSftp.stat(fileName);
//			            long fileSize = attr.getSize();
			            // ͬ���Ĳɼ�
			            chSftp.get(fileName, localFilePath+fileName);
//			            // �첽�Ĳɼ�����������
//			            SFtpFileProgressMonitor sfpm = new SFtpFileProgressMonitor(fileSize);
//			        	chSftp.get(fileName, localFilePath+fileName, sfpm);
//			        	// ��ѯֱ���ɼ����
//			        	while(!sfpm.isEnd()){
//			        		Thread.sleep(500);
//			        	}
                		System.out.println("�ɼ�Զ�̷������ļ�["+fileName+"]���.");
			        	// �ɹ������ļ������Ƴ�Զ�̷������ϵ��ļ�
                		break;
			        }
	        	}	        	
			}
		}catch(Exception e){
			System.out.println("%%%%%SFTP�ɼ�Զ�̷�������"+remoteFilePath+"���ļ�ʱ������");
		}
	}

	/**
	 * �ر�SFTPͨ�����ӺͻỰ����
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
			System.out.println("%%%%%�ر�SFTP���ӳ�������");
		}
	}
	
	/**
	 * ʹ�ô��н������ļ�ؽ���SFTP�ϴ�
	 * @param chSftp SFTPͨ��
	 * @param local_file �����ļ�
	 * @param remote_file Զ���ļ�
	 * */
	public void upload(ChannelSftp chSftp, String local_file, String remote_file){
		if(chSftp!=null && chSftp.isConnected()){
			File file = new File(local_file);
	        long fileSize = file.length();
	        System.out.println("SFTP upload begin . [local_file]"+local_file+"[remote_file]"+remote_file);
	        try {
	        	SFtpFileProgressMonitor sfpm = new SFtpFileProgressMonitor(fileSize);
				chSftp.put(local_file, remote_file, sfpm, ChannelSftp.OVERWRITE);
	        	// ��ѯֱ���ɼ����
	        	while(!sfpm.isEnd()){
	        		Thread.sleep(500);
	        	}
		        System.out.println("SFTP upload end . [local_file]"+local_file+"[remote_file]"+remote_file);
			} catch (Exception e) {
				System.out.println("%%%%%ChannelSftp.put ERROR(SFTP�ϴ��ļ�ʧ��)������");
			}
		}
	}
	
//	public static void main(String[] args) {
//		InitConfFile.init("H:/Work/WorkSpace/luna/edc-bigdata-DSP-BOX-Encryption/src/main/resources/conf/filedcfg.xml");
//		SFtpUtils s = new SFtpUtils();
//		ChannelSftp chSftp = s.getChannel("hadoop", "10.1.8.1", 22, "hadoopA1!");
//        String local_file = "d:/FJBASS-�������(Exadata).xls"; // �����ļ���
//        String remote_file = "/home/hadoop/data/DSP-BOX-DATA/FJBASS-�������(Exadata).tmp"; // Ŀ���ļ���
//        // �ϴ��ļ�
//        s.upload(chSftp, local_file, remote_file);
//        try {
//        	// �޸�����
//			chSftp.rename("/home/hadoop/data/DSP-BOX-DATA/FJBASS-�������(Exadata).tmp"
//					,"/home/hadoop/data/DSP-BOX-DATA/FJBASS-�������(Exadata).xls");
//		} catch (SftpException e) {
//			e.printStackTrace();
//		}
////        s.dowload(chSftp, "d:/Work/ETL/�����ݺ���/DSP-BOX/data/", "/home/hadoop/data/DSP-BOX-DATA/");
//        s.closeSftpConnection();
//	}
}
