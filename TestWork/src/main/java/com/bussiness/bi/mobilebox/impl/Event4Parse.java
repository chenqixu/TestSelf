package com.bussiness.bi.mobilebox.impl;

import com.bussiness.bi.mobilebox.bean.BodyInfo;
import com.bussiness.bi.mobilebox.bean.DeviceInfo;
import com.bussiness.bi.mobilebox.parse.AbstractBodyParse;

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
