package com.newland.bi.bigdata.utils;

import com.newland.bi.bigdata.bean.TaskBean;
import org.junit.Test;

import static org.junit.Assert.*;

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