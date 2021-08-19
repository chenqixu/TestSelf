package com.newland.bi.bigdata.file;

import com.cqx.common.utils.serialize.ISerialization;
import com.cqx.common.utils.serialize.impl.KryoSerializationImpl;
import com.newland.bi.bigdata.bean.javabean.ListObject;
import com.newland.bi.bigdata.bean.javabean.S1mmeBean;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompressionRatioTest {

    @Test
    public void AVRO() throws Exception {
        String path = "d:\\tmp\\data\\dpi\\dpi_s1mme\\";
        CompressionRatio compressionRatio = new CompressionRatio();
        compressionRatio.setReadFileName(path + "LTE_S1MME_026679650002_20190529171000.txt");
        compressionRatio.setWriteFileName(path + "AVRO.txt");
//        compressionRatio.setRAF(true);
        compressionRatio.write(CompressionRatio.Compression.AVRO);
    }

    @Test
    public void KRYO() throws Exception {
        String path = "d:\\tmp\\data\\dpi\\dpi_s1mme\\";
        CompressionRatio compressionRatio = new CompressionRatio();
        compressionRatio.setReadFileName(path + "LTE_S1MME_026679650002_20190529171000.txt");
        compressionRatio.setWriteFileName(path + "KRYO.txt");
//        compressionRatio.setRAF(true);
        compressionRatio.write(CompressionRatio.Compression.KRYO);
    }

    @Test
    public void PROTOSTUFF() throws Exception {
        String path = "d:\\tmp\\data\\dpi\\dpi_s1mme\\";
        CompressionRatio compressionRatio = new CompressionRatio();
        compressionRatio.setReadFileName(path + "LTE_S1MME_026679650002_20190529171000.txt");
        compressionRatio.setWriteFileName(path + "PROTOSTUFF.txt");
//        compressionRatio.setRAF(true);
        compressionRatio.write(CompressionRatio.Compression.PROTOSTUFF);
    }

    @Test
    public void readKRYO() throws IOException {
        String path = "d:\\tmp\\data\\dpi\\dpi_s1mme\\";
        CompressionRatio compressionRatio = new CompressionRatio();
        compressionRatio.setWriteFileName(path + "KRYO.txt");
//        compressionRatio.setRAF(true);
        compressionRatio.read(CompressionRatio.Compression.KRYO);
    }

    @Test
    public void writeAndread() throws IOException {
        ISerialization<ListObject> iSerialization = new KryoSerializationImpl<>();
        iSerialization.setTClass(ListObject.class);

        String value = "275|0595|5|1408805b0d7a2e00|6|460009980939860|869189041598393|13599993859|20||1559120968729|11|1559120968740|0|||||0|6988|213962012|453|130|D44D1496|460|0||||||100.98.46.129|100.77.106.60|36412|36412|595C|7745A11||||||IMS|0|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||2019|05|29|17|09|20|||||||||||||||||||||||||||||||||||||||||59FB|";
        CompressionRatio compressionRatio = new CompressionRatio();

        List<S1mmeBean> s1mmeBeanList = new ArrayList<>();
        for (int i = 0; i < 4000; i++)
            s1mmeBeanList.add(compressionRatio.contentToBean(value));

//        S1mmeBean s1mmeBean = new S1mmeBean();
//        s1mmeBean.setApn("4g");
//        s1mmeBean.setXdr_id("12345");
//        s1mmeBeanList.add(s1mmeBean);
//        S1mmeBean s1mmeBean2 = new S1mmeBean();
//        s1mmeBean2.setApn("3g");
//        s1mmeBean2.setXdr_id("123");
//        s1mmeBeanList.add(s1mmeBean2);

//        ListObject listObject = new ListObject(s1mmeBeanList);
//        ListObject listObject = new ListObject(new ArrayList<>(s1mmeBeanList));
//        listObject.setValue("test");
        byte[] bytes = iSerialization.serialize(new ListObject(new ArrayList<>(s1mmeBeanList)));
        System.out.println(bytes.length);
        ListObject ds = iSerialization.deserialize(bytes);
        System.out.println(ds.getList().size());
    }
}