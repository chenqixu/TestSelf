package com.newland.bi.bigdata.utils.system;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ClassUtilTest {

    private static Logger logger = LoggerFactory.getLogger(ClassUtilTest.class);

    @Test
    public void getClassLoader() {
        logger.info("getClassLoader：{}", ClassUtil.getClassLoader());
    }

    @Test
    public void loadClass() {

    }

    @Test
    public void getClassSet() {
        Set<Class<?>> classSet = ClassUtil.getClassSet("com.newland.bi.mobilebox.impl");
        for (Class<?> cls : classSet) {
            logger.info("cls：{}", cls);
        }
    }
}