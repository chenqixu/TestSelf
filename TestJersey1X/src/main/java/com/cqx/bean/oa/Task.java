package com.cqx.bean.oa;

/**
 * Task
 *
 * @author chenqixu
 */
public class Task {
    private String taskId;
    private String itemId;
    private String userId;
    private String userName;
    private long createTime;
    private float duration;
    private long actualDateStart;
    private long actualDateEnd;
    private String description;
    private int isCrossTeamTask;
    private String userStoryId;
    private int status;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsCrossTeamTask() {
        return isCrossTeamTask;
    }

    public void setIsCrossTeamTask(int isCrossTeamTask) {
        this.isCrossTeamTask = isCrossTeamTask;
    }

    public String getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String statusDesc() {
        switch (status) {
            case 1:
                return "待办任务";
            case 2:
                return "进行中";
            case 3:
                return "已完成";
        }
        return "未知状态";
    }

    public long getActualDateStart() {
        return actualDateStart;
    }

    public void setActualDateStart(long actualDateStart) {
        this.actualDateStart = actualDateStart;
    }

    public long getActualDateEnd() {
        return actualDateEnd;
    }

    public void setActualDateEnd(long actualDateEnd) {
        this.actualDateEnd = actualDateEnd;
    }
}
