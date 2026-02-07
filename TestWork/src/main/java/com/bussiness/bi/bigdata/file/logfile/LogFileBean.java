package com.bussiness.bi.bigdata.file.logfile;

/**
 * <pre>
 *     [数据示例]
 *     ### <周三> [需求-20260204行销，阵地集团约客] 进行中 吴建伟
 *     [数据说明]
 *     ### <星期几> [分类-任务名称] 任务状态 负责人
 * </pre>
 */
public class LogFileBean implements Comparable<LogFileBean> {
    private int weekNum;// 任务时间转数字，用于排序
    private String dealWeek;// 任务时间
    private String taskType;// 任务分类
    private String taskName;// 任务名称（实际上是 任务分类-任务真实名称）
    private String realTaskName;// 去掉任务分类之后的任务名称，任务真实名称
    private String taskStatus;// 任务状态
    private String taskPIC;// Person In Charge 负责人
    private String content;// 输入的完整内容，未解析
    private String startTime = "";// 可能的开始时间
    private String endTime = "";// 可能的完成时间

    LogFileBean() {
    }

    LogFileBean(String taskType, String realTaskName, String taskStatus, String taskPIC, String startTime, String endTime) {
        this.taskType = taskType;
        this.realTaskName = realTaskName;
        this.taskStatus = taskStatus;
        if (taskPIC == null || taskPIC.equals("null")) taskPIC = "我";
        this.taskPIC = taskPIC;
        this.taskName = this.taskType + "-" + this.realTaskName;
        if (startTime != null && !startTime.equals("null")) this.startTime = startTime;
        if (endTime != null && !endTime.equals("null")) this.endTime = endTime;
    }

    LogFileBean(String content) {
        this.content = content;
        String[] arr = content.split(" ", -1);
        this.dealWeek = arr[1].replace("<", "").replace(">", "");
        this.taskName = arr[2].replace("[", "").replace("]", "");
        String[] taskNameArr = this.taskName.split("-", -1);
        if (taskNameArr.length == 2) {
            this.taskType = taskNameArr[0];
            this.realTaskName = taskNameArr[1];
        } else {
            throw new NullPointerException(String.format("[%s]缺失任务分类！", this.taskName));
        }
        this.taskStatus = arr[3];
        if (arr.length == 5) {
            this.taskPIC = arr[4];
        } else if (arr.length < 5) {
            this.taskPIC = "我";
        } else {
            throw new NullPointerException(String.format("[%s]任务格式不正确！要求格式[### <星期几> [分类-任务名称] 任务状态 负责人]", this.content));
        }
        String _tmp = this.dealWeek;
        _tmp = _tmp.replace("周一", "1");
        _tmp = _tmp.replace("周二", "2");
        _tmp = _tmp.replace("周三", "3");
        _tmp = _tmp.replace("周四", "4");
        _tmp = _tmp.replace("周五", "5");
        _tmp = _tmp.replace("周六", "6");
        _tmp = _tmp.replace("周日", "7");
        this.weekNum = Integer.valueOf(_tmp);
    }

    @Override
    public String toString() {
        return String.format("任务分类=%s，任务名称=%s，任务状态=%s，负责人=%s"
                , this.taskType, this.realTaskName, this.taskStatus, this.taskPIC);
    }

    @Override
    public int compareTo(LogFileBean o) {
        return Integer.compare(this.getWeekNum(), o.getWeekNum());
    }

    public String getDealWeek() {
        return dealWeek;
    }

    public void setDealWeek(String dealWeek) {
        this.dealWeek = dealWeek;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public String getTaskPIC() {
        return taskPIC;
    }

    public void setTaskPIC(String taskPIC) {
        this.taskPIC = taskPIC;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getRealTaskName() {
        return realTaskName;
    }

    public void setRealTaskName(String realTaskName) {
        this.realTaskName = realTaskName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
