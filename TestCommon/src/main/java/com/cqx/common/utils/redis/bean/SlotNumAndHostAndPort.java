package com.cqx.common.utils.redis.bean;

import redis.clients.jedis.HostAndPort;

import java.util.List;

/**
 * SlotNumAndHostAndPort
 *
 * @author chenqixu
 */
public class SlotNumAndHostAndPort {
    private HostAndPort hostAndPort;
    private List<Integer> slotNums;
    private String nodeKey;

    public SlotNumAndHostAndPort(List<Integer> slotNums, HostAndPort hostAndPort) {
        this.slotNums = slotNums;
        this.hostAndPort = hostAndPort;
        this.nodeKey = hostAndPort.getHost() + ":" + hostAndPort.getPort();
    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

    public void setHostAndPort(HostAndPort hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    public List<Integer> getSlotNums() {
        return slotNums;
    }

    public void setSlotNums(List<Integer> slotNums) {
        this.slotNums = slotNums;
    }

    public String getNodeKey() {
        return nodeKey;
    }
}
