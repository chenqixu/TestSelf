package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.DeviceInfo;

/**
 * 机顶盒厂家解析
 *
 * @author chenqixu
 */
public class Event12Parse extends AbstractBodyParse {

    protected static int code = 12;

    @Override
    public BodyInfo parse(String leftoverstr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSupplierName(leftoverstr);
        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }
}
