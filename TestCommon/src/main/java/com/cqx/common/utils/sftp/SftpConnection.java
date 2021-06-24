package com.cqx.common.utils.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import java.io.Closeable;
import java.io.IOException;

public class SftpConnection implements Closeable {
    //SSH的channel
    private ChannelSftp channelSftp;
    //SSH的Session
    private Session sshSession;

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

    @Override
    public void close() throws IOException {
        SftpUtil.closeSftpConnection(this);
    }
}
