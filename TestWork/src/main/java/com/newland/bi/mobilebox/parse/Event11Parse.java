package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.DeviceInfo;

/**
 * 牌照方解析
 *
 * @author chenqixu
 */
public class Event11Parse extends AbstractBodyParse {

    protected static int code = 11;

    @Override
    public BodyInfo parse(String leftoverstr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setLicencesName(leftoverstr);
        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }
}
