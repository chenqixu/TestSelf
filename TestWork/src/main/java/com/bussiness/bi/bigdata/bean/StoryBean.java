package com.bussiness.bi.bigdata.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * StoryBean
 *
 * @author chenqixu
 */
public class StoryBean {
    String story_name;
    List<TaskBean> taskBeanList = new ArrayList<>();
    int task_num = 1;
    int sumProgress;
    int totalProgress;

    public String toString() {
        return story_name + " --故事进度：" + getProgress() + "%";
    }

    public String getStory_name() {
        return story_name;
    }

    public void setStory_name(String story_name) {
        // 去掉故事编号
        int index = story_name.indexOf("(#");
        if (index > 0) story_name = story_name.substring(0, index);
        this.story_name = story_name;
    }

    public List<TaskBean> getTaskBeanList() {
        return taskBeanList;
    }

    public void setTaskBeanList(List<TaskBean> taskBeanList) {
        this.taskBeanList = taskBeanList;
    }

    public void addTask(TaskBean taskBean) {
        taskBean.setNum(task_num);
        this.taskBeanList.add(taskBean);
        task_num++;
        sumProgress += taskBean.getProgress();
        totalProgress += 100;
    }

    public int getProgress() {
        if (sumProgress > 0 && totalProgress > 0) {
            return sumProgress * 100 / totalProgress;
        } else {
            return 0;
        }
    }
}
