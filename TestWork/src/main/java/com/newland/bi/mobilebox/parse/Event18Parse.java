package com.newland.bi.mobilebox.parse;

import com.newland.bi.mobilebox.bean.BodyInfo;
import com.newland.bi.mobilebox.bean.DeviceInfo;

/**
 * 设备基本信息解析
 *
 * @author chenqixu
 */
public class Event18Parse extends AbstractBodyParse {

    protected static int code = 18;

    @Override
    public BodyInfo parse(String leftoverstr) {
        DeviceInfo deviceInfo = new DeviceInfo();
        /**
         * 用户ID###
         * APK版本号###
         * 终端盒子型号###
         * 终端盒子ID###
         * 接入方式###
         * 探针程序版本号###
         * 牌照方###
         * 机顶盒厂家###
         * 开机时延#ip#有线mac#无线mac)
         */
        String[] arr = leftoverstr.split(KEY_LEFT);
        deviceInfo.setUserID(arr[0]);
        deviceInfo.setApkVersion(arr[1]);
        deviceInfo.setTerminalMode(arr[2]);
        deviceInfo.setTerminalID(arr[3]);
        deviceInfo.setConnectMode(arr[4]);
        deviceInfo.setProbeSoftVersion(arr[5]);
        deviceInfo.setLicencesName(arr[6]);
        deviceInfo.setSupplierName(arr[7]);

        bodyInfo.setBody(deviceInfo);
        return bodyInfo;
    }
}
