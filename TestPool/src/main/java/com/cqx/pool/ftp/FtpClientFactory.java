package com.cqx.pool.ftp;

import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.pro.ProFTPClient;
import com.enterprisedt.net.ftp.pro.ProFTPClientInterface;
import com.enterprisedt.net.ftp.ssh.SSHFTPClient;
import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.license.License;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author chenqixu
 * @description Ftp客户端工厂
 * @date 2018/11/28 17:17
 */
public class FtpClientFactory implements KeyedPooledObjectFactory<FtpCfg, NlFtpClient> {


    static {
        /**
         * 初始化证书信息
         */
        License.setLicenseDetails("hello", "371-2454-4908-7510");
        com.enterprisedt.util.debug.Logger.setLevel(Level.WARN);
    }

    private static final Logger logger = LoggerFactory.getLogger(FtpClientFactory.class);
    public final static int CONNECT_TIME_WAIT = 30 * 1000; // 30秒获取连接超时

    /**
     * 这个方法是用来创建一个对象，当在GenericObjectPool类中调用borrowObject方法时，如果当前对象池中没有空闲的对象，GenericObjectPool会调用这个方法，创建一个对象，并把这个对象封装到PooledObject类中，并交给对象池管理。
     */
    @Override
    public PooledObject<NlFtpClient> makeObject(FtpCfg ftpCfg) throws Exception {
        logger.info("ftp连接工厂新创建FTP连接:{}", ftpCfg);
        ProFTPClientInterface client = createFtpClient(ftpCfg);
        NlFtpClient nlClient = new NlFtpClient(ftpCfg, client);
        logger.info("ftp连接工厂成功新创建FTP连接:{}", nlClient);
        return new DefaultPooledObject<NlFtpClient>(nlClient);
    }

    /**
     * 销毁对象，当对象池检测到某个对象的空闲时间(idle)超时，或使用完对象归还到对象池之前被检测到对象已经无效时，就会调用这个方法销毁对象。对象的销毁一般和业务相关，但必须明确的是，当调用这个方法之后，对象的生命周期必须结果。如果是对象是线程，线程必须已结束，如果是socket，socket必须已close，如果是文件操作，文件数据必须已flush，且文件正常关闭。
     */
    @Override
    public void destroyObject(FtpCfg ftpCfg, PooledObject<NlFtpClient> clientObject) throws Exception {
        NlFtpClient nlClient = clientObject.getObject();
        logger.info("ftp连接工厂释放一个FTP连接{} key:{}", nlClient.getClientUUid(), ftpCfg);
        nlClient.disconnect();
    }

    /**
     * 检测一个对象是否有效。在对象池中的对象必须是有效的，这个有效的概念是，从对象池中拿出的对象是可用的。比如，如果是socket,那么必须保证socket是连接可用的。在从对象池获取对象或归还对象到对象池时，会调用这个方法，判断对象是否有效，如果无效就会销毁
     */
    @Override
    public boolean validateObject(FtpCfg ftpCfg, PooledObject<NlFtpClient> clientObject) {
        NlFtpClient nlClient = clientObject.getObject();
        logger.debug("ftp连接工厂验证一个FTP连接{} key:{}", nlClient.getClientUUid(), ftpCfg);

        return nlClient.isConnected();
    }

    /**
     * 激活一个对象或者说启动对象的某些操作。比如，如果对象是socket，如果socket没有连接，或意外断开了，可以在这里启动socket的连接。它会在检测空闲对象的时候，如果设置了测试空闲对象是否可以用，就会调用这个方法，在borrowObject的时候也会调用。另外，如果对象是一个包含参数的对象，可以在这里进行初始化。让使用者感觉这是一个新创建的对象一样。
     */
    @Override
    public void activateObject(FtpCfg key, PooledObject<NlFtpClient> p) throws Exception {
        logger.debug("ftp连接工厂激活一个FTP连接{} ", p.getObject());

    }

    /**
     * 钝化一个对象。在向对象池归还一个对象是会调用这个方法。这里可以对对象做一些清理操作。比如清理掉过期的数据，下次获得对象时，不受旧数据的影响。
     * 一般来说activateObject和passivateObject是成对出现的。前者是在对象从对象池取出时做一些操作，后者是在对象归还到对象池做一些操作，可以根据自己的业务需要进行取舍。
     */
    @Override
    public void passivateObject(FtpCfg key, PooledObject<NlFtpClient> p) throws Exception {
        logger.debug("ftp连接工厂钝化一个FTP连接{} ", p.getObject());

    }

    /**********************************
     * 以下是FTP连接和验证的方法
     *************************************************************/
    // 使用一个独立的线程池，异步构建ftp连接。否则ftp连接超时可能会导致任务阻塞几分钟
    final static ExecutorService es = Executors.newFixedThreadPool(3);

