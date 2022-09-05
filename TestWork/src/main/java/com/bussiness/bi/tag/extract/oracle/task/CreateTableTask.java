package com.bussiness.bi.tag.extract.oracle.task;

import com.bussiness.bi.tag.extract.bean.TagEngineExtractTaskBean;
import com.bussiness.bi.tag.extract.oracle.bean.CreateTableBean;
import com.bussiness.bi.tag.extract.common.Task;
import com.bussiness.bi.tag.extract.common.TaskUtil;
import com.cqx.common.utils.jdbc.IJDBCUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CreateTableTask
 *
 * @author chenqixu
 */
public class CreateTableTask extends Task<CreateTableBean> {
    private static final Logger logger = LoggerFactory.getLogger(CreateTableTask.class);
    private IJDBCUtil jdbcUtil;
    private TagEngineExtractTaskBean tagEngineExtractTaskBean;

    @Override
    public void init(TagEngineExtractTaskBean tagEngineExtractTaskBean) {
        this.jdbcUtil = TaskUtil.getInstance().getJdbcUtil();
        this.tagEngineExtractTaskBean = tagEngineExtractTaskBean;
    }

    @Override
    protected Class<CreateTableBean> getTaskCls() {
        return CreateTableBean.class;
    }

    @Override
    public boolean check() throws Exception {
        // 表已建，且file_num有值
        return false;
    }

    @Override
    public boolean run() throws Exception {
        CreateTableBean createTaskBean = parserParam(tagEngineExtractTaskBean.getTask_param());
        logger.info("createTaskBean：{}", createTaskBean);
        return true;
    }
}
