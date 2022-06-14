package com.bussiness.bi.mobilebox.parse;

import com.bussiness.bi.mobilebox.bean.BodyInfo;

/**
 * 信息体解析类
 *
 * @author chenqixu
 */
public abstract class AbstractBodyParse {

    //事件编码
    public abstract int getCode();

    protected final String KEY_LEFT = "###";

    protected BodyInfo bodyInfo = new BodyInfo();

    /**
     * 具体事件解析
     */
    public abstract BodyInfo parse(String leftoverstr);
}
