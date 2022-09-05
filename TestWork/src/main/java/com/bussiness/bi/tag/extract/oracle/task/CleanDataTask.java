package com.bussiness.bi.tag.extract.oracle.task;

import com.bussiness.bi.tag.extract.bean.TagEngineExtractTaskBean;
import com.bussiness.bi.tag.extract.oracle.bean.CleanDataBean;
import com.bussiness.bi.tag.extract.common.Task;

/**
 * CleanDataTask，清理临时表，清理本地导出的临时文件
 *
 * @author chenqixu
 */
public class CleanDataTask extends Task<CleanDataBean> {

    @Override
    public void init(TagEngineExtractTaskBean tagEngineExtractTaskBean) {

    }

    @Override
    protected Class<CleanDataBean> getTaskCls() {
        return CleanDataBean.class;
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
