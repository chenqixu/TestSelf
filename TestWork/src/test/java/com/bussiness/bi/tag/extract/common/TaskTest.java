package com.bussiness.bi.tag.extract.common;

import com.alibaba.fastjson.JSON;
import com.bussiness.bi.tag.extract.bean.TagEngineExtractTaskBean;
import com.bussiness.bi.tag.extract.bean.TaskParamBean;
import com.bussiness.bi.tag.extract.oracle.bean.CreateTableBean;
import com.bussiness.bi.tag.extract.oracle.schedule.CleanDataSchedule;
import com.bussiness.bi.tag.extract.oracle.schedule.CreateTableSchedule;
import com.bussiness.bi.tag.extract.oracle.schedule.DownLoadSchedule;
import com.bussiness.bi.tag.extract.oracle.schedule.PutHdfsSchedule;
import com.bussiness.bi.tag.extract.oracle.task.CreateTaskList;
import com.cqx.common.test.TestBase;
import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.IJDBCUtil;
import com.cqx.common.utils.jdbc.JDBCUtil;
import com.cqx.common.utils.jdbc.ParamsParserUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TaskTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TaskTest.class);
    private IJDBCUtil jdbcUtil;

    @Before
    public void setUp() throws Exception {
        Map params = getParam("jdbc.yaml");
        ParamsParserUtil paramsParserUtil = new ParamsParserUtil(params);
        DBBean dbBean = paramsParserUtil.getBeanMap().get("oracle12c_receng_Bean");
        jdbcUtil = new JDBCUtil(dbBean, 1, 1, 1);
        TaskUtil.getInstance().setJdbcUtil(jdbcUtil).setUuid("2022090214").setTask_type("oracle");
    }

    @After
    public void tearDown() throws Exception {
        if (jdbcUtil != null) jdbcUtil.close();
    }

    @Test
    public void task() throws Exception {
        TagEngineExtractTaskBean tagEngineExtractTaskBean = new TagEngineExtractTaskBean();
        tagEngineExtractTaskBean.setUuid("2022090214");
        tagEngineExtractTaskBean.setTask_type("oracle");
        // 派发任务
        CreateTaskList createTask = new CreateTaskList();
        createTask.init(tagEngineExtractTaskBean);
        boolean ret;
        if (!createTask.check()) {
            ret = createTask.run();
        } else {
            ret = true;
        }
        logger.info("createTask: {}", ret);
        // 调度工厂
        TaskScheduleFactory taskScheduleFactory = new TaskScheduleFactory();
        // 添加调度任务
        taskScheduleFactory.addTaskSchedule(new CreateTableSchedule());
        taskScheduleFactory.addTaskSchedule(new DownLoadSchedule());
        taskScheduleFactory.addTaskSchedule(new PutHdfsSchedule());
        taskScheduleFactory.addTaskSchedule(new CleanDataSchedule());
        // 启动所有的调度任务并等待执行完成
        taskScheduleFactory.startAndWait();
        // 资源释放
        TaskUtil.getInstance().getJdbcUtil().close();
    }

    @Test
    public void bean() {
        CreateTableBean createTaskBean = new CreateTableBean();
        createTaskBean.setCreate_sql("create table tmp_1 as select * from F_MKT_SMS_USER_LIST_1");
        String json = new TaskParamBean<>(createTaskBean).toJSONValues();
        logger.info("json: {}", json);
        TaskParamBean tp2 = JSON.parseObject(json, TaskParamBean.class);
        tp2.setT(JSON.parseObject(tp2.getValues(), CreateTableBean.class));
        logger.info("getClassName: {}， tp2.getValues: {}，t: {}", tp2.getClassName(), tp2.getValues(), tp2.getT());
    }
}