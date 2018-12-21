package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.DeviceInfo;

/**
 * 探针程序版本号解析
 *
 * @author chenqixu
 */
public class Event10Parse extends AbstractBodyParse {

    protected static int code = 10;

    @Override
    public BodyInfo parse(String leftoverstr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setProbeSoftVersion(leftoverstr);
        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }
}
