package com.bussiness.bi.bigdata.bean;

/**
 * TaskBean
 *
 * @author chenqixu
 */
public class TaskBean {
    int num;
    String task_name;
    int progress;

    public String toString() {
        return num + ". " + task_name + " --任务进度：" + progress + "%";
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
