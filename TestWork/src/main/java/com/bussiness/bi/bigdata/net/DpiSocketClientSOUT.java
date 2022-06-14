package com.bussiness.bi.bigdata.net;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * socket客户端 System.out.println 输出版本
 *
 * @author chenqixu
 */
public class DpiSocketClientSOUT {

    private static final String LANG = "utf-8";
    private Socket client;
    private int server_port;
    private String server_ip;
    private BufferedReader br = null;
    private PrintWriter pw = null;
    private TimeCostUtil timeCostUtil = new TimeCostUtil(true);

    public DpiSocketClientSOUT() {
    }

    public DpiSocketClientSOUT(String server_ip, int server_port) {
        this.server_ip = server_ip;
        this.server_port = server_port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("no enough args!");
            System.exit(0);
        }
        String fileName = args[0];
        String ip = args[1];
        int port = Integer.valueOf(args[2]);
        System.out.println(String.format("fileName：%s，ip：%s，port：%s", fileName, ip, port));
        DpiSocketClientSOUT dpiSocketClient = new DpiSocketClientSOUT(ip, port);
        dpiSocketClient.connect();

        TimeCostUtil timeCostUtil = new DpiSocketClientSOUT().new TimeCostUtil();
        timeCostUtil.start();
//        String content = "8613900000000|Dalvik/2.1.0 (Linux; U; Android 6.0.1; vivo X9Plus Build/MMB29M)|/mmsns/BrVA8rJQ5YiaE3jsNCbIlvWuxZqwy7iceOyo4hmLlZcPhIQdGr41EoEmXPVFTzzeIr8xSa3mlvGjM/150?tp=wxpc&token=WSEN6qDsKwV8A02w3onOGQYfxnkibdqSOkmHhZGNB4DFBVuxiac0cLTBOPNlRfmSzxb4E8u5TPZzWfCdpkAiaX3Ug&idx=1|shmmsns.qpic.cn|117.172.5.42|80|1|9|1198.0|5086.0|image/wxpc";
//        String content = "4119|0595|11|0398000620521433|460005050115438|869762032028453|13515053736|D806C801|1|10.41.176.91|100.98.46.148|100.77.101.122|2152|2152|5955|689A103|||6|CMNET||103|-4611686018086517239|1554940916089|185|1554940916274|1|9||0||53564|120.198.201.185|80|460|0|1069|523|69775|43781|||0|0|5|5|||3|0|43781|0|0|0|0|0|0|26|79|11|43781|82432|1400|1|0|1|szminorshort.weixin.qq.com|szminorshort.weixin.qq.com/mmtls/75d67e73||MicroMessenger Client|application/octet-stream|||212|0||||||||||||||2019|04|11|08|01||||||||||5|200|3|43781|43781|69775|0|0|159430733|1973||0|50333621|243019576|1|0|0|||||||";
        String allName = "length|city_1|interface|xdr_id|imsi|imei|msisdn|m_tmsi|ip_add_type|user_ip|sgw_ip_add|enodeb_ip_add|sgw_port|enodeb_port|tac|eci|other_tac|other_eci|rat|apn|sid|app_type_code|procedure_id|procedure_start_time|delay_time|procedure_end_time|app_class_top|app_class|ownclass|l4_protocol|busi_bear_type|source_port|server_ip|destination_port|mcc|mnc|upbytes|downbytes|dura|dura_1|upflow|downflow|updura|downdura|up_packet|down_packet|up_packet_flow|down_packet_flow|busi_behavior_identify|busi_complete_identify|busi_dura|ul_tcp_disordered_packets|dl_tcp_disordered_packets|ul_tcp_retransmission_packets|dl_tcp_retransmission_packets|ul_ip_frag_packets|dl_ip_frag_packets|tcp_built_delay|tcp_confirm_delay|first_tcp_success_delay|first_answer_delay|window_size|mss_size|tcp_attempts_cnt|tcp_connection_status|session_end_flag|host|uri|x_online_host|user_agent|http_content_type|refer_uri|cookie|content_length|target_action|wtp_disruption_type|wtp_disruption_causes|title|keyword|get|post|success|e100|e300|e401|area|city|areaclass|s_year|s_month|s_day|s_hour|s_minute|telnumber|imei_prefix8|terminaltype|mobilevendor|mobiletype|mobileos|sys_reported_time|p_id|page_id|object_type|object_status|http_versions|first_http_answer_delay|last_http_answer_delay|last_ack_answer_delay|browsing_tool|portal_app_collections|mmeues1apid|enbues1apid|location|first_request|enb_sgsn_gtp_teid|sgw_ggsn_gtp_teid|protocol_type|app_content|app_status|user_ipv6|app_server_ipv6|reserve_1|reserve_2|reserve_3|demand_1|demand_2";
        String orderName = "msisdn|app_class_top|app_class|unknow|eci|imei|tac|procedure_start_time|procedure_end_time|server_ip|destination_port|user_agent|uri|host|http_content_type|upbytes|downbytes|city_1|imsi|delay_time|ownclass|busi_bear_type|mcc|refer_uri|app_content|unknow|unknow|target_action|upflow|downflow";
        String split = "\\|";
        String concat = "|";
        List<String> listContent = readFile(fileName, "UTF-8", 0);
        for (String content : listContent) {
            String _content = parserContent(content, allName, orderName, split, concat);
            try {
                String reviceMsg = dpiSocketClient.sendMsg(_content);
            } catch (SocketException e) {
                System.out.println("catch SocketException");
            }
        }
        timeCostUtil.stop();
        dpiSocketClient.disconnect();
        System.out.println(String.format("file size：%s，all cost：%s，sendMsg cost：%s",
                listContent.size(), timeCostUtil.getCost(), dpiSocketClient.getIncrementCost()));
    }

    public static String parserContent(String content, String allName, String orderName, String split, String concat) throws UnsupportedOperationException {
        StringBuffer sb = new StringBuffer();
        if (content != null && content.length() > 0) {
            String[] arrContent = content.split(split, -1);
            String[] arrAllName = allName.split(split, -1);
            if (arrContent.length != arrAllName.length)
                throw new UnsupportedOperationException("内容和解析字段不匹配！内容长度：" +
                        arrContent.length + "，解析字段长度：" + arrAllName.length);
            String[] arrOrderName = orderName.split(split, -1);
            Map<String, String> mapContent = new HashMap<>();
            for (int i = 0; i < arrContent.length; i++) {
                mapContent.put(arrAllName[i], arrContent[i]);
            }
            for (int i = 0; i < arrOrderName.length; i++) {
                String result = mapContent.get(arrOrderName[i]);
                sb.append(result == null ? "" : result);
                if ((i + 1) < arrOrderName.length)
                    sb.append(concat);
            }
        }
        return sb.toString();
    }

    public static List<String> readFile(String fileName, String read_code, int limit) {
        List<String> resultlist = new ArrayList<>();
        BufferedReader reader = null;
        try {
            File readFile = new File(fileName);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(readFile), read_code));
            String _tmp;
            int count = 0;
            while ((_tmp = reader.readLine()) != null) {
                resultlist.add(_tmp);
                count++;
                if (limit > 0 && count >= limit) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultlist;
    }

    /**
     * 压抑异常的sleep
     *
     * @param timeout 几豪秒
     */
    public static void sleepMilliSecond(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 连接服务端
     */
    public void connect() throws IOException {
        try {
            client = new Socket(server_ip, server_port);
            br = new BufferedReader(new InputStreamReader(client.getInputStream(),
                    Charset.forName(LANG)));
            pw = new PrintWriter(client.getOutputStream(), true);
            System.out.println(timeCostUtil.getNow("yyyy-MM-dd HH:mm:ss.SSS") + " connect：" + client);
        } catch (IOException e) {
            System.out.println("无法连接端口：" + server_port + "，" + e.getMessage());
            throw new IOException("无法连接端口：" + server_port + "，" + e.getMessage());
        }
    }

    /**
     * 校验
     */
    private void check() throws IOException {
        if (client == null) throw new IOException("client is null ! please connect first !");
        if (br == null) throw new IOException("br is null ! please connect first !");
        if (pw == null) throw new IOException("pw is null ! please connect first !");
    }

    /**
     * 发送、接收数据
     *
     * @param data send data
     */
    public String sendMsg(String data) throws IOException {
        try {
            check();
            timeCostUtil.start();
            pw.println(data);
            String content = br.readLine();
            timeCostUtil.stopAndIncrementCost();
//            logger.debug("client：{}，send：{}，receive：{}", client, data, content);
//            System.out.println("client：" + client + "，send：" + data + "，receive：" + content);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
            // 尝试重连
            retryConnect(10);
            // 重连后检查
            try {
                check();
            } catch (IOException e1) {
                e1.printStackTrace();
                throw e1;
            }
        }
        return null;
    }

    /**
     * 退出服务端
     */
    public void disconnect() {
        System.out.println(timeCostUtil.getNow("yyyy-MM-dd HH:mm:ss.SSS") + " disconnect，br：" + br + "，pw：" + pw + "，client：" + client);
        if (br != null) {
            try {
                br.close();
                br = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (pw != null) {
            pw.close();
            pw = null;
        }
        if (client != null) {
            try {
                client.close();
                client = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void retryConnect(int count) {
        if (count > 0) {
            try {
                disconnect();
                connect();
                check();
            } catch (IOException e) {
                sleepMilliSecond(500);
                count--;
                retryConnect(count);
            }
        }
    }

    public void resetIncrementCost() {
        timeCostUtil.resetIncrementCost();
    }

    public long getIncrementCost() {
        return timeCostUtil.getIncrementCost();
    }

    public class TimeCostUtil {
        long start;
        long end;
        long incrementCost = 0;
        boolean isNanoTime = false;
        long lastCheckTime = getCurrentTime();

        public TimeCostUtil() {
        }

        public TimeCostUtil(boolean isNanoTime) {
            this.isNanoTime = isNanoTime;
        }

        public String getNow(String format) {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(now);
        }

        private long getCurrentTime() {
            if (isNanoTime) {
                return System.nanoTime();//纳秒
            } else {
                return System.currentTimeMillis();
            }
        }

        public void start() {
            start = getCurrentTime();
        }

        public void stop() {
            end = getCurrentTime();
        }

        public boolean tag(long limitTime) {
            if (getCurrentTime() - lastCheckTime > limitTime) {
                lastCheckTime = getCurrentTime();
                return true;
            }
            return false;
        }

        /**
         * 花费的时间
         *
         * @return
         */
        public long getCost() {
            if (start == 0) return 0;
            return end - start;
        }

        public long stopAndGet() {
            stop();
            return getCost();
        }

        public String getStart() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.format(new Date(start));
        }

        public String getEnd() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return sdf.format(new Date(end));
        }

        public void stopAndIncrementCost() {
            stop();
            incrementCost += getCost();
        }

        public long getIncrementCost() {
            if (isNanoTime)
                return incrementCost / 1000000;
            else
                return incrementCost;
        }

        public void resetIncrementCost() {
            incrementCost = 0;
        }
    }
}
