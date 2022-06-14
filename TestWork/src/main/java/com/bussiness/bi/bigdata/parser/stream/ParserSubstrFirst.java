package com.bussiness.bi.bigdata.parser.stream;

/**
 * ParserSubstrFirst
 * 首个字符串截取
 *
 * @author chenqixu
 */
public class ParserSubstrFirst implements IStreamParser {

    private String param;

    public ParserSubstrFirst(String param) {
        this.param = param;
    }

    @Override
    public String parser(String value) throws Exception {
        if (value != null && value.length() > 0) {
            String _value = value.trim();
            // 如果是以{param}开头，则截取掉
            if (_value.startsWith(param)) return _value.substring(param.length());
            else return _value;
        } else {
            return null;
        }
    }
}
