package com.cqx.common.utils.cmd;

import com.cqx.common.utils.system.SleepUtil;

/**
 * 命令窗口下的进度条
 *
 * @author chenqixu
 */
public class CmdProgressbar {

    /**
     * 进度条总长度
     */
    private static int TOTLE_LENGTH = 30;

    public static void main(String[] args) {
        for (int i = 0; i <= 100; i++) {
            printSchedule(i);
            SleepUtil.sleepMilliSecond(100);
        }
    }

    public static void printSchedule(int percent) {
        //退格，这里的10是作为末尾的百分比预留的
        for (int i = 0; i < TOTLE_LENGTH + 10; i++) {
            System.out.print("\b");
        }
        int now = TOTLE_LENGTH * percent / 100;
        for (int i = 0; i < now; i++) {
            System.out.print(">");
        }
        for (int i = 0; i < TOTLE_LENGTH - now; i++) {
            System.out.print("=");
        }
        System.out.print("  " + percent + "%");
    }
}
