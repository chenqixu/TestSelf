package com.newland.bi.bigdata.ftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

public class SftpConnection {
	//SSH channel
	private ChannelSftp channelSftp;
	//SSH Session
	private Session sshSession ;
	public ChannelSftp getChannelSftp() {
		return channelSftp;
	}
	public void setChannelSftp(ChannelSftp channelSftp) {
		this.channelSftp = channelSftp;
	}
	public Session getSshSession() {
		return sshSession;
	}
	public void setSshSession(Session sshSession) {
		this.sshSession = sshSession;
	}

}
