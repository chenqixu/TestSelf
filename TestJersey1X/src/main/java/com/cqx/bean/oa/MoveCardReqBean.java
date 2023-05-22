package com.cqx.bean.oa;

/**
 * MoveCardReqBean
 *
 * @author chenqixu
 */
public class MoveCardReqBean {
    private String itemId;
    private int sort;
    private String userStoryId;
    private int typeId;
    private String columnId;
    private String statusColumnId;
    private int columnTypeSort;
    private boolean clearTask = false;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getUserStoryId() {
        return userStoryId;
    }

    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getStatusColumnId() {
        return statusColumnId;
    }

    public void setStatusColumnId(String statusColumnId) {
        this.statusColumnId = statusColumnId;
    }

    public int getColumnTypeSort() {
        return columnTypeSort;
    }

    public void setColumnTypeSort(int columnTypeSort) {
        this.columnTypeSort = columnTypeSort;
    }

    public boolean isClearTask() {
        return clearTask;
    }

    public void setClearTask(boolean clearTask) {
        this.clearTask = clearTask;
    }
}
