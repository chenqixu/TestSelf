package com.cqx.process;

import com.cqx.bean.CmdBean;
import com.bussiness.bi.bigdata.db.JDBCUtil;

import java.util.Scanner;

/**
 * 命令工具
 *
 * @author chenqixu
 */
public class CmdTool {
    private static String loglevel = "info";
    private Scanner scanner;

    private CmdTool() {
        scanner = new Scanner(System.in);
    }

    public static CmdTool newbuilder() {
        return new CmdTool();
    }

    public static void main(String[] args) throws Exception {
        //-t oracle -u bishow -p bishow -d jdbc:oracle:thin:@10.1.0.242:1521:ywxx
        args[0] = "-t";
        args[1] = "redis";
        args[2] = "-u";
        args[3] = "redis";
        args[4] = "-p";
        args[5] = "redis";
        args[6] = "-d";
        args[7] = "10.1.4.185:6380,10.1.4.185:6381,10.1.4.185:6382,10.1.4.185:6383,10.1.4.185:6384,10.1.4.185:6385";
        args[8] = "-l";
        args[9] = "debug";
        CmdTool.newbuilder().run(args);
    }

    public static void print(String msg) {
        System.out.print(msg);
    }

    public static void println(String msg) {
        System.out.println(msg);
    }

    public static void debug(String msg) {
        if (loglevel.equals("debug"))
            System.out.println(msg);
    }

    /**
     * <pre>
     *     1、类型 -t --type
     *     2、用户 -u --username
     *     3、密码 -p --password
     *     4、连接串 -d --dns
     * </pre>
     *
     * @param args
     */
    public void run(String[] args) throws Exception {
        CmdBean cmdBean = OptionsTool.newbuilder().parser(args).getCmdBean();
        loglevel = cmdBean.getLoglevel();
        println("cmdBean：" + cmdBean);
        JDBCUtil jdbcUtil = new JDBCUtil(cmdBean);
        String type = cmdBean.getType();
        print(type + ">");
        while (scanner.hasNextLine()) {
            String cmd = scanner.nextLine();
            if (cmd.trim().equalsIgnoreCase("exit")
                    || cmd.trim().equalsIgnoreCase("quit")) {
                jdbcUtil.closeAll();
                System.exit(0);
            } else {
                debug("you input:" + cmd);
                try {
                    jdbcUtil.parserSql(cmd);
                } catch (Exception e) {
                    println(e.getMessage());
                }
                print(type + ">");
            }
        }
    }
}
