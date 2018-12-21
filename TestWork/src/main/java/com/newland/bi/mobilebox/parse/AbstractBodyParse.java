package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;

/**
 * 信息体解析类
 *
 * @author chenqixu
 */
public abstract class AbstractBodyParse {

    //事件编码
    protected static int code = 0;

    protected final String KEY_LEFT = "###";

    protected BodyInfo bodyInfo = new BodyInfo();

    /**
     * 具体事件解析
     */
    public abstract BodyInfo parse(String leftoverstr);
}
