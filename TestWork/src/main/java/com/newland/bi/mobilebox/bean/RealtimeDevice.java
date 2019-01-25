package com.newland.bi.mobilebox.bean;

import com.alibaba.fastjson.JSON;

/**
 * 实时指标
 *
 * @author chenqixu
 */
public class RealtimeDevice {
    private String supplierName;
    private String terminalMode;
    private String terminalId;
    private String deviceId;
    private String userId;
    private int loginCount;
    private String licencesName;
    private String onlineDate;
    private String apkVersion;
    private String connectMode;
    private String videoName;
    private String probeSoftVersion;
    private String createDate;
    private String city;
    private String logoutDate;
    private String userLocation;
    private String ip;
    private String wired;
    private String wireless;

    public static RealtimeDevice jsonToBean(String string) {
        return JSON.parseObject(string, RealtimeDevice.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getTerminalMode() {
        return terminalMode;
    }

    public void setTerminalMode(String terminalMode) {
        this.terminalMode = terminalMode;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public String getLicencesName() {
        return licencesName;
    }

    public void setLicencesName(String licencesName) {
        this.licencesName = licencesName;
    }

    public String getOnlineDate() {
        return onlineDate;
    }

    public void setOnlineDate(String onlineDate) {
        this.onlineDate = onlineDate;
    }

    public String getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(String apkVersion) {
        this.apkVersion = apkVersion;
    }

    public String getConnectMode() {
        return connectMode;
    }

    public void setConnectMode(String connectMode) {
        this.connectMode = connectMode;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getProbeSoftVersion() {
        return probeSoftVersion;
    }

    public void setProbeSoftVersion(String probeSoftVersion) {
        this.probeSoftVersion = probeSoftVersion;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(String logoutDate) {
        this.logoutDate = logoutDate;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getWired() {
        return wired;
    }

    public void setWired(String wired) {
        this.wired = wired;
    }

    public String getWireless() {
        return wireless;
    }

    public void setWireless(String wireless) {
        this.wireless = wireless;
    }
}
