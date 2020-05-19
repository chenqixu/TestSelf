package com.cqx.sync;

import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ParserSQLFileTest {

    private ParserSQLFile parserSQLFile;
    private String file_name;

    @Before
    public void setUp() throws Exception {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.ORACLE);
        dbBean.setTns("jdbc:oracle:thin:@10.1.8.205:1521:cbass");
        dbBean.setUser_name("xdcdr");
        dbBean.setPass_word("xdcdr");
        dbBean.setPool(false);

//        file_name = "d:\\Work\\ETL\\OGG\\data\\sss1.sql";
        file_name = "d:\\Work\\ETL\\OGG\\data\\broadband_reservation.sql";

        parserSQLFile = new ParserSQLFile(dbBean, file_name);
    }

    @After
    public void tearDown() throws Exception {
        if (parserSQLFile != null) parserSQLFile.release();
    }

    @Test
    public void readAndExec() throws Exception {
        //USERS
//        parserSQLFile.readAndExec("HOME_CITY, USER_ID, NETWORK_TYPE, CUSTOMER_ID, TYPE, SERVICE_TYPE, MSISDN, IMSI, USER_BRAND, HOME_COUNTY, CREATOR, CREATE_TIME, CREATE_SITE, SERVICE_STATUS, PASSWORD, TRANSFER_TIME, STOP_TIME, MODIFY_ID, MODIFY_SITE, MODIFY_TIME, MODIFY_CONTENT, RC_SN, RC_EXPIRE_TIME, ORDER_SEQ, BROKER_ID, HISTORY_SEQ, LOCK_FLAG, BILL_TYPE, BILL_CREDIT, BILL_TIME, EXPIRE_TIME, ARCHIVES_CREATE_TIME, PASSWORD_GET_TYPE, PASSWORD_GET_TIME, PASSWORD_RESET_TIME, SUB_TYPE",
//                "USERS");
        //CQX_CSS_BROADBAND_RESERVATION
        parserSQLFile.readAndExec("HOME_CITY, HOME_COUNTY, CONTACT_NAME, CONTACT_TEL, ORDER_STATE, MANAGER_NAME, CREATE_TIME, COMMUNITY_CODE, ADDRESS, OPTIONAL_DEAL_ID, LINE_COVERED, SMS_SEND_TIME, MODIFY_OPERATOR, MODIFY_TIME, REMARK, MANAGER_TEL, CONFIRM_SERIAL, MSISDN, COMMUNITY_NAME, REQUEST_SOURCE, CREATE_OPERATOR, CREATE_OPERATOR_NAME, CREATE_ORGAN_NAME, CREATE_ORGAN_ID, USER_ID, FINISH_TIME, IS_SUCC, CNT_EFFECT_TIME, RESOURCE_REMARK, ORDER_REMARK, RESOURCE_MODIFY_TIME, ORDER_MODIFY_TIME, SALE_ID, SALE_NAME, PRODUCT_ATTR, PARAM, BROADBAND_USER_ID, OPEN_ID, OPEN_TIME, ACCEPT_REASON_CODE, TELE_INFO, LINE_STATE, EQUIPMENT_OUT_TYPE, USER_SOURCE, RECOMMEND_ORDER_ID, RECOMMEND_ORG_ID, RECOMMEND_MSISDN, IS_ORDER, RES_CHECK_RESULT, SEARCH_TYPE, ACCEPT_ORDER_STATE, SEARCH_OPERATION_ID, PD_CONTACT_TEL, PD_CONTACT_NAME, ACCEPT_REMARK, SEARCH_TIME, SEARCH_ORGAN_ID, SEARCH_OPERATOR_ID, ORDER_NUMBER, SUB_ORDER_NUMBER",
                "CQX_CSS_BROADBAND_RESERVATION");
    }
}