package com.cqx.bean.oa;

/**
 * QueryListRespData
 *
 * @author chenqixu
 */
public class QueryListRespData {
    private String columnId;
    private String columnName;
    private String planId;
    private int sort;
    private int frozen;
    private int columnStatusNum;
    private int objStatus;
    private long createTime;
    private String createUser;
    private int typeId;
    private int columnType;
    private int wip;
    private int visible;
    private String appId;
    private String teamId;
    private QueryListChildren[] children;

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getFrozen() {
        return frozen;
    }

    public void setFrozen(int frozen) {
        this.frozen = frozen;
    }

    public int getColumnStatusNum() {
        return columnStatusNum;
    }

    public void setColumnStatusNum(int columnStatusNum) {
        this.columnStatusNum = columnStatusNum;
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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getColumnType() {
        return columnType;
    }

    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }

    public int getWip() {
        return wip;
    }

    public void setWip(int wip) {
        this.wip = wip;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public QueryListChildren[] getChildren() {
        return children;
    }

    public void setChildren(QueryListChildren[] children) {
        this.children = children;
    }
}
