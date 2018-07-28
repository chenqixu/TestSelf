package com.cqx.zookeeper;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * zk的信息
 * */
public class ZkInfo implements Serializable {
	private static final long serialVersionUID = -914361806566542846L;
	private String zkServer;

    public ZkInfo() {
    }

    public ZkInfo(String zkServer) {
        this.zkServer = zkServer;
    }

    public String getZkServer() {
        return zkServer;
    }

    public void setZkServer(String zkServer) {
        this.zkServer = zkServer;
    }

    public String[] getServers() {
        return null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
