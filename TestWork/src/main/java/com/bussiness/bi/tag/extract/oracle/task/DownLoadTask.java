package com.bussiness.bi.tag.extract.oracle.task;

import com.bussiness.bi.tag.extract.bean.TagEngineExtractTaskBean;
import com.bussiness.bi.tag.extract.oracle.bean.DownLoadBean;
import com.bussiness.bi.tag.extract.common.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DownLoadTask
 *
 * @author chenqixu
 */
public class DownLoadTask extends Task<DownLoadBean> {
    private static final Logger logger = LoggerFactory.getLogger(DownLoadTask.class);
    private TagEngineExtractTaskBean tagEngineExtractTaskBean;

    @Override
    protected Class<DownLoadBean> getTaskCls() {
        return DownLoadBean.class;
    }

    @Override
    public void init(TagEngineExtractTaskBean tagEngineExtractTaskBean) {
        this.tagEngineExtractTaskBean = tagEngineExtractTaskBean;
    }

    @Override
    public boolean check() throws Exception {
        return false;
    }

    @Override
    public boolean run() throws Exception {
        return true;
    }
}
