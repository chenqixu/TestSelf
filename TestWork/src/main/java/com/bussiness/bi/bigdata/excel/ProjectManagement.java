package com.bussiness.bi.bigdata.excel;

import com.cqx.common.utils.excel.ExcelSheetList;
import com.cqx.common.utils.excel.ExcelUtils;
import com.bussiness.bi.bigdata.bean.ProjectManagementBean;
import com.bussiness.bi.bigdata.bean.StoryBean;
import com.bussiness.bi.bigdata.bean.TaskBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 项目管理进度反馈
 *
 * @author chenqixu
 */
public class ProjectManagement {

    private static final Logger logger = LoggerFactory.getLogger(ProjectManagement.class);
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private ExcelUtils eu = new ExcelUtils();

    public void run(String path) {
        List<ExcelSheetList> list;
        try {
            list = eu.readExcel(path);
            ProjectManagementBean projectManagementBean = new ProjectManagementBean();
            StoryBean storyBean = new StoryBean();
            for (ExcelSheetList excelSheetList : list) {
                String old_story = null;
                for (List<String> stringList : excelSheetList.getSheetList()) {
                    if (stringList.size() <= 16) continue;
                    // 第一次初始化
                    if (old_story == null) {
                        old_story = stringList.get(3);
                        storyBean.setStory_name(old_story);
                        projectManagementBean.addStory(storyBean);
                    }
                    String new_story = stringList.get(3);
                    // 故事切换
                    if (!new_story.equals(old_story)) {
                        // 切换新建故事
                        storyBean = new StoryBean();
                        storyBean.setStory_name(new_story);
                        projectManagementBean.addStory(storyBean);
                        old_story = new_story;
                        // 新任务
                        TaskBean taskBean = new TaskBean();
                        taskBean.setTask_name(stringList.get(4));
                        taskBean.setProgress((int) (Float.valueOf(stringList.get(16)) * 100));
                        storyBean.addTask(taskBean);
                    } else {
                        // 新任务
                        TaskBean taskBean = new TaskBean();
                        taskBean.setTask_name(stringList.get(4));
                        taskBean.setProgress((int) (Float.valueOf(stringList.get(16)) * 100));
                        storyBean.addTask(taskBean);
                    }
                }
            }
            // 打印
            System.out.println("##本周开发##");
            for (StoryBean storyBean1 : projectManagementBean.getStoryBeanList()) {
                System.out.println("  " + storyBean1);
                for (TaskBean taskBean : storyBean1.getTaskBeanList()) {
                    System.out.println("  " + taskBean);
                }
                System.out.println();
            }
            System.out.println("##下周计划##");
            for (StoryBean storyBean1 : projectManagementBean.getStoryBeanList()) {
                if (storyBean1.getProgress() < 100) {
                    System.out.println("  " + storyBean1.getStory_name());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
