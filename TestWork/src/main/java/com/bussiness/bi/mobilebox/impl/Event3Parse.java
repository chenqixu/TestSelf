package com.bussiness.bi.mobilebox.impl;

import com.bussiness.bi.mobilebox.bean.BodyInfo;
import com.bussiness.bi.mobilebox.bean.DeviceInfo;
import com.bussiness.bi.mobilebox.parse.AbstractBodyParse;
import com.bussiness.bi.mobilebox.utils.BodyImpl;

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
