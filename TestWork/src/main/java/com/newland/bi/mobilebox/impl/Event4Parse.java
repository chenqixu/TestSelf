package com.newland.bi.mobilebox.impl;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.DeviceInfo;
import com.newland.bi.mobilebox.parse.AbstractBodyParse;

/**
 * 终端盒子型号解析
 *
 * @author chenqixu
 */
public class Event4Parse extends AbstractBodyParse {

    @Override
    public int getCode() {
        return 4;
    }

    @Override
    public BodyInfo parse(String leftoverstr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setTerminalMode(leftoverstr);
        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }
}
