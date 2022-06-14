package com.bussiness.bi.bigdata.component;

import com.cqx.common.utils.ftp.FileInfo;
import com.cqx.common.utils.ftp.FtpParamCfg;
import com.cqx.common.utils.sftp.SftpConnection;
import com.cqx.common.utils.sftp.SftpFileFilter;
import com.cqx.common.utils.sftp.SftpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Ftp采集，验证编码，并验证容错率
 *
 * @author chenqixu
 */
public class FtpAndCheck {

    private static final Logger logger = LoggerFactory.getLogger(FtpAndCheck.class);
    private FtpParamCfg ftpParamCfg;
    private Map<String, Map<String, List<FileInfo>>> fileMaps;
    private SftpConnection sftpConnection;

    public FtpAndCheck(FtpParamCfg ftpParamCfg) {
        this.ftpParamCfg = ftpParamCfg;
        this.sftpConnection = SftpUtil.getSftpConnection(this.ftpParamCfg);
    }

    public void splitPath(String path, String dataSourceFileRegex, String save_path) {
        FtpAndCheckBean ftpAndCheckBean = ruleTool(dataSourceFileRegex);
        SftpFileFilter sftpFileFilter = new SftpFileFilter(ftpAndCheckBean.getNew_rule());
        fileMaps = new HashMap<>();
        String[] remoteFilePaths = path.split(",", -1);
        //扫描
        for (String remoteFilePath : remoteFilePaths) {
            listFile(remoteFilePath, ftpAndCheckBean, sftpFileFilter);
        }
        //挑第一个周期
        boolean selected = false;
        List<FileInfo> selectedList = null;
        for (Map.Entry<String, Map<String, List<FileInfo>>> entrys : fileMaps.entrySet()) {
            String file_path = entrys.getKey();
            Map<String, List<FileInfo>> cycle_map = entrys.getValue();
            for (Map.Entry<String, List<FileInfo>> entry : cycle_map.entrySet()) {
                String key = entry.getKey();
                List<FileInfo> value = entry.getValue();
                int cycle_size = value.size();
                logger.info("file_path：{}，cycle：{}，cycle_size：{}", file_path, key, cycle_size);
                if (cycle_size > 0) {
                    selected = true;
                    selectedList = value;
                    break;
                }
            }
            if (selected) break;
        }
        //打印挑选的列表，并下载
        if (selected) {
            logger.info("{} 有文件，文件个数：{}", path, selectedList.size());
            for (FileInfo fileInfo : selectedList) {
                logger.info("fileInfo：{}", fileInfo);
                try {
                    SftpUtil.ftpFileDownload(sftpConnection, fileInfo.getSource_path(), fileInfo.getFile_name(), save_path);
                } catch (IOException e) {
                    logger.error("文件无法下载，原因：" + e.getMessage(), e);
                }
            }
        } else {
            logger.warn("{} 没有文件", path);
        }
    }

    public void close() {
        SftpUtil.closeSftpConnection(sftpConnection);
    }

    public void listFile(String remoteFilePath, FtpAndCheckBean ftpAndCheckBean, SftpFileFilter sftpFileFilter) {
        //按路径分组，再按周期分组，取出个数最多的
        Map<String, List<FileInfo>> fileInfoMap = new HashMap<>();
        fileMaps.put(remoteFilePath, fileInfoMap);//路径，文件按周期分组的Map
        List<FileInfo> fileInfoList = new ArrayList<>();
        SftpUtil.listFtpFiles(fileInfoList, sftpConnection, remoteFilePath, sftpFileFilter);
        for (FileInfo fileInfo : fileInfoList) {
            //获取周期，put到Map
            String cycle = fileInfo.getFile_name().substring(ftpAndCheckBean.getDate_begin(), ftpAndCheckBean.getDate_end());
            fileInfo.setFile_cycle(cycle);
            logger.debug("fileInfo：{}，cycle：{}", fileInfo, cycle);
            List<FileInfo> fileInfos = fileInfoMap.get(cycle);
            if (fileInfos == null) {
                fileInfos = new ArrayList<>();
                fileInfoMap.put(cycle, fileInfos);
            }
            fileInfos.add(fileInfo);
        }
    }

    public FtpAndCheckBean ruleTool(String rule_value) {
        String old_rule = rule_value;
        FtpAndCheckBean ftpAndCheckBean = new FtpAndCheckBean();
        ftpAndCheckBean.setOld_rule(old_rule);
        List<Rule> rules = new ArrayList<>();
        TimeRule timeRule = new TimeRule(ftpAndCheckBean);
        rules.add(new SpotRule());
        rules.add(timeRule);
        rules.add(new ZeroRule());
        rules.add(new CharacterRule());
        for (Rule rule : rules) {
            rule_value = rule.replace(rule_value);
        }
        ftpAndCheckBean = timeRule.getFtpAndCheckBean();
        ftpAndCheckBean.setNew_rule(rule_value);
        logger.info("ruleTool：{}", ftpAndCheckBean);
        return ftpAndCheckBean;
    }

