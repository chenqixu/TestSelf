package com.newland.bi.bigdata.bean.avro;

import org.junit.Test;

public class UserTest {

    @Test
    public void getName() {
        User user = User.newBuilder(new User()).build();
        System.out.println(user.getId() == null);
    }
}