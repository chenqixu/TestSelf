package com.cqx.bean.oa;

/**
 * BoardDataRespData
 *
 * @author chenqixu
 */
public class BoardDataRespData {
    private String itemId;
    private String userStoryId;
    private String userStoryCode;
    private String userStorySeq;
    private String description;
    private String agilePlanId;
    private String priority;
    private String priorityName;
    private String type;
    private String status;
    private String scale;// 估算规模(点数)
    private boolean isPrint;
    private String responsiblePerson;
    private String responsiblePersonName;
    private BoardDataRespPosition position;
    private TaskCard[] taskCardList;
    private boolean hasIterationHistory;
    private int taskCardPositionPriority;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    public String getUserStoryCode() {
        return userStoryCode;
    }

    public void setUserStoryCode(String userStoryCode) {
        this.userStoryCode = userStoryCode;
    }

    public String getUserStorySeq() {
        return userStorySeq;
    }

    public void setUserStorySeq(String userStorySeq) {
        this.userStorySeq = userStorySeq;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAgilePlanId() {
        return agilePlanId;
    }

    public void setAgilePlanId(String agilePlanId) {
        this.agilePlanId = agilePlanId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public boolean isPrint() {
        return isPrint;
    }

    public void setPrint(boolean print) {
        isPrint = print;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public String getResponsiblePersonName() {
        return responsiblePersonName;
    }

    public void setResponsiblePersonName(String responsiblePersonName) {
        this.responsiblePersonName = responsiblePersonName;
    }

    public BoardDataRespPosition getPosition() {
        return position;
    }

    public void setPosition(BoardDataRespPosition position) {
        this.position = position;
    }

    public TaskCard[] getTaskCardList() {
        return taskCardList;
    }

    public void setTaskCardList(TaskCard[] taskCardList) {
        this.taskCardList = taskCardList;
    }

    public boolean isHasIterationHistory() {
        return hasIterationHistory;
    }

    public void setHasIterationHistory(boolean hasIterationHistory) {
        this.hasIterationHistory = hasIterationHistory;
    }

    public int getTaskCardPositionPriority() {
        return taskCardPositionPriority;
    }

    public void setTaskCardPositionPriority(int taskCardPositionPriority) {
        this.taskCardPositionPriority = taskCardPositionPriority;
    }
}
