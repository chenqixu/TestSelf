package com.cqx.bean.oa;

/**
 * QueryBoardData
 *
 * @author chenqixu
 */
public class QueryBoardData {
    private String teamId;
    private String agilePlanId;
    private String memberSearch;// 用户id
    private String[] statusList;
    private String storySearch;
    private String orderType;
    private String productId;
    private String requirementSectionId;
    private String[] productIdList;
    private boolean onlySelfTask;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getAgilePlanId() {
        return agilePlanId;
    }

    public void setAgilePlanId(String agilePlanId) {
        this.agilePlanId = agilePlanId;
    }

    public String getMemberSearch() {
        return memberSearch;
    }

    public void setMemberSearch(String memberSearch) {
        this.memberSearch = memberSearch;
    }

    public String[] getStatusList() {
        return statusList;
    }

    public void setStatusList(String[] statusList) {
        this.statusList = statusList;
    }

    public String getStorySearch() {
        return storySearch;
    }

    public void setStorySearch(String storySearch) {
        this.storySearch = storySearch;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getRequirementSectionId() {
        return requirementSectionId;
    }

    public void setRequirementSectionId(String requirementSectionId) {
        this.requirementSectionId = requirementSectionId;
    }

    public String[] getProductIdList() {
        return productIdList;
    }

    public void setProductIdList(String[] productIdList) {
        this.productIdList = productIdList;
    }

    public boolean isOnlySelfTask() {
        return onlySelfTask;
    }

    public void setOnlySelfTask(boolean onlySelfTask) {
        this.onlySelfTask = onlySelfTask;
    }
}
