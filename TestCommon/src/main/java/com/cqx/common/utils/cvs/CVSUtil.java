package com.cqx.common.utils.cvs;

import org.netbeans.lib.cvsclient.CVSRoot;
import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.commandLine.BasicListener;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.connection.ConnectionFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * CVS工具类
 *
 * @author chenqixu
 */
public class CVSUtil implements Closeable {
    /**
     * 连接字符串
     */
    private String CONNECTION_STRING = ":pserver:linshutao:123456@168.168.0.77:/home/cvsroot";
    /**
     * Cvs clinet instance used to communicate with cvs server
     */
    private Client cvsclient = null;
    /**
     * Cvs connect string
     */
    private CVSRoot cvsroot = null;
    /**
     * Connection instance to keep connect with cvs server
     */
    private Connection connection = null;
    /**
     * Global options to store the requied parameter for cvs server
     */
    private GlobalOptions globalOptions = null;
    /**
     * The local path on ur local machine
     */
    private String LOCALPATH = "d:/cvs_checkout";

    /**
     * 构造
     *
     * @param CONNECTION_STRING 连接字符串，格式：:pserver:linshutao:123456@168.168.0.77:/home/cvsroot
     * @param LOCALPATH         本地路径
     */
    public CVSUtil(String CONNECTION_STRING, String LOCALPATH) {
        this.CONNECTION_STRING = CONNECTION_STRING;
        this.LOCALPATH = LOCALPATH;
    }

    /**
     * 打开CVS
     *
     * @return
     * @throws AuthenticationException
     * @throws IOException
     * @throws CommandException
     */
    private Connection openConnection() throws AuthenticationException,
            IOException, CommandException {
        cvsroot = CVSRoot.parse(CONNECTION_STRING);
        connection = ConnectionFactory.getConnection(cvsroot);
        cvsclient = new Client(connection, new StandardAdminHandler());
        cvsclient.setLocalPath(LOCALPATH);
        cvsclient.getEventManager().addCVSListener(new BasicListener());
        connection.open();

        globalOptions = new GlobalOptions();
        globalOptions.setCVSRoot(CVSRoot.parse(CONNECTION_STRING).getRepository());

        return connection;
    }

    public Client getCvsclient() throws AuthenticationException, IOException, CommandException {
        if (cvsclient == null) {
            openConnection();
        }
        return cvsclient;
    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            connection.close();
        }
    }
}
