package com.bussiness.bi.tag.extract.common;

import com.cqx.common.utils.jdbc.IJDBCUtil;

/**
 * TaskDBUtil
 *
 * @author chenqixu
 */
public class TaskUtil {
    private static TaskUtil taskUtil = new TaskUtil();
    private volatile IJDBCUtil jdbcUtil;
    private volatile String uuid;
    private volatile String task_type;

    private TaskUtil() {
    }

    public static TaskUtil getInstance() {
        return taskUtil;
    }

    public IJDBCUtil getJdbcUtil() {
        return jdbcUtil;
    }

    public TaskUtil setJdbcUtil(IJDBCUtil jdbcUtil) {
        this.jdbcUtil = jdbcUtil;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public TaskUtil setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getTask_type() {
        return task_type;
    }

    public TaskUtil setTask_type(String task_type) {
        this.task_type = task_type;
        return this;
    }
}
