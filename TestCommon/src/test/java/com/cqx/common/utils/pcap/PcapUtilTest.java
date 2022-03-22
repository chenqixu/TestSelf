package com.cqx.common.utils.pcap;

import com.cqx.common.utils.cmd.LogDealInf;
import com.cqx.common.utils.cmd.ProcessBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PcapUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(PcapUtilTest.class);
    private String path = "d:\\tmp\\data\\xdr\\";
    private String fileName = "";
    private PcapUtil pcapUtil;

    @Before
    public void setUp() throws Exception {
        pcapUtil = new PcapUtil();
    }

    @Test
    public void callParser() throws Exception {
        // 有通话
        fileName = "VoLTE_ici_8582644071769099_1588582644067395557_8618060318859_8613706030111_20220224_094416_012022_11_1588580346752508940.cap";
        String result = pcapUtil.parserSIP(path + fileName);
        logger.info("callParser：{}", result);
    }

    @Test
    public void hangUpParser() throws Exception {
        // 挂机
        fileName = "VoLTE_ici_8582644071373960_1588582644067401578_8651180874284_8618965191816_20220224_094416_012022_11_1588580346752507017.cap";
        String result = pcapUtil.parserSIP(path + fileName);
        logger.info("hangUpParser：{}", result);
    }

    @Test
    public void my_trafficParser() throws Exception {
        // my_traffic
        fileName = "my_traffic.pcap";
        String result = pcapUtil.parserSIP(path + fileName);
        logger.info("my_trafficParser：{}", result);
    }

    @Test
    public void stringTest() {
        String a = "10.1.8.203:8080;";
        List<Byte> byteList = new ArrayList<>();
        for (byte b : a.getBytes()) {
            byteList.add(b);
        }
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        logger.info("{}", new String(bytes));
    }

    @Test
    public void parserRXInterface() throws IOException {
        fileName = "VoLTE_rx_1520553769498984665_875589044738617344_8615959492433_0_20220315_150912_011012_38_875586638558343461.cap";
        String result = pcapUtil.parserDiameter(path + fileName);
    }

    @Test
    public void parserGXInterface() throws IOException {
        fileName = "VoLTE_gx_293402035875760793_1736058048541266324_8613950083659_0_20220315_150834_011024_23_1736055642362853132.cap";
        String result = pcapUtil.parserDiameter(path + fileName);
    }

    @Test
    public void parserCXInterface() throws IOException {
        fileName = "VoLTE_cx_1520835244435892029_3756203956355670080_8618350610576_0_20220315_145846_011052_32_3756201550215192709.cap";
        String result = pcapUtil.parserDiameter(path + fileName);
    }

    @Test
    public void parserSHInterface() throws IOException {
        fileName = "VoLTE_sh_153060371665203518_3683864887337663402_8618350076069_0_20220315_145818_011051_31_3683862481200601229.cap";
        String result = pcapUtil.parserDiameter(path + fileName);
    }

    @Test
    public void parserZHInterface() throws IOException {
        fileName = "VoLTE_zh_301661567184062570_3756203956355458672_8613799720280_0_20220315_145842_011052_32_3756201550215243625.cap";
        String result = pcapUtil.parserDiameter(path + fileName);
    }

    @Test
    public void flow() throws IOException {
        fileName = "VoLTE_ici_8582644071372977_1588582644068691561_8613306965800_8615759638470_20220224_094416_012022_11_1588580346752506034.cap";
        StringBuilder code = new StringBuilder();
        System.setProperty("file.encoding", "UTF-8");
        ProcessBuilderFactory processBuilderFactory = new ProcessBuilderFactory(false);
//        processBuilderFactory.execCmd("tshark", "-r", path + fileName);
        processBuilderFactory.execCmdNoWait(new LogDealInf() {
            @Override
            public void logDeal(String logMsg) {
                String[] logArr = logMsg.split(" ", -1);
                AtomicBoolean a1 = new AtomicBoolean(true);
                AtomicBoolean a2 = new AtomicBoolean(true);
                AtomicBoolean a3 = new AtomicBoolean(true);
                AtomicBoolean a4 = new AtomicBoolean(true);
                AtomicBoolean a5 = new AtomicBoolean(true);
                String s1 = null;
                String s2 = null;
                String s3 = "";
                String s4 = "";
                String s5 = "";
                StringBuilder s6 = new StringBuilder();
                // 找到值
                for (String tmp : logArr) {
                    if (tmp.length() > 0) {
                        if (a1.getAndSet(false)) {
                            s1 = tmp;
                        } else if (a2.getAndSet(false)) {
                            s2 = tmp;
                        } else if (a3.getAndSet(false)) {
                            s3 = tmp;
                        } else if (a4.getAndSet(false)) {
                            s4 = tmp;
                        } else if (a5.getAndSet(false)) {
                            s5 = tmp;
                        } else {
                            s6.append(tmp);
                        }
                    }
                }
                String format = "%s%s%s: %s";
                String result = String.format(format
                        , s3.replaceAll(":", ";")
                        , s4.equals("→") ? "->" : "<-"
                        , s5.replaceAll(":", ";")
                        , s6);
                if (!(s6.toString().contains("fragment"))) {
                    code.append(result)
                            .append("\n");
                }
//                logger.info("result：{}，s1：{}，s2：{}，s3：{}，s4：{}，s5：{}，s6：{}", result, s1, s2, s3, s4, s5, s6.toString());
            }
        }, "tshark", "-r", path + fileName);
        logger.info("Title: {}\n{}", fileName, code.toString());
//        logger.info("{}", processBuilderFactory.getSuccess_sb());
//        processBuilderFactory.execCmd("tshark.exe", "-V", "-r", path + fileName);
    }

    @Test
    public void frame() {
        fileName = "VoLTE_ici_8582644071372977_1588582644068691561_8613306965800_8615759638470_20220224_094416_012022_11_1588580346752506034.cap";
        List<StringBuilder> infoList = new ArrayList<>();
        System.setProperty("file.encoding", "GBK");
        ProcessBuilderFactory processBuilderFactory = new ProcessBuilderFactory(false);
        processBuilderFactory.execCmdNoWait(new LogDealInf() {
            @Override
            public void logDeal(String logMsg) {
                if (logMsg.startsWith("Frame")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(logMsg).append("\n");
                    infoList.add(sb);
                } else {
                    if (infoList.size() > 0) {
                        StringBuilder sb = infoList.get(infoList.size() - 1);
                        sb.append(logMsg).append("\n");
                    }
                }
            }
        }, "tshark", "-V", "-r", path + fileName);
        AtomicInteger count = new AtomicInteger(1);
        for (StringBuilder sb : infoList) {
            logger.info("{} {}", count.getAndIncrement(), sb);
        }
    }
}