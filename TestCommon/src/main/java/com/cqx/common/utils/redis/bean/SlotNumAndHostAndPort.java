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

    public SlotNumAndHostAndPort(List<Integer> slotNums, HostAndPort hostAndPort) {
        this.slotNums = slotNums;
        this.hostAndPort = hostAndPort;
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
}
