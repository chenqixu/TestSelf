package com.bussiness.bi.mobilebox.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户行为
 *
 * @author chenqixu
 */
public class UserBehavior {
    private String rowkey;//设备id+时间
    private String ip;//ip
    private String createTime;//发生时间
    private String code;//事件code
    private String videoType;//节目类型
    private String videoSource;//节目来源
    private String firstLevel;//一级类别
    private String secondLevel;//二级类别
    private String threeLevel;//三级类别
    private String pannelLocation;//pannel位置
    private String postUrl;//海报图片地址
    private String openPostTime;//海报打开时长
    private String channelSwitchTime;//频道切换时长
    private String channelName;//电视频道
    private String videoQuality;//视频质量
    private String videoName;//节目名称
    private String directName;//节目导演
    private String actorName;//节目主演
    private String videoPlot;//节目情节
    private String videoScore;//节目评分
    private String videoRegion;//节目地区
    private String videoSize;//节目大小
    private String videoTimeLength;//节目时长
    private String videoStream;//节目码流
    private String contentSource;//内容来源
    private String videoStartTime;//视频播放开始时间
    private String videoEndTime;//视频播放结束时间
    private String videoErrorTime;//视频播放失败时间
    private String watchTime;//观看时长
    private String firstFrameTime;//首帧等待时长
    private String kadunTime;//视频播放卡顿时长
    private String appName;//App应用名称
    private String searchValue;//搜索热词
    private String bootTimeDelay;//开机时延
    private String openAppTime;//app打开时长
    private String loadPageTime;//页面加载时长
    private String logs;//上报原始数据日志
    private String ht;//小时
    private String dt;//天

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getOpenPostTime() {
        return openPostTime;
    }

    public void setOpenPostTime(String openPostTime) {
        this.openPostTime = openPostTime;
    }

    public String getChannelSwitchTime() {
        return channelSwitchTime;
    }

    public void setChannelSwitchTime(String channelSwitchTime) {
        this.channelSwitchTime = channelSwitchTime;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(String videoQuality) {
        this.videoQuality = videoQuality;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getDirectName() {
        return directName;
    }

    public void setDirectName(String directName) {
        this.directName = directName;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getVideoPlot() {
        return videoPlot;
    }

    public void setVideoPlot(String videoPlot) {
        this.videoPlot = videoPlot;
    }

    public String getVideoScore() {
        return videoScore;
    }

    public void setVideoScore(String videoScore) {
        this.videoScore = videoScore;
    }

    public String getVideoRegion() {
        return videoRegion;
    }

    public void setVideoRegion(String videoRegion) {
        this.videoRegion = videoRegion;
    }

    public String getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(String videoSize) {
        this.videoSize = videoSize;
    }

    public String getVideoTimeLength() {
        return videoTimeLength;
    }

    public void setVideoTimeLength(String videoTimeLength) {
        this.videoTimeLength = videoTimeLength;
    }

    public String getVideoStream() {
        return videoStream;
    }

    public void setVideoStream(String videoStream) {
        this.videoStream = videoStream;
    }

    public String getContentSource() {
        return contentSource;
    }

    public void setContentSource(String contentSource) {
        this.contentSource = contentSource;
    }

    public String getVideoStartTime() {
        return videoStartTime;
    }

    public void setVideoStartTime(String videoStartTime) {
        this.videoStartTime = videoStartTime;
    }

    public String getVideoEndTime() {
        return videoEndTime;
    }

    public void setVideoEndTime(String videoEndTime) {
        this.videoEndTime = videoEndTime;
    }

    public String getVideoErrorTime() {
        return videoErrorTime;
    }

    public void setVideoErrorTime(String videoErrorTime) {
        this.videoErrorTime = videoErrorTime;
    }

    public String getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(String watchTime) {
        this.watchTime = watchTime;
    }

    public String getFirstFrameTime() {
        return firstFrameTime;
    }

    public void setFirstFrameTime(String firstFrameTime) {
        this.firstFrameTime = firstFrameTime;
    }

    public String getKadunTime() {
        return kadunTime;
    }

    public void setKadunTime(String kadunTime) {
        this.kadunTime = kadunTime;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getBootTimeDelay() {
        return bootTimeDelay;
    }

    public void setBootTimeDelay(String bootTimeDelay) {
        this.bootTimeDelay = bootTimeDelay;
    }

    public String getOpenAppTime() {
        return openAppTime;
    }

    public void setOpenAppTime(String openAppTime) {
        this.openAppTime = openAppTime;
    }

    public String getLoadPageTime() {
        return loadPageTime;
    }

    public void setLoadPageTime(String loadPageTime) {
        this.loadPageTime = loadPageTime;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getHt() {
        return ht;
    }

    public void setHt(String ht) {
        this.ht = ht;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
