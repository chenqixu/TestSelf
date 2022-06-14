package com.bussiness.bi.bigdata.metric;

import com.bussiness.bi.bigdata.utils.OtherUtils;

/**
 * 监控工具类
 *
 * @author chenqixu
 */
public class MetricsUtil {

    private int stepCnt = 0;
    private int cnt = 0;
    private TimeOut timeOut;
    private TimeLevel timeLevel = TimeLevel.Millis;

    public static MetricsUtil builder() {
        return new MetricsUtil();
    }

    public void setMic() {
        timeLevel = TimeLevel.Mic;
    }

    public void setMillis() {
        timeLevel = TimeLevel.Millis;
    }

    public String getStep() {
        return this + "##step" + stepCnt + "##";
    }

    /**
     * 增加监控
     */
    public void addTimeTag() {
        timeOut = new TimeOut();
        stepCnt++;
    }

    /**
     * 获取对象的运行时长
     *
     * @return
     */
    public long getTimeOut() {
        return timeOut == null ? 0l : timeOut.getTime();
    }

    public void increase() {
        cnt++;
    }

    public int getCnt() {
        return cnt;
    }

    enum TimeLevel {
        Millis, Mic;
    }

    class TimeOut {
        long time = 0l;

        public TimeOut() {
            switch (timeLevel) {
                case Mic:
                    time = OtherUtils.getMicTime();
                    break;
                case Millis:
                    time = OtherUtils.getMillisTime();
                    break;
                default:
                    break;
            }
        }

        /**
         * 返回运行时长
         *
         * @return
         */
        public long getTime() {
            long result = 0l;
            switch (timeLevel) {
                case Mic:
                    result = OtherUtils.getMicTime() - time;
                    break;
                case Millis:
                    result = OtherUtils.getMillisTime() - time;
                    break;
                default:
                    break;
            }
            return result;
        }
    }
}
