package com.cqx.services.work;

import org.junit.Test;

public class WorkFactoryTest {

    @Test
    public void getWork() {
        System.out.println(WorkFactory.getWork("work:bricklayer://1234"));
    }
}