    /**
     * 连接远程服务器
     *
     * @return
     * @throws Exception
     * @throws IOException
     */
    private ProFTPClientInterface createFtpClient(FtpCfg ftpCfg) throws Exception {

        logger.info("ftp开始连接:" + ftpCfg);
        ProFTPClientInterface client = null;
        if (ftpCfg.isUseSftp()) {
            final SSHFTPClient sshftpClient = new SSHFTPClient();
            sshftpClient.setRemoteHost(ftpCfg.getHost());
            sshftpClient.setRemotePort(ftpCfg.getPort());
            sshftpClient.getValidator().setHostValidationEnabled(false);
            sshftpClient.setAuthentication(ftpCfg.getUser(), ftpCfg.getPassword());
            sshftpClient.setTimeout(0);
            connectFtpClient(ftpCfg, sshftpClient, CONNECT_TIME_WAIT);

            if (ftpCfg.getContorlCharset() != null) {
                logger.info("设置ftp字符编码{}", ftpCfg.getContorlCharset());
                sshftpClient.setControlEncoding(ftpCfg.getContorlCharset());
            }
            client = sshftpClient;
        } else {
            ProFTPClient proFTPClient = new ProFTPClient();
            proFTPClient.setRemoteHost(ftpCfg.getHost());
            proFTPClient.setRemotePort(ftpCfg.getPort());
            proFTPClient.setTimeout(0);
            if (ftpCfg.getContorlCharset() != null) {
                logger.info("设置ftp字符编码{}", ftpCfg.getContorlCharset());
                proFTPClient.setControlEncoding(ftpCfg.getContorlCharset());
            }
            connectFtpClient(ftpCfg, proFTPClient, CONNECT_TIME_WAIT);
            proFTPClient.login(ftpCfg.getUser(), ftpCfg.getPassword());
            client = proFTPClient;
            setMode(proFTPClient, ftpCfg);
        }

        logger.info("ftp连接成功");

        return client;

    }

    private void setMode(ProFTPClient client, FtpCfg ftpCfg) throws Exception {

        if (!ftpCfg.isUseSftp()) // FTP模式有主被动设置
        {
            // 判断采用主被动模式
            if (ftpCfg.isUseActive()) {
                // 判断是否采用指定端口
                if (ftpCfg.getActiveEndPort() == 0 || ftpCfg.getActiveStartPort() == 0) {
                    logger.info("采用主动方式连接");
                    client.setConnectMode(FTPConnectMode.ACTIVE);
                } else {
                    logger.info("采用主动方式连接，端口范围：" + ftpCfg.getActiveStartPort() + "-" + ftpCfg.getActiveEndPort());
                    client.setActivePortRange(ftpCfg.getActiveStartPort(), ftpCfg.getActiveEndPort());
                    client.setConnectMode(FTPConnectMode.ACTIVE);
                }
            } else {
                logger.info("设置ftp为被动模式");
                client.setConnectMode(FTPConnectMode.PASV);
            }
        }

        // 判断采用二进制传输还是ascii
        if (ftpCfg.isUseBinary()) {
            logger.info("设置ftp传输方式为二进制方式传输");
            client.setType(FTPTransferType.BINARY);
        } else {
            logger.info("设置ftp传输方式为ASCII方式传输");
            client.setType(FTPTransferType.ASCII);
        }

    }

    private void connectFtpClient(FtpCfg ftpCfg, ProFTPClientInterface client, int timeoutMs) throws Exception {
        ConnectCallable callable = new ConnectCallable(ftpCfg, client, timeoutMs);
        Future<Boolean> ret = es.submit(callable);
        boolean done = false;
        try {
            done = ret.get(timeoutMs, TimeUnit.MILLISECONDS);
        } finally {
            if (!done) {
                ret.cancel(true);
            }
        }
    }

    static class ConnectCallable implements Callable<Boolean> {
        private ProFTPClientInterface client;
        private long timeoutMs;
        long start = System.currentTimeMillis();
        FtpCfg ftpCfg;

        public ConnectCallable(FtpCfg ftpCfg, ProFTPClientInterface client, long timeoutMs) {
            this.client = client;
            this.timeoutMs = timeoutMs;
            this.ftpCfg = ftpCfg;
        }

        @Override
        public Boolean call() throws Exception {
            client.connect();
//            StaticThreadSleep.sleep(3 * 1000);
            // 防止外部已经取消了这个线程，但线程依然连接上了。导致连接对象泄漏。
            if (System.currentTimeMillis() - start - 1000 > this.timeoutMs) {
                try {
                    client.quitImmediately();
                } catch (Exception e) {

                }
                throw new RuntimeException("连接" + ftpCfg.getHost() + "超时,获取连接等待已经结束");
            }
            return true;
        }

    }

    ;
}
