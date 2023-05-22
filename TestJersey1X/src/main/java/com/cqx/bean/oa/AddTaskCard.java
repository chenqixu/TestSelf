package com.cqx.bean.oa;

/**
 * AddTaskCard
 *
 * @author chenqixu
 */
public class AddTaskCard {
    private String itemId = "";
    private String name;
    private String description;
    private String userId = "e544f554a4b6467d8887f52e2ff69506";
    private String userName = "";
    private int duration = 1;
    private String userStoryId;
    private String agilePlanId;
    private String[] deleteTaskIdList;
    private int isCrossTeamTask = 0;
    private String taskId = "";

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    public String getAgilePlanId() {
        return agilePlanId;
    }

    public void setAgilePlanId(String agilePlanId) {
        this.agilePlanId = agilePlanId;
    }

    public String[] getDeleteTaskIdList() {
        return deleteTaskIdList;
    }

    public void setDeleteTaskIdList(String[] deleteTaskIdList) {
        this.deleteTaskIdList = deleteTaskIdList;
    }

    public int getIsCrossTeamTask() {
        return isCrossTeamTask;
    }

    public void setIsCrossTeamTask(int isCrossTeamTask) {
        this.isCrossTeamTask = isCrossTeamTask;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
