package com.cqx.common.utils.jdbc;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.Test;

public class DBFormatUtilTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(DBFormatUtilTest.class);

    @Test
    public void to_char() {
        logger.info("{}", DBFormatUtil.to_char("last_login_time", DBFormatEnum.YYYYMMDDHH24MISS));
        logger.info("{}", DBFormatUtil.to_char("last_login_time", "create_time", DBFormatEnum.YYYYMMDDHH24MISS));
        logger.info("{}", DBFormatUtil.to_date("last_login_time", DBFormatEnum.YYYYMMDDHH24MISS));
        logger.info("{}", DBFormatUtil.to_date("last_login_time", "create_time", DBFormatEnum.YYYYMMDDHH24MISS));
        logger.info("{}", DBFormatUtil.to_char(DBFormatUtil.sysdate(), DBFormatEnum.YYYYMM));
        logger.info("{}", DBFormatUtil.sysdate());
        logger.info("{}", DBFormatUtil.to_number("123"));
        logger.info("{}", DBFormatUtil.to_number(DBFormatUtil.to_char(DBFormatUtil.sysdate(), DBFormatEnum.YYYYMM)));
    }
}