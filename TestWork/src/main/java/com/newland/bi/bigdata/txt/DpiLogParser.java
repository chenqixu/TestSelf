package com.newland.bi.bigdata.txt;

import com.newland.bi.bigdata.changecode.ChangeCode;
import com.newland.bi.bigdata.log.LogBackUtil;
import com.newland.bi.bigdata.utils.SleepUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * DpiLogParser
 *
 * @author chenqixu
 */
public class DpiLogParser extends ChangeCode {
    private static Logger logger = LoggerFactory.getLogger(DpiLogParser.class);

    public static void main(String[] args) throws Exception {
        // 手工加载logback配置
//        LogBackUtil.init("D:\\Document\\Workspaces\\Git\\TestSelf\\TestWork\\src\\main\\resources\\logback.xml");
        String path = "d:\\Work\\实时\\DPI实时解析\\logs\\filesortlte.log";
        DpiLogParser dpiLogParser = new DpiLogParser();
        dpiLogParser.run(path);
    }

    public void run(String scanpath) {
        setRead_code("UTF-8");
        List<String> javalist = read(scanpath);
        int tag = 0;
        String header = "";
        boolean strTag = true;
        String lastIp = "";
        for (String str : javalist) {
            if (str.trim().endsWith("########################")) {
                tag = 1;// 找到一个新的
                header = str.trim();
            } else if (tag == 1) {
                // 第一个是ip，第二个是未处理个数
                if (strTag) {
                    lastIp = str.trim();
                    strTag = false;
                } else {
                    String value = str.trim();
                    int untreatedNum = 0;
                    try {
                        untreatedNum = Integer.valueOf(value);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    if (untreatedNum > 1000 && !header.contains("Tue Jun 11"))
                        logger.info(header + " " + lastIp + " " + untreatedNum);
                    strTag = true;
                }
            }
        }
    }

}
