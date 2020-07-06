package com.cqx.system;

import com.cqx.common.utils.system.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ChildClass
 *
 * @author chenqixu
 */
public class ChildClass extends ParentClass {

    private static final Logger logger = LoggerFactory.getLogger(ChildClass.class);

    public void run() {
        ClassUtil classUtil = new ClassUtil();
        logger.info("ChildClass.run {}", classUtil.getClassLoader());
    }
}
