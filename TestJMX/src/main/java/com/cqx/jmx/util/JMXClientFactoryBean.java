package com.cqx.jmx.util;

/**
 * JMXClientFactoryBean
 *
 * @author chenqixu
 */
public class JMXClientFactoryBean {
    private String objectname;
    private String ip;
    private int rmiPort;
    private boolean isNeedMBEAN;

    public static JMXClientFactoryBean builder() {
        return new JMXClientFactoryBean();
    }

    public String getObjectname() {
        return objectname;
    }

    public JMXClientFactoryBean setObjectname(String objectname) {
        this.objectname = objectname;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public JMXClientFactoryBean setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public JMXClientFactoryBean setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
        return this;
    }

    public boolean isNeedMBEAN() {
        return isNeedMBEAN;
    }

    public JMXClientFactoryBean setNeedMBEAN(boolean needMBEAN) {
        isNeedMBEAN = needMBEAN;
        return this;
    }
}
