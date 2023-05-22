package com.cqx.bean.oa;

/**
 * AgilePlanByTeamIdRespData
 *
 * @author chenqixu
 */
public class AgilePlanByTeamIdRespData implements Comparable<AgilePlanByTeamIdRespData> {
    private String agilePlanId;
    private String parentId;
    private String name;
    private String description;
    private long planDateStart;
    private long planDateEnd;
    private int status;
    private int objStatus;
    private long createTime;
    private String createUser;
    private long updateTime;
    private String updateUser;
    private String teamId;
    private String appId;
    private int pageSize;
    private int pageNum;
    private int storyCount;
    private int completedCount;
    private long buildCompletionRateTime;
    private long completionRateTime;
    private String buildCompletionRateStoryStatus;
    private String allScale;
    private String changeRate;
    private int cancelCount;
    private String waitAcceptColumnAlias;
    private String storyColumnAlias;
    private boolean canAssociatedStory;
    private int completionCompletedCount;

    public String getAgilePlanId() {
        return agilePlanId;
    }

    public void setAgilePlanId(String agilePlanId) {
        this.agilePlanId = agilePlanId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPlanDateStart() {
        return planDateStart;
    }

    public void setPlanDateStart(long planDateStart) {
        this.planDateStart = planDateStart;
    }

    public long getPlanDateEnd() {
        return planDateEnd;
    }

    public void setPlanDateEnd(long planDateEnd) {
        this.planDateEnd = planDateEnd;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getObjStatus() {
        return objStatus;
    }

    public void setObjStatus(int objStatus) {
        this.objStatus = objStatus;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getStoryCount() {
        return storyCount;
    }

    public void setStoryCount(int storyCount) {
        this.storyCount = storyCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public long getBuildCompletionRateTime() {
        return buildCompletionRateTime;
    }

    public void setBuildCompletionRateTime(long buildCompletionRateTime) {
        this.buildCompletionRateTime = buildCompletionRateTime;
    }

    public long getCompletionRateTime() {
        return completionRateTime;
    }

    public void setCompletionRateTime(long completionRateTime) {
        this.completionRateTime = completionRateTime;
    }

    public String getBuildCompletionRateStoryStatus() {
        return buildCompletionRateStoryStatus;
    }

    public void setBuildCompletionRateStoryStatus(String buildCompletionRateStoryStatus) {
        this.buildCompletionRateStoryStatus = buildCompletionRateStoryStatus;
    }

    public String getAllScale() {
        return allScale;
    }

    public void setAllScale(String allScale) {
        this.allScale = allScale;
    }

    public String getChangeRate() {
        return changeRate;
    }

    public void setChangeRate(String changeRate) {
        this.changeRate = changeRate;
    }

    public int getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(int cancelCount) {
        this.cancelCount = cancelCount;
    }

    public String getWaitAcceptColumnAlias() {
        return waitAcceptColumnAlias;
    }

    public void setWaitAcceptColumnAlias(String waitAcceptColumnAlias) {
        this.waitAcceptColumnAlias = waitAcceptColumnAlias;
    }

    public String getStoryColumnAlias() {
        return storyColumnAlias;
    }

    public void setStoryColumnAlias(String storyColumnAlias) {
        this.storyColumnAlias = storyColumnAlias;
    }

    public boolean isCanAssociatedStory() {
        return canAssociatedStory;
    }

    public void setCanAssociatedStory(boolean canAssociatedStory) {
        this.canAssociatedStory = canAssociatedStory;
    }

    public int getCompletionCompletedCount() {
        return completionCompletedCount;
    }

    public void setCompletionCompletedCount(int completionCompletedCount) {
        this.completionCompletedCount = completionCompletedCount;
    }

    @Override
    public int compareTo(AgilePlanByTeamIdRespData o) {
        return Long.compare(o.getPlanDateStart(), getPlanDateStart());
    }
}
