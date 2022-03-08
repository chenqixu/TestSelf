package com.cqx.common.utils.pcap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PcapUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(PcapUtilTest.class);

    @Test
    public void callParser() throws Exception {
        String path = "d:\\Work\\ETL\\上网日志查询2022\\data\\";
        PcapUtil pcapUtil = new PcapUtil();
        // 有通话
        String result = pcapUtil.parser(path + "VoLTE_ici_8582644071769099_1588582644067395557_8618060318859_8613706030111_20220224_094416_012022_11_1588580346752508940.cap");
        logger.info("callParser：{}", result);
    }

    @Test
    public void hangUpParser() throws Exception {
        String path = "d:\\Work\\ETL\\上网日志查询2022\\data\\";
        PcapUtil pcapUtil = new PcapUtil();
        // 挂机
        String result = pcapUtil.parser(path + "VoLTE_ici_8582644071373960_1588582644067401578_8651180874284_8618965191816_20220224_094416_012022_11_1588580346752507017.cap");
        logger.info("hangUpParser：{}", result);
    }

    @Test
    public void my_trafficParser() throws Exception {
        String path = "d:\\Work\\ETL\\上网日志查询2022\\data\\";
        PcapUtil pcapUtil = new PcapUtil();
        // my_traffic
        String result = pcapUtil.parser(path + "my_traffic.pcap");
        logger.info("my_trafficParser：{}", result);
    }

    @Test
    public void stringTest() {
        String a = "10.1.8.203:8080;";
        List<Byte> byteList = new ArrayList<>();
        for (byte b : a.getBytes()) {
            byteList.add(b);
        }
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        System.out.println(new String(bytes));
    }
}