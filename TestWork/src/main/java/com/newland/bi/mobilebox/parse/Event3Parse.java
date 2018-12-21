package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.DeviceInfo;

/**
 * APK版本号解析
 *
 * @author chenqixu
 */
public class Event3Parse extends AbstractBodyParse {

    protected static int code = 3;

    @Override
    public BodyInfo parse(String leftoverstr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setApkVersion(leftoverstr);
        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }

}
