package com.bussiness.bi.tag.extract.common;

import com.alibaba.fastjson.JSON;
import com.bussiness.bi.tag.extract.bean.TagEngineExtractTaskBean;
import com.bussiness.bi.tag.extract.bean.TaskParamBean;

/**
 * Task
 *
 * @author chenqixu
 */
public abstract class Task<T> {

    public abstract void init(TagEngineExtractTaskBean tagEngineExtractTaskBean);

    protected abstract Class<T> getTaskCls();

    // 校验
    public abstract boolean check() throws Exception;

    // 执行
    public abstract boolean run() throws Exception;

    public T parserParam(String task_param) {
        if (task_param != null && task_param.length() > 0) {
            TaskParamBean<T> tp = JSON.parseObject(task_param, TaskParamBean.class);
            Class<T> cls = getTaskCls();
            if (cls != null) {
                tp.setT(JSON.parseObject(tp.getValues(), cls));
                return tp.getT();
            }
        }
        return null;
    }
}
