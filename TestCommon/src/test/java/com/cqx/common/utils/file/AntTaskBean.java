package com.cqx.common.utils.file;

/**
 * AntTaskBean
 *
 * @author chenqixu
 */
public class AntTaskBean {
    private String sqls;
    private String userGroupInfo;
    private String taskDate;
    private String runType;
//    private String confProp;
    private String clusterName;

    public String getSqls() {
        return sqls;
    }

    public void setSqls(String sqls) {
        this.sqls = sqls;
    }

    public String getUserGroupInfo() {
        return userGroupInfo;
    }

    public void setUserGroupInfo(String userGroupInfo) {
        this.userGroupInfo = userGroupInfo;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public String getRunType() {
        return runType;
    }

    public void setRunType(String runType) {
        this.runType = runType;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
