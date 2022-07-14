package com.cqx.common.utils.cvs;

import com.cqx.common.utils.net.SocketClient;
import com.cqx.common.utils.system.ByteUtil;
import com.cqx.common.utils.system.SleepUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class CVSUtilTest {
    public static final String END = "\n";
    private static final Logger logger = LoggerFactory.getLogger(CVSUtilTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getCvsclient() throws IOException, AuthenticationException, CommandException {
//        try (CVSUtil cvsUtil = new CVSUtil("", "")) {
//            CheckoutCommand checkoutCommand  =new CheckoutCommand();
//        }

        String[] args = {""};
//        CVSCommand.main(args);
        String hello = "BEGIN AUTH REQUEST";
        logger.info("{}", ByteUtil.bytesToHexStringH(hello.getBytes()));
        // 大小写结果一致
        logger.info("{}", ByteUtil.hexStringToBytes("0A"));
        logger.info("{}", ByteUtil.hexStringToBytes("0a"));
        // 10应该是回车，13是换行
        logger.info("{}", "\r".getBytes());
        logger.info("{}", "\n".getBytes());
        String vaild = "Valid-responses ok error Valid-requests Checked-in New-entry Checksum Copy-file Updated Created Update-existing Merged Patched Rcs-diff Update-baserev Mode Mod-time Removed Renamed Remove-entry Set-static-directory Clear-static-directory Set-sticky Clear-sticky Template Notified Module-expansion Wrapper-rcsOption Clear-rename Rename EntriesExtra M Mbinary E F MT NoTranslateBegin NoTranslateEnd";
        String vaildHex = ByteUtil.bytesToHexStringH(vaild.getBytes());
        int size = 0;
        StringBuilder sb = new StringBuilder();
        for (char c : vaildHex.toCharArray()) {
            size++;
            sb.append(c);
            if (size == 2) {
                size = 0;
                sb.append(" ");
            }
        }
        logger.info("{}", sb.toString());

        // -Dfile.encoding=GBK
        // 获取系统默认编码
//        logger.info("系统默认编码：" + System.getProperty("file.encoding"));//查询结果GBK
//        // 系统默认字符编码
//        logger.info("系统默认字符编码:" + Charset.defaultCharset()); //查询结果GBK
//        // 操作系统用户使用的语言
//        logger.info("系统默认语言:" + System.getProperty("user.language")); //查询结果zh
//        System.setProperty("file.encoding", "GBK");
//        System.setProperty("user.language", "en_US");
////        Charset.defaultCharset = null;
//        logger.info("修改后================================");
//        // 获取系统默认编码
//        logger.info("系统默认编码：" + System.getProperty("file.encoding"));//查询结果GBK
//        // 系统默认字符编码
//        logger.info("系统默认字符编码:" + Charset.defaultCharset()); //查询结果GBK
//        // 操作系统用户使用的语言
//        logger.info("系统默认语言:" + System.getProperty("user.language")); //查询结果zh

        String value = "你好";
        byte[] bytes1 = value.getBytes("GBK");
        byte[] bytes2 = value.getBytes("UTF-8");
        String newValue1 = new String(bytes1, "GBK");
        String newValue2 = new String(bytes2, "UTF-8");

        logger.info("value: {}\n" +
                        "newValue1: {}, newValue2: {}\n" +
                        "bytes1.len: {}, bytes2.len: {}\n " +
                        "bytes1: {}, bytes2: {}\n " +
                        "bytes1Hex: {}, bytes2Hex: {}"
                , value
                , newValue1, newValue2
                , bytes1.length, bytes2.length
                , bytes1, bytes2
                , ByteUtil.bytesToHexStringH(bytes1), ByteUtil.bytesToHexStringH(bytes2));

        byte[] bfs = ByteUtil.hexStringToBytes("BF");
        logger.info("len: {}, bfs: {}, 1111: {}, BF: {}"
                , bfs.length, bfs, Integer.valueOf("1111", 2)
                , ByteUtil.byteToBit(bfs[0])
        );
        // 1111是15，所以16进制的单个字符由4个bit组成，所以1个字节(byte)由8个bit组成，可以表示2个16进制的字符
        // 2个16进制的字符即1个字节
        logger.info("{}", Integer.valueOf("10000001", 2));
        logger.info("{}", Integer.valueOf("11001001", 2));
        logger.info("{}", Integer.valueOf("00100110", 2));
        logger.info("{}", Integer.valueOf("10110011", 2));

        logger.info("{}", Integer.valueOf("00C8", 16));
    }

    @Test
    public void loginTest() throws IOException {
        // jdk8语法糖自动释放
        try (SocketClient socketClient = SocketClient.newbuilder()
                .setIp("10.1.0.129")
                .setPort(2401)
                .build()) {
            ClientReceive clientReceive = new ClientReceive();
            StringBuilder loginRequest = new StringBuilder();
            loginRequest.append("BEGIN AUTH REQUEST")
                    .append(END)
                    .append("/home/cvsroot/BASS")
                    .append(END)
                    .append("chenqx")
                    .append(END)
                    .append("A*byuW_184oR")
                    .append(END)
                    .append("END AUTH REQUEST")
                    .append(END)
            ;
            socketClient.send(loginRequest.toString().getBytes());
            socketClient.receive(clientReceive);

            StringBuilder validRequest = new StringBuilder();
            validRequest.append("Valid-responses ok error Valid-requests Checked-in New-entry Checksum Copy-file Updated Created Update-existing Merged Patched Rcs-diff Update-baserev Mode Mod-time Removed Renamed Remove-entry Set-static-directory Clear-static-directory Set-sticky Clear-sticky Template Notified Module-expansion Wrapper-rcsOption Clear-rename Rename EntriesExtra M Mbinary E F MT NoTranslateBegin NoTranslateEnd")
                    .append(END)
                    .append("valid-requests")
                    .append(END)
            ;
            socketClient.send(validRequest.toString().getBytes());
            socketClient.receive(clientReceive);

            socketClient.send(("UseUnchanged" + END).getBytes());

            StringBuilder expand_modules = new StringBuilder();
            expand_modules.append("Root /home/cvsroot/BASS")
                    .append(END)
                    .append("Global_option -q")
                    .append(END)
                    .append("Global_option -Q")
                    .append(END)
                    .append("Global_option -n")
                    .append(END)
                    .append("expand-modules")
                    .append(END)
            ;
            socketClient.send(expand_modules.toString().getBytes());
            socketClient.receive(clientReceive);

            StringBuilder argument = new StringBuilder();
            argument.append("Argument -N")
                    .append(END)
                    .append("Argument -q")
                    .append(END)
                    .append("Argument --")
                    .append(END)
                    .append("Argument .")
                    .append(END)
                    .append("Directory .")
                    .append(END)
                    .append("/home/cvsroot/BASS")
                    .append(END)
                    .append("co")
                    .append(END)
            ;
            socketClient.send(argument.toString().getBytes());
            socketClient.receive(clientReceive);
        }

        String csn = Charset.defaultCharset().name();
        logger.info("csn: {}", csn);
    }

    class ClientReceive implements SocketClient.ReceiveCall {

        @Override
        public void read(InputStream in) throws IOException {
            int size = 0;
            while (size == 0) {
                size = in.available();
                if (size > 0) {
                    byte[] result = new byte[size];
                    in.read(result);
                    logger.info("{}", new String(result, "GBK"));
                }
                SleepUtil.sleepMilliSecond(10);
            }
        }
    }
}