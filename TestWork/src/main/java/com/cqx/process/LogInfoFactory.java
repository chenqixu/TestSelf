package com.cqx.process;

import java.text.SimpleDateFormat;
import java.util.Arrays;

public class LogInfoFactory implements Logger {
    //	private static LogInfoFactory log = new LogInfoFactory();
    private static String LEVEL = PropertyUtil.getProperty("logger", "INFO");
    private int LEVELCODE = 0;
    public final static String SPLIT_STR = "\\{\\}";
    public final static String BLANK_SPACE = " ";
    private Class<?> cs = null;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public enum EnumLevel {
        ERR("ERR"),
        INFO("INFO"),
        WARN("WARN"),
        DEBUG("DEBUG");

        private final String code;

        private EnumLevel(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }

    private LogInfoFactory() {
        setLevel();
    }

    private LogInfoFactory(Class<?> cs) {
        setLevel();
        this.cs = cs;
    }

//	public static synchronized LogInfoFactory getInstance(){
//		return log!=null?log:new LogInfoFactory();
//	}

//	public static synchronized LogInfoFactory getInstance(){
//		return new LogInfoFactory();
//	}

    public static synchronized LogInfoFactory getInstance(Class<?> cs) {
        return new LogInfoFactory(cs);
    }

    /**
     * <pre>
     * 3:ERR
     * 2:INFO
     * 1:WARN
     * 0:DEBUG
     * </pre>
     */
    public void setLevel(int levelcode) {
        this.LEVELCODE = levelcode;
    }

    /**
     * 根据LEVEL设置日志级别
     * LEVEL从配置文件读取
     */
    public void setLevel() {
        switch (EnumLevel.valueOf(LEVEL)) {
            case ERR:
                this.LEVELCODE = 3;
                break;
            case INFO:
                this.LEVELCODE = 2;
                break;
            case WARN:
                this.LEVELCODE = 1;
                break;
            case DEBUG:
                this.LEVELCODE = 0;
                break;
            default:
                this.LEVELCODE = 0;
                break;
        }
    }

    private boolean isLogLevel(String LEVEL) {
        if (LEVEL.equals(EnumLevel.ERR.getCode())) {
            return getLEVELCODE() <= 3;
        } else if (LEVEL.equals(EnumLevel.INFO.getCode())) {
            return getLEVELCODE() <= 2;
        } else if (LEVEL.equals(EnumLevel.WARN.getCode())) {
            return getLEVELCODE() <= 1;
        } else if (LEVEL.equals(EnumLevel.DEBUG.getCode())) {
            return getLEVELCODE() <= 0;
        } else {
            return getLEVELCODE() <= 0;
        }
    }

    private int getLEVELCODE() {
        return this.LEVELCODE;
    }

    // TODO 循环判断类名，如果是线程，得打印线程名
    public static String getLineInfo() {
        StackTraceElement[] ste = new Throwable().getStackTrace();
//        System.out.println("getLineInfo：" + Arrays.asList(ste));
        return ":" + ste[ste.length - 2].getLineNumber();
    }

    /**
     * 打印
     *
     * @param level
     * @param msg
     */
    private void print(String level, String msg) {
        StringBuffer output = new StringBuffer();
//		if(isNeedTime)
        output.append("[")
                .append(level)
                .append(BLANK_SPACE)
                .append(simpleDateFormat.format(new java.util.Date()))
                .append(BLANK_SPACE)
                .append(cs.getCanonicalName())
                .append(getLineInfo())
                .append("]");
        output.append(msg);
        System.out.println(output.toString());
    }

    /**
     * 使用objs替换msg中的{}
     *
     * @param level
     * @param msg
     * @param objs
     */
    private void print(String level, String msg, Object... objs) {
        msg = msg + BLANK_SPACE;
        String[] msgarr = msg.split(SPLIT_STR);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < msgarr.length; i++) {
            sb.append(msgarr[i]);
            if (i < (msgarr.length - 1))
                sb.append(objs[i]);
        }
        print(level, sb.toString());
    }

    public void error(String msg) {
        error(msg, null);
    }

    public void error(String msg, Throwable throwable) {
        if (isLogLevel(EnumLevel.ERR.getCode())) {
            print(EnumLevel.ERR.getCode(), msg);
            if (throwable != null)
                throwable.printStackTrace();
        }
    }

    public void info(String msg) {
        if (isLogLevel(EnumLevel.INFO.getCode())) {
            print(EnumLevel.INFO.getCode(), msg);
        }
    }

    /**
     * 使用objs替换msg中的{}
     *
     * @param msg
     * @param objs
     */
    public void info(String msg, Object... objs) {
        if (isLogLevel(EnumLevel.INFO.getCode())) {
            print(EnumLevel.INFO.getCode(), msg, objs);
        }
    }

    public void warn(String msg) {
        if (isLogLevel(EnumLevel.WARN.getCode())) {
            print(EnumLevel.WARN.getCode(), msg);
        }
    }

    public void warn(String msg, Object... objs) {
        if (isLogLevel(EnumLevel.WARN.getCode())) {
            print(EnumLevel.WARN.getCode(), msg, objs);
        }
    }

    public void debug(String msg) {
        if (isLogLevel(EnumLevel.DEBUG.getCode())) {
            print(EnumLevel.DEBUG.getCode(), msg);
        }
    }

    public void debug(String msg, Object... objs) {
        if (isLogLevel(EnumLevel.DEBUG.getCode())) {
            print(EnumLevel.DEBUG.getCode(), msg, objs);
        }
    }

    public void debug(String msg, Throwable throwable) {
        if (isLogLevel(EnumLevel.DEBUG.getCode())) {
            print(EnumLevel.DEBUG.getCode(), msg);
            if (throwable != null)
                throwable.printStackTrace();
        }
    }
}
