package com.bussiness.bi.bigdata.file.logfile;

/**
 * 任务状态
 *
 * @author chenqixu
 */
public enum TaskStatusEnum {
    NOT_STARTED("未开始"),
    TODO("待办"),
    IN_PROGRESS("进行中"),
    PENDING_ACCEPTANCE("待验收"),
    RETURNED_FOR_REVISION("返回修改"),
    COMPLETED("已完成"),
    BLOCKED("已阻塞"),
    CANCELLED("已取消"),
    HAS_SUBTASK("有子任务"),
    SUSPENDED("挂起"),
    STAGE_COMPLETED("阶段完成");

    private final String name;

    /**
     * 构造函数
     *
     * @param name
     */
    TaskStatusEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * 示例方法：判断任务是否处于可进行工作的活跃状态。
     */
    public boolean isActive() {
        return this == NOT_STARTED || this == TODO || this == IN_PROGRESS || this == RETURNED_FOR_REVISION;
    }

    /**
     * 示例方法：判断任务是否已结束（终态）。
     */
    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
