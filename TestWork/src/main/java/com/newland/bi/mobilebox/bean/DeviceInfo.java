package com.newland.bi.mobilebox.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 设备信息
 *
 * @author chenqixu
 */
public class DeviceInfo {
    private String deviceid;//设备id
    private String createDate;//注册时间
    private String onlineDate;//上线时间
    private String logoutDate;//下线时间
    private String userID;//用户ID
    private String userLocation;//用户位置
    private String apkVersion;//APK版本号
    private String terminalMode;//终端盒子型号
    private String terminalID;//终端盒子ID
    private String connectMode;//接入方式
    private String probeSoftVersion;//探针程序版本号
    private String licencesName;//牌照方
    private String supplierName;//机顶盒厂家
    private String loginCount;//登陆次数
    private String provinceId;//省ID
    private String cityId;//市ID
    private String lanMac;//有线mac地址
    private String wirelessMac;//无线mac地址
    private String dt;//天

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getOnlineDate() {
        return onlineDate;
    }

    public void setOnlineDate(String onlineDate) {
        this.onlineDate = onlineDate;
    }

    public String getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(String logoutDate) {
        this.logoutDate = logoutDate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(String apkVersion) {
        this.apkVersion = apkVersion;
    }

    public String getTerminalMode() {
        return terminalMode;
    }

    public void setTerminalMode(String terminalMode) {
        this.terminalMode = terminalMode;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public String getConnectMode() {
        return connectMode;
    }

    public void setConnectMode(String connectMode) {
        this.connectMode = connectMode;
    }

    public String getProbeSoftVersion() {
        return probeSoftVersion;
    }

    public void setProbeSoftVersion(String probeSoftVersion) {
        this.probeSoftVersion = probeSoftVersion;
    }

    public String getLicencesName() {
        return licencesName;
    }

    public void setLicencesName(String licencesName) {
        this.licencesName = licencesName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(String loginCount) {
        this.loginCount = loginCount;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getLanMac() {
        return lanMac;
    }

    public void setLanMac(String lanMac) {
        this.lanMac = lanMac;
    }

    public String getWirelessMac() {
        return wirelessMac;
    }

    public void setWirelessMac(String wirelessMac) {
        this.wirelessMac = wirelessMac;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
