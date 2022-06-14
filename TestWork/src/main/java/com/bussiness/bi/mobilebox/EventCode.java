package com.bussiness.bi.mobilebox;

/**
 * 事件
 *
 * @author chenqixu
 */
public enum EventCode implements IEventCode {
    EVENT1(0x0001, "用户ID"),
    EVENT2(0x0002, "用户位置"),
    EVENT3(0x0003, "APK版本号"),
    EVENT4(0x0004, "终端盒子型号"),
    EVENT5(0x0005, "终端盒子ID"),
    EVENT6(0x0006, "固件版本"),
    EVENT7(0x0007, "接入方式"),
    EVENT9(0x0009, "界面布局xml"),
    EVENT10(0x000A, "探针程序版本号"),
    EVENT11(0x000B, "牌照方"),
    EVENT12(0x000C, "机顶盒厂家"),
    EVENT17(0x0011, "开机时延"),
    EVENT18(0x0012, "设备基本信息"),
    EVENT257(0x0101, "用户搜索"),
    EVENT258(0x0102, "海报打开时长（ms)"),
    EVENT259(0x0103, "频道切换等待时长(ms)"),
    EVENT260(0x0104, "打开app"),
    EVENT262(0x0106, "入口信息"),
    EVENT263(0x0107, "机顶盒最上面一行的入口"),
    EVENT513(0x0201, "视频播放开始"),
    EVENT514(0x0202, "视频播放结束(ms)"),
    EVENT515(0x0203, "视频播放失败"),
    EVENT516(0x0204, "首帧等待时长(ms)"),
    EVENT517(0x0205, "播放卡顿时长(ms)"),
    EVENT523(0x020B, "节目基本信息"),
    EVENT526(0x020E, "节目推荐的视频播放开始"),
    EVENT527(0x020F, "节目推荐的视频播放结束(ms)");

    private EventCode(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private final int code;
    private final String name;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Name:[%s].", this.code, this.name);
    }
}
