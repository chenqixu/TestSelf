package com.newland.bi.mobilebox.parse;

import com.newland.bi.bigdata.utils.string.StringUtils;
import com.newland.bi.mobilebox.bean.HeaderInfo;
import com.newland.bi.mobilebox.exception.MobileBoxException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 信息头解析类
 *
 * @author chenqixu
 */
public class HeaderParse {

    //原始日志
    protected String logValue;
    private final String KEY_KY = "#KY#";
    protected final String KEY_LEFT = "###";
    //是否4.0探针
    boolean is4Probe = false;
    //事件编码
    protected int code;
    //设备ID
    protected String deviceid;
    //时间
    protected String time;
    //需要自行解析的字符
    protected String leftoverstr;
    //解析结果对象
    public HeaderInfo parseObj;

    /**
     * 构造：传入原始日志
     *
     * @param logValue
     * @throws MobileBoxException
     */
    public HeaderParse(String logValue) throws MobileBoxException {
        this.logValue = logValue;
        this.parseObj = new HeaderInfo();
        init();
    }

    /**
     * 解析设备ID以及事件编码、时间
     *
     * @throws MobileBoxException
     */
    protected void init() throws MobileBoxException {
        if (StringUtils.isEmpty(logValue)) throw new MobileBoxException("原始日志为空！");
        String[] arr = logValue.split(KEY_KY);
        throwError(arr, 2, "原始日志：{}，无法通过关键字：{}，进行解析", logValue, KEY_KY);
        deviceid = arr[0];
        code = Integer.parseInt(arr[1]);
        leftoverstr = arr[2];
        String[] leftoverarr = splitFirst(leftoverstr, KEY_LEFT);
        time = parseTime(leftoverarr[0]);
        if (is4Probe) leftoverstr = leftoverarr[1];
        parseObj.setCode(code);
        parseObj.setDeviceid(deviceid);
        parseObj.setTime(time);
        parseObj.setIs4Probe(is4Probe);
    }

    /**
     * 异常抛出
     *
     * @param arr
     * @param errorLen
     * @param msg
     * @param objs
     * @throws MobileBoxException
     */
    public void throwError(String[] arr, int errorLen, String msg, Object... objs) throws MobileBoxException {
        if (arr.length <= errorLen) throw new MobileBoxException(msg, objs);
    }

    /**
     * 时间解析
     *
     * @param ptime
     * @return
     */
    public String parseTime(String ptime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat year = new SimpleDateFormat("yyyy-");
        String _tmp = year.format(new Date()) + ptime;
        try {
            //可以解析，认为是4.0探针
            simpleDateFormat.parse(_tmp);
            is4Probe = true;
        } catch (ParseException e) {
            //无法解析，认为是非4.0探针，返回程序操作时间
            _tmp = simpleDateFormat.format(new Date());
            is4Probe = false;
        }
        return _tmp;
    }

    /**
     * 根据key进行切割，拆分成2个元素的数组，第一个元素作为一个单独元素，后面所有元素都合并成一个新元素
     *
     * @param str
     * @param key
     * @return
     */
    public String[] splitFirst(String str, String key) {
        String[] result = new String[2];
        String[] arr = str.split(key);
        result[0] = arr[0];
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < arr.length; i++) {
            sb.append(arr[i]).append(key);
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - key.length(), sb.length());
        }
        result[1] = sb.toString();
        return result;
    }

    public HeaderInfo getParseObj() {
        return parseObj;
    }

    public String getLeftoverstr() {
        return leftoverstr;
    }

    @Override
    public String toString() {
        return parseObj.toString();
    }
}
