package com.bussiness.bi.tag.extract.oracle.task;

import com.bussiness.bi.tag.extract.bean.TagEngineExtractTaskBean;
import com.bussiness.bi.tag.extract.bean.TagEngineExtractTaskDependBean;
import com.bussiness.bi.tag.extract.bean.TaskParamBean;
import com.bussiness.bi.tag.extract.common.Task;
import com.bussiness.bi.tag.extract.common.TaskConstanct;
import com.bussiness.bi.tag.extract.common.TaskUtil;
import com.bussiness.bi.tag.extract.oracle.bean.CleanDataBean;
import com.bussiness.bi.tag.extract.oracle.bean.CreateTableBean;
import com.bussiness.bi.tag.extract.oracle.bean.DownLoadBean;
import com.bussiness.bi.tag.extract.oracle.bean.PutHdfsBean;
import com.cqx.common.utils.jdbc.IJDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CreateTask
 *
 * @author chenqixu
 */
public class CreateTaskList extends Task {
    private static final Logger logger = LoggerFactory.getLogger(CreateTaskList.class);
    private IJDBCUtil jdbcUtil;
    private String uuid;
    private String task_type;

    @Override
    public void init(TagEngineExtractTaskBean tagEngineExtractTaskBean) {
        this.jdbcUtil = TaskUtil.getInstance().getJdbcUtil();
        this.uuid = tagEngineExtractTaskBean.getUuid();
        this.task_type = tagEngineExtractTaskBean.getTask_type();
    }

    @Override
    protected Class getTaskCls() {
        return null;
    }

    @Override
    public boolean check() throws Exception {
        // 是否有值
        // 任务表是4的倍数，依赖表记录个数=任务表记录个数/4*3
        String check_sql1 = "select * from tag_engine_extract_task where uuid='" + uuid + "' and task_type='" + task_type + "'";
        List<TagEngineExtractTaskBean> tasks = jdbcUtil.executeQuery(check_sql1, TagEngineExtractTaskBean.class);

        String check_sql2 = "select * from tag_engine_extract_task_depend where uuid='" + uuid + "' and task_type='" + task_type + "'";
        List<TagEngineExtractTaskDependBean> depends = jdbcUtil.executeQuery(check_sql2, TagEngineExtractTaskDependBean.class);
        return tasks.size() > 0 && depends.size() > 0
                && tasks.size() % 4 == 0
                && depends.size() % 3 == 0
                && (tasks.size() / 4) * 3 == depends.size();
    }

    @Override
    public boolean run() throws Exception {
        // 模拟生成
        String task_fields = "uuid,task_type,task_tag,task_id,task_param";
        String task_sql = "insert into tag_engine_extract_task(" + task_fields + ") values(?,?,?,?,?)";
        List<TagEngineExtractTaskBean> tasks = new ArrayList<>();
        // level1 - create tmp table
        TagEngineExtractTaskBean level1 = new TagEngineExtractTaskBean();
        level1.setUuid(uuid);
        level1.setTask_type(task_type);
        level1.setTask_tag(TaskConstanct.TAG_CREATE_TABLE);
        level1.setTask_id(UUID.randomUUID().toString().replace("-", ""));
        CreateTableBean createTaskBean = new CreateTableBean();
        createTaskBean.setCreate_sql("create table tmp_1 as select * from F_MKT_SMS_USER_LIST_1");
        level1.setTask_param(new TaskParamBean<>(createTaskBean).toJSONValues());
        tasks.add(level1);

        // level2 - down load tmp table data to local file
        TagEngineExtractTaskBean level2 = new TagEngineExtractTaskBean();
        level2.setUuid(uuid);
        level2.setTask_type(task_type);
        level2.setTask_tag(TaskConstanct.TAG_DOWN_LOAD);
        level2.setTask_id(UUID.randomUUID().toString().replace("-", ""));
        DownLoadBean downLoadBean = new DownLoadBean();
        level2.setTask_param(new TaskParamBean<>(downLoadBean).toJSONValues());
        tasks.add(level2);

        // level3 - put local file to hdfs
        TagEngineExtractTaskBean level3 = new TagEngineExtractTaskBean();
        level3.setUuid(uuid);
        level3.setTask_type(task_type);
        level3.setTask_tag(TaskConstanct.TAG_PUT_HDFS);
        level3.setTask_id(UUID.randomUUID().toString().replace("-", ""));
        PutHdfsBean putHdfsBean = new PutHdfsBean();
        level3.setTask_param(new TaskParamBean<>(putHdfsBean).toJSONValues());
        tasks.add(level3);

        // level4 - clean local file, clean tmp table
        TagEngineExtractTaskBean level4 = new TagEngineExtractTaskBean();
        level4.setUuid(uuid);
        level4.setTask_type(task_type);
        level4.setTask_tag(TaskConstanct.TAG_CLEAN_DATA);
        level4.setTask_id(UUID.randomUUID().toString().replace("-", ""));
        CleanDataBean cleanBean = new CleanDataBean();
        level4.setTask_param(new TaskParamBean<>(cleanBean).toJSONValues());
        tasks.add(level4);

        int task_rets = jdbcUtil.executeBatch(task_sql, tasks, TagEngineExtractTaskBean.class, task_fields);
        logger.info("task_rets: {}", task_rets);

        // depend
        String depend_fields = "uuid,task_type,task_id,task_depend_id";
        String depend_sql = "insert into tag_engine_extract_task_depend(" + depend_fields + ") values(?,?,?,?)";
        List<TagEngineExtractTaskDependBean> depends = new ArrayList<>();
        TagEngineExtractTaskDependBean level2d = new TagEngineExtractTaskDependBean();
        level2d.setUuid(uuid);
        level2d.setTask_type(task_type);
        level2d.setTask_id(level2.getTask_id());
        level2d.setTask_depend_id(level1.getTask_id());
        depends.add(level2d);

        TagEngineExtractTaskDependBean level3d = new TagEngineExtractTaskDependBean();
        level3d.setUuid(uuid);
        level3d.setTask_type(task_type);
        level3d.setTask_id(level3.getTask_id());
        level3d.setTask_depend_id(level2.getTask_id());
        depends.add(level3d);

        TagEngineExtractTaskDependBean level4d = new TagEngineExtractTaskDependBean();
        level4d.setUuid(uuid);
        level4d.setTask_type(task_type);
        level4d.setTask_id(level4.getTask_id());
        level4d.setTask_depend_id(level3.getTask_id());
        depends.add(level4d);

        int depend_rets = jdbcUtil.executeBatch(depend_sql, depends, TagEngineExtractTaskDependBean.class, depend_fields);
        logger.info("depend_rets: {}", depend_rets);
        return task_rets > 0 && depend_rets > 0;
    }
}
