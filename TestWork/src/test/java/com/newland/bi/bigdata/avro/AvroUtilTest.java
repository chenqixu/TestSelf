package com.newland.bi.bigdata.avro;

import com.newland.bi.bigdata.bean.avro.User;
import org.apache.avro.specific.SpecificRecordBase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class AvroUtilTest {

    private AvroUtil avroUtil;
    private byte[] bytes;

    @Before
    public void setUp() {
        avroUtil = new AvroUtil();
    }

    @Test
    public void testAll() {
        serializeAvroToByte();
        deserialzeAvroFromByte();
    }

    @Test
    public void serializeAvroToByte() {
        try {
//            bytes = avroUtil.serializeAvroToByte(User.newBuilder().setFavoriteColor("红色")
//                    .setFavoriteNumber(1).setName("张三").build());
            bytes = avroUtil.serializeAvroToByte(User.newBuilder().build());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void deserialzeAvroFromByte() {
        if (bytes != null) {
            try {
                SpecificRecordBase specificRecordBase = avroUtil.deserialzeAvroFromByte(bytes);
                User user = (User) specificRecordBase;
                System.out.println(user.getName());
                System.out.println(user.getFavoriteColor());
                System.out.println(user.getFavoriteNumber());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}