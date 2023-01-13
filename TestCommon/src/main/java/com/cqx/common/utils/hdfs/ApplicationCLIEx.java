package com.cqx.common.utils.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.client.cli.ApplicationCLI;

/**
 * ApplicationCLIEx<br>
 * <pre>
 *     由于ApplicationCLI是空构造
 *     会先执行YarnCLI的构造
 *     在这个构造里，直接通过DefaultNoHARMFailoverProxyProvider获取配置里的ResourceManager
 *     而这个时候，配置是默认配置，根本没有地方输入外部的正确配置，就一直连接无效的ResourceManager
 *     就死循环了
 *     所以需要继承ApplicationCLI，并实现YarnCLI构造里的过程
 * </pre>
 *
 * @author chenqixu
 */
public class ApplicationCLIEx extends ApplicationCLI {

    public ApplicationCLIEx(Configuration conf) {
        setConf(conf);
        this.client = this.createYarnClient();
        this.client.init(this.getConf());
        this.client.start();
    }
}
