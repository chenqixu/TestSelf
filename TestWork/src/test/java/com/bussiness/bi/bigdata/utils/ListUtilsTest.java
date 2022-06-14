package com.bussiness.bi.bigdata.utils;

import com.bussiness.bi.bigdata.bean.TaskBean;
import com.bussiness.bi.bigdata.utils.ListUtils;
import org.junit.Test;

public class ListUtilsTest {

    @Test
    public void addAndGet() {
        TaskBean taskBean = new TaskBean();
        taskBean.setNum(1);
        taskBean.setProgress(90);
        taskBean.setTask_name("test");
        System.out.println(ListUtils.addAndGet(taskBean));
    }
}