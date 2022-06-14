package com.bussiness.bi.bigdata.parser.stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ParserTimestampTest {
    private static final Logger logger = LoggerFactory.getLogger(ParserTimestampTest.class);
    private ParserTimestamp parserTimestamp = new ParserTimestamp("yyyy-MM-dd HH:mm:ss");
    private String[] values = {
            "1559530835614,1559530835936"};

    @Test
    public void parser() throws Exception {
        // 1
//        System.out.println(parserTimestamp.parser("1565758762168"));
        DateFormat dateoutFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateoutFormat.parse("2019-06-03 11:01:35").getTime());
        // 2
        for (String str : values) {
            String as[] = str.split(",", -1);
            String begin = parserTimestamp.parser(as[0]);
            String end = parserTimestamp.parser(as[1]);
            System.out.println("begin：" + begin + "，end：" + end);
        }
        // 3
//        try {
//            throw new NullPointerException("msisdn is null");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        System.out.println("end");
        // 4
//        Map<String, String> map = new HashMap<>();
//        map.put("1", "aa");
//        map.put("2", "bb");
//        logger.info("map：{}", map);
    }
}