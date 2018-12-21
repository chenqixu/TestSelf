package com.newland.bi.mobilebox.exception;

/**
 * 运营助手异常
 *
 * @author chenqixu
 */
public class MobileBoxException extends Exception {

    public final static String SPLIT_STR = "\\{\\}";
    public final static String BLANK_SPACE = " ";

    public MobileBoxException(final String message) {
        super(message);
    }

    public MobileBoxException(String message, Object... objs) {
        super(MobileBoxParse.parse(message, objs));
    }

    static class MobileBoxParse {
        public static String parse(String message, Object... objs) {
            message = message + BLANK_SPACE;
            String[] msgarr = message.split(SPLIT_STR);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < msgarr.length; i++) {
                sb.append(msgarr[i]);
                if (i < (msgarr.length - 1))
                    sb.append(objs[i]);
            }
            return sb.toString();
        }
    }
}
