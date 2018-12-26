package com.newland.bi.mobilebox.impl;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.DeviceInfo;
import com.newland.bi.mobilebox.parse.AbstractBodyParse;
import com.newland.bi.mobilebox.utils.BodyImpl;

/**
 * APK版本号解析
 *
 * @author chenqixu
 */
@BodyImpl
public class Event3Parse extends AbstractBodyParse {

    @Override
    public int getCode() {
        return 3;
    }

    @Override
    public BodyInfo parse(String leftoverstr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setApkVersion(leftoverstr);
        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }

}
