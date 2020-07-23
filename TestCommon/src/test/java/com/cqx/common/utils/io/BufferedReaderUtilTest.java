package com.cqx.common.utils.io;

import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BufferedReaderUtilTest {

    private List<FtpLog> ftpList = new ArrayList<>();
//    private JDBCUtil jdbcUtil;

    @Before
    public void setUp() {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setPool(false);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.204:1521:orapri");
        dbBean.setUser_name("jutap");
        dbBean.setPass_word("jutap");
//        jdbcUtil = new JDBCUtil(dbBean);
    }

    @Test
    public void filenameTest() {
        String rule = "D_W_PHOME-R2\\.002_%00DD_1\\.1_01\\.dat";
        String filename = "D_W_PHOME-R2.002_20200628_1.1_01.dat";
        info(filename.substring(18, 26));
        info(filename.indexOf("20200628"));
        info(filename.substring(17, 25));
        info(rule.indexOf("%"));

        String value = "a&^$b&^$c";
        String[] value_arr = value.split("&\\^\\$", -1);
        for (String v : value_arr) info(v);
    }

    @Test
    public void readLine() throws IOException, SQLException {
        BufferedReaderUtil bufferedReaderUtil = new BufferedReaderUtil("d:\\Work\\割接\\可视化割接迁移\\data\\ftp.zip");
        String str;
        long cnt = 0L;
//        str = bufferedReaderUtil.readLineSimple();
        final String S1 = "Thread-";
        Map<String, FtpLog> listMap = new HashMap<>();
        while ((str = bufferedReaderUtil.readLine()) != null) {
            //读取一行
            //确认有没Thread-
            //把相同的Thread-放入一个List
            if (str.contains(S1)) {
                //按空格切割
                String[] value_arr = str.split(" ", -1);
                String key = value_arr[1];
                //去掉前4个
                if (value_arr.length > 4) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 4; i < value_arr.length; i++) sb.append(value_arr[i]);
                    str = sb.toString();

                    FtpLog ftpLog = listMap.get(key);
                    if (ftpLog == null) {
                        ftpLog = new FtpLog();
                        listMap.put(key, ftpLog);
                    }
                    if (str.contains("SpotRule")) ftpLog.SpotRule = str;
                    else if (str.contains("timeRuleStr")) ftpLog.timeRuleStr = str;
                    else if (str.contains("zeroRuleStr")) ftpLog.zeroRuleStr = str;
                    else if (str.contains("ruleTool")) {
                        ftpLog.ruleTool = str;
                        String v4 = value_arr[4];
                        if (v4 != null && v4.length() > 0) {
                            v4 = v4.replace("【", "");
                            String[] v4_arr = v4.split("】", -1);
                            if (v4_arr.length > 1) {
                                ftpLog.collectID = "【" + v4_arr[0] + "】";
                            }
                        }
                    } else if (str.contains("file_path")) ftpLog.cycleList.add(str);
                    else if (str.contains("有文件，文件个数")) ftpLog.selected = str;
                    else if (str.contains("fileInfo")) ftpLog.fileInfoList.add(str);
                    else if (str.contains("download，file")) ftpLog.downlaodList.add(ftpLog.collectID + str);
                }
            }
            cnt++;
        }
        info("cnt：%s", cnt);
        boolean next = bufferedReaderUtil.nextFile();
        info("nextFile：%s", next);
        info("listMap.size：%s", listMap.size());
//        FtpLog ftpLog = listMap.get("Thread-63");
////        info("Thread-63：%s", ftpLog);
        for (FtpLog ftpLog : listMap.values()) {
            ftpLog.printExcel();
        }
        ftpList.addAll(listMap.values());
        checkRead();
    }

    @Test
    public void checkRead() throws IOException, SQLException {
        BufferedReaderUtil bufferedReaderUtil = new BufferedReaderUtil("d:\\Work\\割接\\可视化割接迁移\\data\\check.zip");
        String str;
        while ((str = bufferedReaderUtil.readLine()) != null) {
            if (str.contains("容错率验证运行结果")) {
                String[] value_arr = str.split(" ", -1);
                String tmp = value_arr[4];
                String[] tmp_arr;
                boolean is936 = false;
                if (tmp.contains("二次容错率验证运行结果：")) {
                    tmp_arr = tmp.split("二次容错率验证运行结果：", -1);
                    is936 = true;
                } else {
                    tmp_arr = tmp.split("容错率验证运行结果：", -1);
                }
                String collectId = tmp_arr[0].replace("【", "").replace("】", "");
                String result = tmp_arr[1];
                info("collectId：%s，result：%s，is936：%s", collectId, result, is936);
                for (FtpLog ftpLog : ftpList) {
                    if (ftpLog.collectID.contains(collectId)) {
                        String sql;
                        if (is936) {
                            ftpLog.nextCheckResult = result;
                            sql = "update cqx_all_01 set two_check='" + result + "' where t1id=" + collectId;
                        } else {
                            ftpLog.checkResult = result;
                            sql = "update cqx_all_01 set one_check='" + result + "' where t1id=" + collectId;
                        }
                        info("checkRead sql：%s", sql);
//                        jdbcUtil.executeUpdate(sql);
                        break;
                    }
                }
            }
        }
//        for (FtpLog ftpLog : ftpList) ftpLog.printExcel();
    }

    private void info(String format, Object... arg) {
        System.out.println(String.format(format, arg));
    }

    private void info(Object arg) {
        info("%s", arg);
    }

    class FtpLog {
        String collectID;
        String SpotRule;
        String timeRuleStr;
        String zeroRuleStr;
        String ruleTool;
        List<String> cycleList = new ArrayList<>();
        String selected;
        List<String> fileInfoList = new ArrayList<>();
        List<String> downlaodList = new ArrayList<>();
        String checkResult;
        String nextCheckResult;

        public void println() {
            info(collectID);
            info(ruleTool);
            for (String s : cycleList) info(s);
            info(selected);
            for (String s : fileInfoList) info(s);
            for (String s : downlaodList) info(s);
        }

        public void printExcel() throws SQLException {
            String last_cycle = "";
            String cycle = "";
            String cycle_size = "";
            String cycle_file_size = "";
            String _collectID = collectID.replace("【", "").replace("】", "");
            if (cycleList.size() > 0) last_cycle = cycleList.get(cycleList.size() - 1);
            if (last_cycle.length() > 0) {
                last_cycle = last_cycle.replace(collectID, "");
                String[] cycle_arr = last_cycle.split("，", -1);
                cycle = cycle_arr[1].split("：", -1)[1];
                cycle_size = cycle_arr[2].split("：", -1)[1];
                cycle_file_size = cycle_arr[3].split("：", -1)[1];
                String sql = "update cqx_all_01 set downfile_cycle='" + cycle + "',downfile_cnt=" + cycle_size + ",downfile_size=" + cycle_file_size + " where t1id=" + _collectID;
                info("sql：%s", sql);
//                jdbcUtil.executeUpdate(sql);
            }
//            info("%s %s %s %s %s %s", _collectID, cycle, cycle_size, cycle_file_size, checkResult, nextCheckResult);
        }
    }
}