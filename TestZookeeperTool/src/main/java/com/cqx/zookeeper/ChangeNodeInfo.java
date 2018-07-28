package com.cqx.zookeeper;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

public class ChangeNodeInfo {
    private String zkPath = "";
    private int collectType;
    private int actionType;
    private String nodeType = "";
    private String taskId = "";
    private String stageId = "";
    private String serviceId = "";
    private String componentId = "";
    private Map<String, String> mapData;

    public ChangeNodeInfo() {
    }

    public ChangeNodeInfo(String zkPath, int collectType, int actionType) {
        this.zkPath = zkPath;
        this.collectType = collectType;
        this.actionType = actionType;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getZkPath() {
        return zkPath;
    }

    public void setZkPath(String zkPath) {
        this.zkPath = zkPath;
    }

    public int getCollectType() {
        return collectType;
    }

    public void setCollectType(int collectType) {
        this.collectType = collectType;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public Map<String, String> getMapData() {
        return mapData;
    }

    public void setMapData(Map<String, String> mapData) {
        this.mapData = mapData;
    }

    @Override
    public boolean equals(Object object) {
        ChangeNodeInfo changeNodeInfo = (ChangeNodeInfo) object;
        return changeNodeInfo.getZkPath().equals(this.getZkPath()) && (changeNodeInfo.getActionType() == this.actionType);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
