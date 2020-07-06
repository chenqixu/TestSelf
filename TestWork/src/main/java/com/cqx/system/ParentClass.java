package com.cqx.system;

import com.cqx.common.utils.system.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ParentClass
 *
 * @author chenqixu
 */
public class ParentClass {

    private static final Logger logger = LoggerFactory.getLogger(ParentClass.class);

    public void setParams() {
        ClassUtil classUtil = new ClassUtil();
        logger.info("ParentClass.setParams {}", classUtil.getClassLoader());
    }

//    protected abstract void run();
}
