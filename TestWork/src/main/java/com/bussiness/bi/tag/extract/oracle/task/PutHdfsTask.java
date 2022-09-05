package com.bussiness.bi.tag.extract.oracle.task;

import com.bussiness.bi.tag.extract.bean.TagEngineExtractTaskBean;
import com.bussiness.bi.tag.extract.oracle.bean.PutHdfsBean;
import com.bussiness.bi.tag.extract.common.Task;

/**
 * TODO
 *
 * @author chenqixu
 */
public class PutHdfsTask extends Task<PutHdfsBean> {

    @Override
    public void init(TagEngineExtractTaskBean tagEngineExtractTaskBean) {

    }

    @Override
    protected Class<PutHdfsBean> getTaskCls() {
        return PutHdfsBean.class;
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
