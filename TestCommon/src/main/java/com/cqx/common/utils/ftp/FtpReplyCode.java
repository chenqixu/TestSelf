package com.cqx.common.utils.ftp;

import java.util.HashMap;
import java.util.Map;

/**
 * FTP应答码
 *
 * @author chenqixu
 */
public class FtpReplyCode {

    private static Map<Integer, FtpReplyCode> ftpReplyCodeMap = new HashMap<>();

    static {
        put(110, "重新启动标记应答");
        put(120, "在n分钟内准备好");
        put(125, "连接打开准备传送");
        put(150, "打开数据连接");
        put(200, "命令成功");
        put(202, "命令失败");
        put(211, "系统状态");
        put(212, "目录状态");
        put(213, "文件状态");
        put(214, "帮助信息");
        put(215, "名字系统类型");
        put(220, "新用户服务准备好了");
        put(221, "服务关闭控制连接，可以退出登录");
        put(225, "数据连接打开，无传输正在进行");
        put(226, "关闭数据连接，请求的文件操作成功");
        put(227, "进入被动模式");
        put(230, "用户登录");
        put(250, "请求的文件操作完成");
        put(257, "创建'PATHNAME'");
        put(331, "用户名正确，需要口令");
        put(332, "登录时需要帐户信息");
        put(350, "下一步命令");
        put(421, "不能提供服务，关闭控制连接");
        put(425, "不能打开数据连接");
        put(426, "关闭连接，中止传输");
        put(450, "请求的文件操作未执行");
        put(451, "中止请求的操作：有本地错误");
        put(452, "未执行请求的操作：系统存储空间不足");
        put(500, "格式错误，命令不可识别");
        put(501, "参数语法错误");
        put(502, "命令未实现");
        put(503, "命令顺序错误");
        put(504, "此参数下的命令功能未实现");
        put(530, "未登录");
        put(532, "存储文件需要帐户信息");
        put(550, "未执行请求的操作");
        put(551, "请求操作中止：页类型未知");
        put(552, "请求的文件操作中止，存储分配溢出");
        put(553, "未执行请求的操作：文件名不合法");
    }

    private int code;
    private String desc;

    private FtpReplyCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static void put(int code, String desc) {
        ftpReplyCodeMap.put(code, new FtpReplyCode(code, desc));
    }

    public static FtpReplyCode getFtpReplyCodeByCode(int code) {
        return ftpReplyCodeMap.get(code);
    }

    public String toString() {
        return "code：" + code + "，desc：" + desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
