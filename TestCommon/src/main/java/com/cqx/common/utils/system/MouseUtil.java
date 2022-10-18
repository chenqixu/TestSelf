package com.cqx.common.utils.system;

import java.awt.*;
import java.awt.event.InputEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 鼠标工具
 *
 * @author chenqixu
 */
public class MouseUtil {
    private Robot robot;
    private boolean isClick = false;

    public MouseUtil() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public MouseUtil(boolean isClick) {
        this();
        this.isClick = isClick;
    }

    public static void main(String[] args) throws Exception {
        int delay = 3;
        int period = 15;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MouseUtil mouseUtil = new MouseUtil();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int x = 250;
                int y = 250;
                // 鼠标移动到x, y，然后进行点击
                mouseUtil.move(x, y);
                System.out.println(String.format("%s 鼠标移动到%s, %s，%s"
                        , sdf.format(new Date())
                        , x
                        , y
                        , mouseUtil.isClick() ? "然后进行点击" : "无操作"));
            }
        }, delay * 1000, period * 1000); //delay秒后启动，每隔period秒运行1次
    }

    public void move(int x, int y) {
        robot.mouseMove(x, y);
        if (isClick) {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    public Point getLocation() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    public boolean isClick() {
        return isClick;
    }
}
