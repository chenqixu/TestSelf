package com.cqx.bean.oa;

/**
 * QueryListRespDataNext
 *
 * @author chenqixu
 */
public class QueryListRespDataNext {
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
    private String statusColumnId;
    private String statusColumnName;

    public QueryListRespDataNext() {
    }

    public QueryListRespDataNext(QueryListRespData queryListRespData) {
        this.columnId = queryListRespData.getColumnId();
        this.columnName = queryListRespData.getColumnName();
        this.planId = queryListRespData.getPlanId();
        this.sort = queryListRespData.getSort();
        this.frozen = queryListRespData.getFrozen();
        this.columnStatusNum = queryListRespData.getColumnStatusNum();
        this.objStatus = queryListRespData.getObjStatus();
        this.createTime = queryListRespData.getCreateTime();
        this.createUser = queryListRespData.getCreateUser();
        this.typeId = queryListRespData.getTypeId();
        this.columnType = queryListRespData.getColumnType();
        this.wip = queryListRespData.getWip();
        this.visible = queryListRespData.getVisible();
        this.appId = queryListRespData.getAppId();
        this.teamId = queryListRespData.getTeamId();
    }

    public QueryListRespDataNext(QueryListRespData queryListRespData, QueryListChildren children) {
        this(queryListRespData);
        this.statusColumnId = children.getStatusColumnId();
        this.statusColumnName = children.getStatusColumnName();
    }

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

    public String getStatusColumnId() {
        return statusColumnId;
    }

    public void setStatusColumnId(String statusColumnId) {
        this.statusColumnId = statusColumnId;
    }

    public String getStatusColumnName() {
        return statusColumnName;
    }

    public void setStatusColumnName(String statusColumnName) {
        this.statusColumnName = statusColumnName;
    }
}
