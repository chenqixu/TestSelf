package com.newland.bi.bigdata.parser.stream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ParserTimestamp
 * 时间转换成yyyyMMddHHmmss
 *
 * @author chenqixu
 */
public class ParserTimestamp implements IStreamParser {

    private String param;
    // 输出格式
    private DateFormat dateoutFormat;

    public ParserTimestamp(String param) {
        this.param = param;
        this.dateoutFormat = new SimpleDateFormat(param);
    }

    @Override
    public String parser(String value) throws Exception {
        if (value != null && value.length() > 0) {
            // 时间戳转换成输出
            return dateoutFormat.format(new Date(Long.valueOf(value)));
        } else {
            return null;
        }
    }
}
