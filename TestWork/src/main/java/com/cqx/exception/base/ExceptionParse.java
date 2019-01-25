package com.cqx.exception.base;

/**
 * 错误日志参数解析
 *
 * @author chenqixu
 */
public class ExceptionParse {

    public final static String SPLIT_STR = "\\{\\}";
    public final static String BLANK_SPACE = " ";

    public static String parse(String message, Object... objs) {
        message = message + BLANK_SPACE;
        String[] msgarr = message.split(SPLIT_STR);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < msgarr.length; i++) {
            sb.append(msgarr[i]);
            if (i < (msgarr.length - 1))
                sb.append(objs == null ? "null" : objs[i]);
        }
        return sb.toString();
    }
}
