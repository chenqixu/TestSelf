package com.cqx.bean;

/**
 * 命令工具bean
 *
 * @author chenqixu
 */
public class CmdBean {
    private String type;
    private String username;
    private String password;
    private String dns;
    private String loglevel = "info";

    public static CmdBean newbuilder() {
        return new CmdBean();
    }

    public String toString() {
        return "[type=" + type + ",[username=" + username + ",[password=" + password + ",[dns=" + dns + ",[loglevel=" + loglevel;
    }

    public String getType() {
        return type;
    }

    public CmdBean setType(String type) {
        this.type = type;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public CmdBean setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public CmdBean setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDns() {
        return dns;
    }

    public CmdBean setDns(String dns) {
        this.dns = dns;
        return this;
    }

    public String getLoglevel() {
        return loglevel;
    }

    public CmdBean setLoglevel(String loglevel) {
        this.loglevel = loglevel;
        return this;
    }
}
