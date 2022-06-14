package com.bussiness.bi.mobilebox.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 视频推荐清单
 *
 * @author chenqixu
 */
public class VideoinfoRecommend {
    private String deviceid;//设备id
    private String code;//事件code
    private String ip;//宽带IP
    private String createTime;//发生时间
    private String videoType;//节目类型
    private String videoSource;//节目来源
    private String recomendType;//推荐类型
    private String firstLevel;//一级类别
    private String secondLevel;//二级类别
    private String threeLevel;//三级类别
    private String preChannelName;//原电视频道
    private String preVideoName;//原电视节目名称
    private String curChannelName;//新电视频道
    private String curVideoName;//新电视节目名称
    private String videoQuality;//清晰度
    private String pannelLocation;//pannel位置
    private String postUrl;//海报图片地址
    private String watchTime;//观看时长
    private String videoStream;//节目码流
    private String logs;//上报原始数据日志
    private String dt;//
    private String ht;//

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(String videoSource) {
        this.videoSource = videoSource;
    }

    public String getRecomendType() {
        return recomendType;
    }

    public void setRecomendType(String recomendType) {
        this.recomendType = recomendType;
    }

    public String getFirstLevel() {
        return firstLevel;
    }

    public void setFirstLevel(String firstLevel) {
        this.firstLevel = firstLevel;
    }

    public String getSecondLevel() {
        return secondLevel;
    }

    public void setSecondLevel(String secondLevel) {
        this.secondLevel = secondLevel;
    }

    public String getThreeLevel() {
        return threeLevel;
    }

    public void setThreeLevel(String threeLevel) {
        this.threeLevel = threeLevel;
    }

    public String getPreChannelName() {
        return preChannelName;
    }

    public void setPreChannelName(String preChannelName) {
        this.preChannelName = preChannelName;
    }

    public String getPreVideoName() {
        return preVideoName;
    }

    public void setPreVideoName(String preVideoName) {
        this.preVideoName = preVideoName;
    }

    public String getCurChannelName() {
        return curChannelName;
    }

    public void setCurChannelName(String curChannelName) {
        this.curChannelName = curChannelName;
    }

    public String getCurVideoName() {
        return curVideoName;
    }

    public void setCurVideoName(String curVideoName) {
        this.curVideoName = curVideoName;
    }

    public String getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(String videoQuality) {
        this.videoQuality = videoQuality;
    }

    public String getPannelLocation() {
        return pannelLocation;
    }

    public void setPannelLocation(String pannelLocation) {
        this.pannelLocation = pannelLocation;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(String watchTime) {
        this.watchTime = watchTime;
    }

    public String getVideoStream() {
        return videoStream;
    }

    public void setVideoStream(String videoStream) {
        this.videoStream = videoStream;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getHt() {
        return ht;
    }

    public void setHt(String ht) {
        this.ht = ht;
    }
}
