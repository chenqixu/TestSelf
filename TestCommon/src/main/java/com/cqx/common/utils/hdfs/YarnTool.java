package com.cqx.common.utils.hdfs;

import com.cqx.common.utils.io.MyByteArrayOutputStream;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.yarn.client.cli.ApplicationCLI;
import org.apache.hadoop.yarn.client.cli.LogsCLI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * yarn工具类
 *
 * @author chenqixu
 */
public class YarnTool {
    private static final Logger logger = LoggerFactory.getLogger(YarnTool.class);
    private HdfsBean hdfsBean;
    private HdfsTool hdfsTool;

    public YarnTool(HdfsBean hdfsBean) {
        this.hdfsBean = hdfsBean;
        this.hdfsTool = new HdfsTool();
    }

    /**
     * 获取所有应用<br>
     * yarn application -list -appStates ALL
     *
     * @throws Exception
     */
    public void getAllJob() throws Exception {
        String[] args = {"application", "-list", "-appStates", "ALL"};
        ApplicationCLIEx cli = new ApplicationCLIEx(hdfsTool.getConf(hdfsBean.getHadoop_conf(), hdfsBean, true, true));
        cli.setSysOutPrintStream(MyByteArrayOutputStream.buildPrintStream(value -> {
            if (value.startsWith("application_")) {
                YarnApplicationBean yarnApplicationBean = new YarnApplicationBean(value, "\t");
                if (yarnApplicationBean.isHasValue()) {
                    logger.info("解析成功：{}", yarnApplicationBean);
                } else {
                    logger.info("未解析成功：{}", value);
                }
            }
        }));
        cli.setSysErrPrintStream(System.err);
        ToolRunner.run(cli, ApplicationCLI.preProcessArgs(args));
        cli.stop();
    }

    /**
     * 获取正在运行的任务
     *
     * @throws Exception
     */
    public void getRunningJob() throws Exception {
        String[] args = {"application", "-list", "-appStates", "RUNNING"};
        ApplicationCLIEx cli = new ApplicationCLIEx(hdfsTool.getConf(hdfsBean.getHadoop_conf(), hdfsBean, true, true));
        cli.setSysOutPrintStream(MyByteArrayOutputStream.buildPrintStream(value -> {
            if (value.startsWith("application_")) {
                YarnApplicationBean yarnApplicationBean = new YarnApplicationBean(value, "\t");
                if (yarnApplicationBean.isHasValue()) {
                    logger.info("解析成功：{}", yarnApplicationBean);
                } else {
                    logger.info("未解析成功：{}", value);
                }
            }
        }));
        cli.setSysErrPrintStream(System.err);
        ToolRunner.run(cli, ApplicationCLI.preProcessArgs(args));
        cli.stop();
    }

    /**
     * 获取某个应用的日志<br>
     * 看所有日志<br>
     * yarn logs -applicationId application_1493700892407_0007<br>
     * 看有什么日志文件<br>
     * yarn logs -applicationId application_1667339730322_26572 -show_container_log_info<br>
     * 看指定日志文件的日志<br>
     * yarn logs -applicationId application_1667339730322_26572 -log_files syslog<br>
     * 需要这个依赖hadoop-plugins-8.0.2-302022.jar<br>
     * 原因是要通过org.apache.hadoop.hdfs.server.namenode.ha.AdaptiveFailoverProxyProvider来连接HDFS，从而获取到日志文件<br>
     * 这个配置在hdfs-site.xml里，key是dfs.client.failover.proxy.provider.hacluster
     *
     * @param job_id
     * @throws Exception
     */
    public void queryJobLog(String job_id) throws Exception {
        String[] args = {"logs", "-applicationId", job_id};
        LogsCLI logDumper = new LogsCLI();
        logDumper.setConf(hdfsTool.getConf(hdfsBean.getHadoop_conf(), hdfsBean, true, true));
        logDumper.run(args);
    }
}
