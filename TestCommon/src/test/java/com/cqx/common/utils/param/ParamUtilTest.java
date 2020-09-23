package com.cqx.common.utils.param;

import com.cqx.common.bean.javabean.ITask;
import com.cqx.common.bean.javabean.OtherTask;
import com.cqx.common.bean.javabean.Task;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Map;

public class ParamUtilTest {

    @Test
    public void beanToMap() throws Exception {
        Task task = new Task();
        task.setTask_id(123);
//        task.setTask_name("test");
//        task.setTask_status(0);
        System.out.println(ParamUtil.beanToMap(Task.class, task));
    }

    @Test
    public void newInstance() throws Exception {
        Class<? extends Task> cls = Task.class;
        //有参构造
        Constructor<? extends Task> constructor = cls.getDeclaredConstructor(int.class);
        Task t = constructor.newInstance(123);
        System.out.println(t.getTask_id());

        //无参构造
        constructor = cls.getDeclaredConstructor();
        t = constructor.newInstance();
        System.out.println(t.getTask_id());

        //接口
        ITask iTask = new OtherTask();
        System.out.println(((OtherTask) iTask).getOther());
    }

    @Test
    public void classPrint() throws Exception {
        Task task = new Task();
        Map<String, String> map = ParamUtil.beanToMap(Task.class, task);
        ParamUtil.setValueByMap(map, Task.class);
    }
}