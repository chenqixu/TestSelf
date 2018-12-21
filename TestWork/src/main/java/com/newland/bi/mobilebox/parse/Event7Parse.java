package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.DeviceInfo;

/**
 * 接入方式解析
 *
 * @author chenqixu
 */
public class Event7Parse extends AbstractBodyParse {

    protected static int code = 7;

    @Override
    public BodyInfo parse(String leftoverstr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setConnectMode(leftoverstr);
        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }
}
