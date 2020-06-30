package com.cqx.pierce.clipboard;

import com.alibaba.fastjson.JSON;
import com.cqx.pierce.bean.ClipBoardValue;

/**
 * 剪贴板
 *
 * @author chenqixu
 */
public class ClipBoard {
    private ClipBoardStatus status;
    private ClipBoardValue clipBoardValue;
    private String clipValue;

    public ClipBoard() {
        setClipValue(ClipBoardStatus.VM_WAIT, new ClipBoardValue());
    }

    void read() {
        //解析clipValue，分解成status和value
        //status是两位的数字
        String status = clipValue.substring(0, 2);
        String value = clipValue.substring(2);
        this.status = ClipBoardStatus.valueOfClipBoardStatus(status);
        this.clipBoardValue = JSON.parseObject(value, ClipBoardValue.class);
        //###########################################
        //value
        //###########################################
        //type[PLSQL、SecureCRT、PUT、GET]，params[Map<String, String>]
        //###########################################
        //vm send
        //###########################################
        //PLSQL：tns、user_name、pass_word、sql
        //SecureCRT：ip、user_name、pass_word、cmd
        //PUT：ip、user_name、pass_word、local_file、remote_path
        //GET：ip、user_name、pass_word、remote_file、local_path
        //###########################################
        //mstsc send
        //###########################################
        //PLSQL：
        //SecureCRT：
        //PUT：
        //GET：
    }

    public ClipBoardStatus getStatus() {
        return status;
    }

    public void setClipValue(ClipBoardStatus status, ClipBoardValue clipBoardValue) {
        this.clipValue = status.getCode() + JSON.toJSONString(clipBoardValue);
    }

    public ClipBoardValue getClipBoardValue() {
        return clipBoardValue;
    }

    /**
     * 定义两位数，从0到99，个位留给特殊状态<br>
     * 1、VM从1开始<br>
     * 2、MSTSC从2开始
     *
     * @author chenqixu
     */
    public enum ClipBoardStatus {
        UNKNOW("00"),
        VM_WAIT("10"),
        VM_SEND("11"),
        VM_RELEASE("12"),
        MSTSC_WAIT("20"),
        MSTSC_SEND("21"),
        MSTSC_RELEASE("22");

        private String code;

        ClipBoardStatus(String code) {
            this.code = code;
        }

        public static ClipBoardStatus valueOfClipBoardStatus(String value) {
            switch (value) {
                case "00":
                    return UNKNOW;
                case "10":
                    return VM_WAIT;
                case "11":
                    return VM_SEND;
                case "12":
                    return VM_RELEASE;
                case "20":
                    return MSTSC_WAIT;
                case "21":
                    return MSTSC_SEND;
                case "22":
                    return MSTSC_RELEASE;
                default:
                    return UNKNOW;
            }
        }

        public String getCode() {
            return code;
        }
    }
}
