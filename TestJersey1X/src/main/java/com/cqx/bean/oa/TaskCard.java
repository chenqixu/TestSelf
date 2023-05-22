package com.cqx.bean.oa;

/**
 * TaskCard
 *
 * @author chenqixu
 */
public class TaskCard {
    private String itemId;
    private String name;
    private String description;
    private String createUserId;
    private float duration;
    private boolean isPrint;
    private BoardDataRespPosition position;
    private int messageCount;
    private int blockStatus;
    private Task[] taskList;
    private int isDelay;

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

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public boolean isPrint() {
        return isPrint;
    }

    public void setPrint(boolean print) {
        isPrint = print;
    }

    public BoardDataRespPosition getPosition() {
        return position;
    }

    public void setPosition(BoardDataRespPosition position) {
        this.position = position;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(int blockStatus) {
        this.blockStatus = blockStatus;
    }

    public Task[] getTaskList() {
        return taskList;
    }

    public void setTaskList(Task[] taskList) {
        this.taskList = taskList;
    }

    public int getIsDelay() {
        return isDelay;
    }

    public void setIsDelay(int isDelay) {
        this.isDelay = isDelay;
    }
}
