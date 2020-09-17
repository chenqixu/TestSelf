package com.cqx.distributed.net;

/**
 * 服务识别码
 *
 * @author chenqixu
 */
public enum ServerCodeEnum {
    Register(100),
    Success(1),
    Fail(0),
    ;

    private int serverCode;

    ServerCodeEnum(int serverCode) {
        this.serverCode = serverCode;
    }

    public static ServerCodeEnum valueOf(int serverCode) {
        switch (serverCode) {
            case 100:
                return Register;
            case 1:
                return Success;
            case 0:
                return Fail;
            default:
                return Fail;
        }
    }

    public int getServerCode() {
        return this.serverCode;
    }

}
