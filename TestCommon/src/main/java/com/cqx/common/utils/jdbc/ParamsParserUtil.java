package com.cqx.common.utils.jdbc;

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
    private static Logger logger = LoggerFactory.getLogger(ParamsParserUtil.class);
    private Map param;
    private Map<String, DBBean> beanMap = new HashMap<>();
    private List<DBBean> dbBeanList;

    public ParamsParserUtil(Map param) {
        logger.info("param：{}", param);
        this.param = param;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        // 解析dbbeans
        dbBeanList = DBBean.parser(param.get(DBBEANS));
        // dbbeans映射进map
        beanMap = new HashMap<>();
        for (DBBean dbBean : dbBeanList) {
            beanMap.put(dbBean.getName(), dbBean);
        }
    }

    public Map<String, DBBean> getBeanMap() {
        return beanMap;
    }
}
