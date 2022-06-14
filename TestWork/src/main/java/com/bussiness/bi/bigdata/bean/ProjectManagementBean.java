package com.bussiness.bi.bigdata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectManagementBean
 *
 * @author chenqixu
 */
public class ProjectManagementBean {
    String model_name;
    List<StoryBean> storyBeanList = new ArrayList<>();

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public List<StoryBean> getStoryBeanList() {
        return storyBeanList;
    }

    public void setStoryBeanList(List<StoryBean> storyBeanList) {
        this.storyBeanList = storyBeanList;
    }

    public void addStory(StoryBean storyBean) {
        this.storyBeanList.add(storyBean);
    }
}
