package com.bussiness.bi.bigdata.bean.avro;

import com.bussiness.bi.bigdata.bean.avro.User;
import org.junit.Test;

public class UserTest {

    @Test
    public void getName() {
        User user = User.newBuilder(new User()).build();
        System.out.println(user.getId() == null);
    }
}