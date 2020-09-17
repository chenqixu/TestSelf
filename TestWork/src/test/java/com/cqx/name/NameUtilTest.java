package com.cqx.name;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(NameUtilTest.class);
    private NameUtil nameUtil;

    @Before
    public void setUp() throws Exception {
        nameUtil = new NameUtil();
    }

    @Test
    public void getRandomChar() {
        for (int i = 0; i < 10; i++)
            logger.info("{}", nameUtil.getUTF8RandomChar());
    }

    @Test
    public void getRandomJianHan() {
        for (int i = 0; i < 10; i++)
            logger.info("{}", nameUtil.getRandomJianHan(1));
    }

    @Test
    public void randomName() {
        for (int i = 0; i < 10; i++)
            logger.info("{}", nameUtil.randomName(true, 1));
    }
}