package com.cqx.localcache.mod;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ModDBWorkTest {
    private ModDBWork modDBWork;

    @Before
    public void setUp() throws Exception {
        modDBWork = new ModDBWork(10);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void add() {
        modDBWork.add(592430269340L);
        modDBWork.add(592430269341L);
        modDBWork.add(592430269350L);
    }
}