package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.DeviceInfo;

/**
 * 终端盒子ID解析
 *
 * @author chenqixu
 */
public class Event5Parse extends AbstractBodyParse {

    protected static int code = 5;

    @Override
    public BodyInfo parse(String leftoverstr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setTerminalID(leftoverstr);
        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }
}
