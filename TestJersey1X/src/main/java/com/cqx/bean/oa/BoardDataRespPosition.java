package com.cqx.bean.oa;

/**
 * BoardDataRespPosition
 *
 * @author chenqixu
 */
public class BoardDataRespPosition {
    private String seqId;
    private String itemId;
    private String userStoryId;
    private String columnId;
    private int statusColumnId;
    private long createTime;
    private String createUser;
    private String taskId;
    private int objStatus;
    private int sort;

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public int getStatusColumnId() {
        return statusColumnId;
    }

    public void setStatusColumnId(int statusColumnId) {
        this.statusColumnId = statusColumnId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
    }

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

    public int getObjStatus() {
        return objStatus;
    }

    public void setObjStatus(int objStatus) {
        this.objStatus = objStatus;
    }
}