    public boolean matches(String file_name, String rule) {
        FtpAndCheckBean ftpAndCheckBean = ruleTool(rule);
        String pattern_rule = ftpAndCheckBean.getNew_rule();
        boolean result = Pattern.matches(pattern_rule, file_name);
        logger.info("file_name：{}，rule：{}，pattern_rule：{}，matches：{}", file_name, rule, pattern_rule, result);
        return result;
    }

    interface Rule {
        String replace(String value);
    }

    class FtpAndCheckBean {
        int date_begin;
        int date_end;
        String old_rule;
        String new_rule;

        public String toString() {
            return "date_begin：" + date_begin + "，date_end：" + date_end + "，old_rule：" + old_rule + "，new_rule：" + new_rule;
        }

        public int getDate_begin() {
            return date_begin;
        }

        public void setDate_begin(int date_begin) {
            this.date_begin = date_begin;
        }

        public int getDate_end() {
            return date_end;
        }

        public void setDate_end(int date_end) {
            this.date_end = date_end;
        }

        public String getOld_rule() {
            return old_rule;
        }

        public void setOld_rule(String old_rule) {
            this.old_rule = old_rule;
        }

        public String getNew_rule() {
            return new_rule;
        }

        public void setNew_rule(String new_rule) {
            this.new_rule = new_rule;
        }
    }

    /**
     * 规则：%开头，然后两位数字，然后两位字母<br>
     * 比如：把%00DD替换成[0-9]{8}
     */
    class TimeRule implements Rule {
        FtpAndCheckBean ftpAndCheckBean;

        TimeRule(FtpAndCheckBean ftpAndCheckBean) {
            this.ftpAndCheckBean = ftpAndCheckBean;
        }

        @Override
        public String replace(String value) {
            //定位到%
            int index = value.indexOf("%");
            //使用旧规则定位到%
            int old_index = ftpAndCheckBean.getOld_rule().indexOf("%");
            if (index >= 0) {
                String front = value.substring(0, index);
                String behind = value.substring(index + 5);
                String timeRuleStr = value.substring(index, index + 5);
                String replaceStr = "[0-9]{%s}";
                int len = 8;//默认8，天
                //获取最后一位：Y，M，D，H
                String time = String.valueOf(timeRuleStr.charAt(4)).toUpperCase();
                switch (time) {
                    case "Y"://4位
                        len = 4;
                        break;
                    case "M"://6位
                        len = 6;
                        break;
                    case "D"://8位
                        len = 8;
                        break;
                    case "H"://10位
                        len = 10;
                        break;
                    default:
                        len = 8;
                        break;
                }
                ftpAndCheckBean.setDate_begin(old_index);
                ftpAndCheckBean.setDate_end(old_index + len);
                replaceStr = String.format(replaceStr, len);
                value = front + replaceStr + behind;
                logger.info("timeRuleStr：{}，{}，front：{}，behind：{}，value：{}", timeRuleStr, time, front, behind, value);
            }
            return value;
        }

        public FtpAndCheckBean getFtpAndCheckBean() {
            return ftpAndCheckBean;
        }
    }

    /**
     * 规则：$开头，然后1位数字<br>
     * 比如：把$6替换成0{6}
     */
    class ZeroRule implements Rule {
        @Override
        public String replace(String value) {
            //定位到$
            int index = value.indexOf("$");
            if (index >= 0) {
                String front = value.substring(0, index);
                String behind = value.substring(index + 2);
                String zeroRuleStr = value.substring(index, index + 2);
                //获取最后一位：数字
                String num = String.valueOf(zeroRuleStr.charAt(1)).toUpperCase();
                String replaceStr = "0{%s}";
                replaceStr = String.format(replaceStr, num);
                value = front + replaceStr + behind;
                logger.info("zeroRuleStr：{}，{}，front：{}，behind：{}，value：{}", zeroRuleStr, num, front, behind, value);
            }
            return value;
        }
    }

    /**
     * 规则：?，匹配任意单字符<br>
     * 比如：把?替换成.
     */
    class CharacterRule implements Rule {
        @Override
        public String replace(String value) {
            //替换?成.
            int index = value.indexOf("?");
            if (index >= 0) {
                value = value.replaceAll("\\?", ".");
                logger.info("CharacterRuleStr：{}", value);
            }
            return value;
        }
    }

    class SpotRule implements Rule {
        @Override
        public String replace(String value) {
            int index = value.indexOf(".");
            if (index >= 0) {
                value = value.replace(".", "\\.");
                logger.info("SpotRule：{}", value);
            }
            return value;
        }
    }
}
