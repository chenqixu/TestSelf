package com.bussiness.bi.bigdata.utils.system;

import com.cqx.common.utils.jdbc.JDBCUtil;
import com.cqx.common.utils.system.ClassUtil;
import com.bussiness.bi.mobilebox.parse.AbstractBodyParse;
import com.bussiness.bi.mobilebox.utils.BodyImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ClassUtilTest {

    private static Logger logger = LoggerFactory.getLogger(ClassUtilTest.class);
    private ClassUtil<BodyImpl, AbstractBodyParse> classUtil = new ClassUtil();

    @Test
    public void getClassLoader() {
        logger.info("getClassLoader：{}", classUtil.getClassLoader());
    }

    @Test
    public void getResource() {
        logger.info("{}", classUtil.getResource("logback.xml").toString());
    }

    @Test
    public void getClassSet() {
        Set<Class<?>> classSet = classUtil.getClassSet("com.newland.bi.mobilebox.impl",
                BodyImpl.class);
        for (Class<?> cls : classSet) {
            logger.info("cls：{}", cls);
        }
    }

    @Test
    public void getClassfileBuffer() {
        classUtil.getClassfileBuffer(JDBCUtil.class.getName(), JDBCUtil.class.getSimpleName());
    }
}