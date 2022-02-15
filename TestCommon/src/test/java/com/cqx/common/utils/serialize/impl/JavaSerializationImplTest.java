package com.cqx.common.utils.serialize.impl;

import com.cqx.common.bean.javabean.ClassBean;
import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import com.cqx.common.utils.jdbc.JDBCUtil;
import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.system.SleepUtil;
import com.cqx.common.utils.thread.ThreadTool;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JavaSerializationImplTest {
    private static final Logger logger = LoggerFactory.getLogger(JavaSerializationImplTest.class);
    private static ClassBean classBean = new ClassBean();
    private ISerialization<ClassBean> iSerialization;

    @Before
    public void setUp() throws Exception {
        iSerialization = new JavaSerializationImpl<>();
        iSerialization.setTClass(ClassBean.class);
        classBean.setClass_no(1);
        classBean.setClass_name("c1");
    }

    @Test
    public void serializeConcurrent() throws IOException {
        final int count = 5;
        byte[] bytes = iSerialization.serialize(classBean);
        // 线程1：设置并打印班级信息
        Runnable runnable1 = new Runnable() {
            //            ClassBean classBean1 = classBean;
            ClassBean classBean1 = iSerialization.deserialize(bytes);

            @Override
            public void run() {
                classBean1.getHeadmaster().setTeacher_no(9001);
                classBean1.getHeadmaster().setTeacher_name("张三");
                classBean1.getHeadmaster().setTeacher_sex(1);
                classBean1.getHeadmaster().setTeacher_major("语文");
                int i = 0;
                while (i < count) {
                    i++;
                    logger.info("{}", classBean1);
                    SleepUtil.sleepMilliSecond(500L);
                }
            }
        };
        // 线程2：设置并打印班级信息
        Runnable runnable2 = new Runnable() {
            //            ClassBean classBean2 = classBean;
            ClassBean classBean2 = iSerialization.deserialize(bytes);

            @Override
            public void run() {
                classBean2.getHeadmaster().setTeacher_no(9002);
                classBean2.getHeadmaster().setTeacher_name("李四");
                classBean2.getHeadmaster().setTeacher_sex(2);
                classBean2.getHeadmaster().setTeacher_major("数学");
                int i = 0;
                while (i < count) {
                    i++;
                    logger.info("{}", classBean2);
                    SleepUtil.sleepMilliSecond(1500L);
                }
            }
        };
        ThreadTool threadTool = new ThreadTool();
        threadTool.addTask(runnable1);
        threadTool.addTask(runnable2);
        threadTool.startTask();
    }

    @Test
    public void serialize() throws IOException {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setPool(false);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.204:1521:orapri");
        dbBean.setUser_name("jutap");
        dbBean.setPass_word("jutap");
        JDBCUtil jdbcUtil = new JDBCUtil(dbBean);
        classBean = new ClassBean(jdbcUtil);
        byte[] bytes = iSerialization.serialize(classBean);
        ClassBean classBeanNew = iSerialization.deserialize(bytes);
        logger.info("\nold.hash：{}\nold.value：{}\nnew.hash：{}\nnew.value：{}"
                , classBean.hashCode(), classBean
                , classBeanNew.hashCode(), classBeanNew
        );
    }
}