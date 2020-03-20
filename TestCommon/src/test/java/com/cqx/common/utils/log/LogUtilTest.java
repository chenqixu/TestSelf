package com.cqx.common.utils.log;

import com.cqx.common.utils.system.SleepUtil;
import org.junit.Test;

public class LogUtilTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(LogUtilTest.class);

    @Test
    public void testEnum() throws Exception {
        LogLEVEL logLEVEL;
        try {
            logLEVEL = Enum.valueOf(LogLEVEL.class, "INFO");
        } catch (Exception e) {
            System.out.println("parse error，use defaults log level.");
            logLEVEL = LogLEVEL.INFO;
        }
        System.out.println(logLEVEL);
    }

    @Test
    public void testThread() throws Exception {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 10) {
                    i++;
                    logger.info("i：{}", i);
                    SleepUtil.sleepMilliSecond(50);
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 10) {
                    i++;
                    logger.info("i：{}", i);
                    SleepUtil.sleepMilliSecond(50);
                }
            }
        });
        logger.info("{} ready start...", t1);
        t1.start();
        logger.info("{} ready start...", t2);
        t2.start();
        t1.join();
        t2.join();
    }

    /**
     * 范围
     *     转义符之后的字符都会变成转义符所表示的样式
     * <p>
     * 样式
     *     0  空样式
     *     1  粗体
     *     4  下划线
     *     7  反色
     * <p>
     *     颜色1：
     *     30  白色
     *     31  红色
     *     32  绿色
     *     33  黄色
     *     34  蓝色
     *     35  紫色
     *     36  浅蓝
     *     37  灰色
     *     背景颜色：
     *     40-47 和颜色顺序相同
     * <p>
     *     颜色2：
     *     90-97  比颜色1更鲜艳一些
     */
    @Test
    public void testColor() {
        System.out.println("Hello,Color!");
        System.out.println("\033[30;0m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[31;0m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[32;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[33;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[34;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[35;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[36;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[37;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[40;31;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[41;32;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[42;33;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[43;34;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[44;35;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[45;36;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[46;37;4m" + "Hello,Color!" + "\033[0m");
        System.out.println("\033[47;4m" + "Hello,Color!" + "\033[0m");
    }
}