package com.bussiness.bi.tag.extract.common;

import com.bussiness.bi.tag.extract.bean.TagEngineExtractTaskBean;
import com.cqx.common.utils.jdbc.IJDBCUtil;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.system.TimeCostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TaskSchedule
 *
 * @author chenqixu
 */
public abstract class TaskSchedule<T extends Task> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TaskSchedule.class);
    private IJDBCUtil jdbcUtil;
    private AtomicInteger poolSize = new AtomicInteger(0);
    private AtomicInteger completeSize = new AtomicInteger(0);
    private int maxSize = 5;
    private Class<T> beanCls;

    public TaskSchedule() {
        this.jdbcUtil = TaskUtil.getInstance().getJdbcUtil();
    }

    protected abstract Class<T> getTaskCls();

    protected abstract String getTaskTag();

    // 查询任务
    protected List<TagEngineExtractTaskBean> queryTasks() {
        IJDBCUtil jdbcUtil = getJdbcUtil();
        String sql = "select * from tag_engine_extract_task where uuid='"
                + TaskUtil.getInstance().getUuid() + "' and task_type='"
                + TaskUtil.getInstance().getTask_type() + "' and task_tag='" + getTaskTag() + "'";
        try {
            return jdbcUtil.executeQuery(sql, TagEngineExtractTaskBean.class);
        } catch (Exception e) {
            // todo
            logger.warn("查询任务失败，结果为空。");
            return null;
        }
    }

    protected IJDBCUtil getJdbcUtil() {
        return jdbcUtil;
    }

    // 查询这个任务是否有依赖，如果有，依赖是否满足
    private boolean queryDepend(TagEngineExtractTaskBean tagEngineExtractTaskBean) {
        String queryDependSql = "select t1.* from tag_engine_extract_task t1 " +
                "inner join tag_engine_extract_task_depend t2 on t2.uuid='"
                + tagEngineExtractTaskBean.getUuid() + "' and t2.task_type='"
                + tagEngineExtractTaskBean.getTask_type() + "' and t2.task_id='"
                + tagEngineExtractTaskBean.getTask_id() + "' and t1.task_id=t2.task_depend_id " +
                "where t1.uuid='" + tagEngineExtractTaskBean.getUuid() + "' and t1.task_type='"
                + tagEngineExtractTaskBean.getTask_type() + "'";
        try {
            List<TagEngineExtractTaskBean> rets = jdbcUtil.executeQuery(queryDependSql, TagEngineExtractTaskBean.class);
            if (rets.size() > 0) {// 有依赖
                boolean tmp = true;
                for (TagEngineExtractTaskBean teetb : rets) {
                    tmp = tmp && (teetb.getStatus() != 0);
                }
                return tmp;
            } else {// 无依赖
                return true;
            }
        } catch (Exception e) {
            // todo
            return false;
        }
    }

    private void updateTaskStatus(TagEngineExtractTaskBean tagEngineExtractTaskBean) {
        String updateSql = "update tag_engine_extract_task set status=1 where uuid='"
                + tagEngineExtractTaskBean.getUuid() + "' and task_type='"
                + tagEngineExtractTaskBean.getTask_type() + "' and task_id='"
                + tagEngineExtractTaskBean.getTask_id() + "'";
        try {
            int ret = jdbcUtil.executeUpdate(updateSql);
            if (ret > 0) {
                logger.info("任务[{}-{}] 更新状态成功，结果：{}", tagEngineExtractTaskBean.getTask_tag(), tagEngineExtractTaskBean.getTask_id(), ret);
            } else {
                logger.warn("任务[{}-{}] 更新状态失败，结果: {}", tagEngineExtractTaskBean.getTask_tag(), tagEngineExtractTaskBean.getTask_id(), ret);
            }
        } catch (SQLException e) {
            // todo
            logger.warn("任务[{}-{}] 更新状态失败，原因：{}", tagEngineExtractTaskBean.getTask_tag(), tagEngineExtractTaskBean.getTask_id(), e.getMessage());
        }
    }

    @Override
    public void run() {
        exec();
    }

    // 并发执行任务，直到所有任务都执行完成
    protected void exec() {
        this.beanCls = getTaskCls();
        List<TagEngineExtractTaskBean> taskList = queryTasks();
        if (taskList == null) {
            return;
        }
        LinkedBlockingQueue<TagEngineExtractTaskBean> queue = new LinkedBlockingQueue<>();
        // 设置已完成的任务
        for (TagEngineExtractTaskBean tagEngineExtractTaskBean : taskList) {
            if (tagEngineExtractTaskBean.getStatus() > 0) {// 完成
                completeSize.incrementAndGet();
            } else {// 未完成，加到待队列中
                try {
                    queue.put(tagEngineExtractTaskBean);
                } catch (InterruptedException e) {
                    // todo
                }
            }
        }
        // 任务总数
        int allSize = taskList.size();
        logger.info("[{}] 任务总数：{}，已完成任务数：{}，待完成任务数：{}", getTaskTag(), allSize, completeSize.get(), queue.size());
        // 如果任务没有完成
        while (completeSize.get() != allSize) {
            // 如果任务池有空闲
            if (poolSize.get() < maxSize) {
                // 获取一个待执行任务
                TagEngineExtractTaskBean tagEngineExtractTaskBean = queue.poll();
                if (tagEngineExtractTaskBean != null) {
                    // 依赖满足
                    if (queryDepend(tagEngineExtractTaskBean)) {
                        logger.info("任务[{}-{}] 依赖满足，准备启动", tagEngineExtractTaskBean.getTask_tag(), tagEngineExtractTaskBean.getTask_id());
                        TaskRun taskRun = new TaskRun(tagEngineExtractTaskBean);
                        taskRun.start();
                    } else {
                        // 依赖不满足，再塞回去
                        try {
                            queue.put(tagEngineExtractTaskBean);
                        } catch (InterruptedException e) {
                            // todo
                        }
                    }
                }
            }
            SleepUtil.sleepMilliSecond(500);
        }
    }

    class TaskRun extends Thread {
        TagEngineExtractTaskBean runBean;

        TaskRun(TagEngineExtractTaskBean runBean) {
            this.runBean = runBean;
        }

        public void run() {
            // 占用任务池
            poolSize.incrementAndGet();
            TimeCostUtil tc = new TimeCostUtil();
            tc.start();
            try {
                T t = beanCls.newInstance();
                t.init(runBean);
                boolean ret;
                if (!t.check()) {
                    ret = t.run();
                } else {
                    ret = true;
                }
                if (ret) {
                    updateTaskStatus(runBean);
                } else {
                    logger.info("任务[{}-{}] 执行失败。", runBean.getTask_tag(), runBean.getTask_id());
                }
            } catch (Exception e) {
                // todo
            }
            logger.info("任务[{}-{}] 执行完成，执行耗时：{} ms", runBean.getTask_tag(), runBean.getTask_id(), tc.stopAndGet());
            // 释放任务池
            poolSize.decrementAndGet();
            // 任务完成计数
            completeSize.incrementAndGet();
        }
    }
}
