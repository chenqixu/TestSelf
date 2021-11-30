package com.cqx.common.utils.serialize.impl;

import com.cqx.common.bean.javabean.Task;
import com.cqx.common.utils.serialize.ISerialization;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GsonSerializationImplTest {
    private static final Logger logger = LoggerFactory.getLogger(GsonSerializationImplTest.class);
    private ISerialization<Task> iSerialization;

    @Before
    public void setUp() throws Exception {
        iSerialization = new GsonSerializationImpl<>();
        iSerialization.setTClass(Task.class);
    }

    @Test
    public void serialize() throws Exception {
        Task task = new Task(123);
        byte[] bytes = iSerialization.serialize(task);
        Task deserializeTask = iSerialization.deserialize(bytes);
        logger.info("{}", deserializeTask.getTask_id());
    }
}