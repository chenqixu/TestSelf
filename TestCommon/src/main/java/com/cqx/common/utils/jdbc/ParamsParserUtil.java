package com.cqx.common.utils.jdbc;

import com.cqx.common.utils.ftp.FtpBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数解析
 *
 * @author chenqixu
 */
public class ParamsParserUtil {
    public static final String DBBEANS = "dbbeans";
    public static final String FTPBEANS = "ftpbeans";
    private static Logger logger = LoggerFactory.getLogger(ParamsParserUtil.class);
    private Map param;
    private Map<String, DBBean> beanMap = new HashMap<>();
    private List<DBBean> dbBeanList;
    private Map<String, FtpBean> ftpBeanMap = new HashMap<>();
    private List<FtpBean> ftpBeanList;

    public ParamsParserUtil(Map param) {
        logger.info("param：{}", param);
        this.param = param;
        init();
    }

    /**
     * 初始化
     */
    public void init() {
        // 解析dbbeans
        Object dbBeansObj = param.get(DBBEANS);
        if (dbBeansObj != null) {
            dbBeanList = DBBean.parser(dbBeansObj);
            // dbbeans映射进map
            beanMap = new HashMap<>();
            for (DBBean dbBean : dbBeanList) {
                beanMap.put(dbBean.getName(), dbBean);
            }
        }
        // 解析ftpbeans
        Object ftpBeanObj = param.get(FTPBEANS);
        if (ftpBeanObj != null) {
            ftpBeanList = FtpBean.parser(ftpBeanObj);
            // ftpbeans映射进map
            ftpBeanMap = new HashMap<>();
            for (FtpBean ftpBean : ftpBeanList) {
                ftpBeanMap.put(ftpBean.getName(), ftpBean);
            }
        }
    }

    public Map<String, DBBean> getBeanMap() {
        return beanMap;
    }

    public Map<String, FtpBean> getFtpBeanMap() {
        return ftpBeanMap;
    }
}
