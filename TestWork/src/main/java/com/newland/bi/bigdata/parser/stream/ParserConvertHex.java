package com.newland.bi.bigdata.parser.stream;

/**
 * ParserConvertHex
 * 16进制转换
 *
 * @author chenqixu
 */
public class ParserConvertHex implements IStreamParser {

    private String param;

    public ParserConvertHex(String param) {
        this.param = param;
    }

    @Override
    public String parser(String value) throws Exception {
        if (value != null && value.length() > 0) {
            if (param.equals("10")) return String.valueOf(Long.parseLong(value, 16));
            else throw new Exception("不支持的16进制类型转换，类型：" + param);
        } else {
            return null;
        }
    }
}
