package com.bussiness.bi.bigdata.avro;

import com.bussiness.bi.bigdata.avro.AvroUtil;
import com.cqx.common.utils.jdbc.BeanUtil;
import com.bussiness.bi.bigdata.bean.avro.TB_SER_OGG_BROADBAND_RESERV;
import com.bussiness.bi.bigdata.bean.avro.columns;
import org.apache.avro.specific.SpecificRecordBase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AvroUtilTest {

    private AvroUtil avroUtil;
    private byte[] bytes;

    @Before
    public void setUp() {
        avroUtil = new AvroUtil();
    }

    @Test
    public void testAll() throws Exception {
        serializeAvroToByte();
        deserialzeAvroFromByte();
    }

    @Test
    public void serializeAvroToByte() throws Exception {
        try {
//            bytes = avroUtil.serializeAvroToByte(User.newBuilder()
//                    .setFavoriteColor("红色")
//                    .setFavoriteNumber(1)
//                    .setName("张三")
//                    .build());

            BeanUtil beanUtil = new BeanUtil();
            columns after = beanUtil.batchSetMethodsValue(columns.class, "set.*IsMissing", false);
            after.setADDRESS("test address after");
            columns before = beanUtil.batchSetMethodsValue(columns.class, "set.*IsMissing", false);
            before.setADDRESS("test address before");
            bytes = avroUtil.serializeAvroToByte(TB_SER_OGG_BROADBAND_RESERV.newBuilder()
                    .setTable("TB_SER_OGG_BROADBAND_RESERV")
                    .setOpType("I")
                    .setOpTs("")
                    .setPos("")
                    .setPrimaryKeys(new ArrayList<CharSequence>())
                    .setTokens(new HashMap<CharSequence, CharSequence>())
                    .setCurrentTs("")
                    .setAfter(after)
                    .setBefore(before)
                    .build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deserialzeAvroFromByte() {
        if (bytes != null) {
            try {
                SpecificRecordBase specificRecordBase = avroUtil.deserialzeAvroFromByte(bytes);

//                User user = (User) specificRecordBase;
//                System.out.println(user.getName());
//                System.out.println(user.getFavoriteColor());
//                System.out.println(user.getFavoriteNumber());

                TB_SER_OGG_BROADBAND_RESERV tb_ser_ogg_broadband_reserv = (TB_SER_OGG_BROADBAND_RESERV) specificRecordBase;
                System.out.println(tb_ser_ogg_broadband_reserv.getAfter().getADDRESS());
                System.out.println(tb_ser_ogg_broadband_reserv.getTable());
                System.out.println(tb_ser_ogg_broadband_reserv.getOpType());
                System.out.println(tb_ser_ogg_broadband_reserv.getBefore().getADDRESS());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}