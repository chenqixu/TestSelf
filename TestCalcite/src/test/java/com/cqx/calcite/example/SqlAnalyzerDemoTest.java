package com.cqx.calcite.example;

import org.apache.calcite.sql.parser.SqlParseException;
import org.junit.Before;
import org.junit.Test;

public class SqlAnalyzerDemoTest {
    private SqlAnalyzerDemo sqlAnalyzerDemo;

    @Before
    public void setUp() {
        sqlAnalyzerDemo = new SqlAnalyzerDemo();
    }

    @Test
    public void analyzer() {
        // 先查询出sql
        // 再捞出表结构

        //融合
        String sql = "insert into nmc_app_nl_loc_mix_v1 " +
                "select case when eventid in (3,7) then CALLEDIMSI else CALLINGIMSI end," +
                "case when eventid in (3,7) then CALLEDIMEI else CALLINGIMEI end," +
                "case when eventid in (3,7) then CALLEDNUM else CALLINGNUM end," +
                "eventid,BTIME,LAC,CI,MSCCODE " +
                " from nmc_tb_mc_cdr " +
                "where EVENTID is not null and eventid in (1,3,6,7,8,12,13,14)" +
                "union all " +
                "select imsi,imei,msisdn,procedure_type,procedure_start_time,tac,cell_id,0 from nmc_etl_hw_lte_s1mme_v1 " +
                "where msisdn is not null " +
                "and lac is not null " +
                "and ci is not null " +
                "and code is not null ";
        sqlAnalyzerDemo.analyzer(sql);
    }

    @Test
    public void testFieldOrigin() {
        sqlAnalyzerDemo.testFieldOrigin(
//                "insert into emp(EMPNO,ENAME,JOB,MGR,HIREDATE,SAL,COMM,DEPTNO,SLACKER) " +
                "select t1.EMPNO as a1,case when t2.DEPTNO is not null then t2.NAME else t1.ENAME end as a2 from emp t1 left join dept t2 on t1.DEPTNO=t2.DEPTNO");
    }

    @Test
    public void analyzerV1() throws SqlParseException {
        String sql = "insert into ft_mid_user_join_daily\n"
                + " (sum_date,user_id,name_request_source)"
                + " select 20230301,t1.user_id,t3.request_source name_request_source "
                + " from ft_mid_user_join_user_2_tmp t1 "
                + " left join (select user_id,request_source from ft_mid_user_join_realname_tmp) t3 on t1.user_id = t3.user_id "
//                + " left join ft_mid_user_join_channl_02_tmp t4 on t1.msisdn=t4.user_number "
                + "";

//                "  (sum_date --统计日期\n" +
//                "  ,\n" +
//                "   user_id --用户编码\n" +
//                "  ,\n" +
//                "   join_create_id --渠道入网工号\n" +
//                "  ,\n" +
//                "   name_create_id --实名工号\n" +
//                "  ,\n" +
//                "   name_org_id --实名渠道\n" +
//                "  ,\n" +
//                "   name_record_time --实名制时间 v1.1\n" +
//                "  ,\n" +
//                "   name_request_source --实名请求来源 v1.1\n" +
//                "  ,\n" +
//                "   join_request_source --入网请求来源 v1.1\n" +
//                "  ,\n" +
//                "   is_sqxz --统计日是否发送申请携转短信 v1.3\n" +
//                "  ,\n" +
//                "   is_sum_sqxz --累计当月是否发送申请携转短信 v1.3\n" +
//                "  ,\n" +
//                "   sub_id,\n" +
//                "   chnl_type_id)\n" +
//                "  select 20230301,\n" +
//                "         t1.user_id\n" +
//                "         --渠道入网工号\n" +
//                "        ,\n" +
//                "         case\n" +
//                "           when t1.priority_id = 4 then\n" +
//                "            t3.name_create_id\n" +
//                "           else\n" +
//                "            t1.join_create_id\n" +
//                "         end as join_create_id\n" +
//                "         --实名工号\n" +
//                "        ,\n" +
//                "         case\n" +
//                "           when t3.name_create_id = 2400 then\n" +
//                "            (case\n" +
//                "           when t1.priority_id = 4 then\n" +
//                "            t3.name_create_id\n" +
//                "           else\n" +
//                "            t1.join_create_id\n" +
//                "         end) else t3.name_create_id end as name_create_id\n" +
//                "         --实名渠道\n" +
//                "        ,\n" +
//                "         case\n" +
//                "           when t3.name_create_id = 2400 then\n" +
//                "            (case\n" +
//                "           when t1.priority_id = 4 then\n" +
//                "            t3.name_org_id\n" +
//                "           else\n" +
//                "            t1.join_channel\n" +
//                "         end) else t3.name_org_id end as name_org_id,\n" +
//                "         t3.record_time name_record_time --实名制时间 v1.1\n" +
//                "        ,\n" +
//                "         t3.request_source name_request_source --实名请求来源 v1.1\n" +
//                "        ,\n" +
//                "         t1.request_source --入网请求来源 v1.1\n" +
//                "        ,\n" +
//                "         case\n" +
//                "           when t4.user_number is not null then\n" +
//                "            1\n" +
//                "           else\n" +
//                "            0\n" +
//                "         end as is_sqxz --统计日是否发送申请携转短信 1:是  0：否 v1.3\n" +
//                "        ,\n" +
//                "         case\n" +
//                "           when t5.user_number is not null then\n" +
//                "            1\n" +
//                "           else\n" +
//                "            0\n" +
//                "         end as is_sum_sqxz --累计当月是否发送申请携转短信 1:是  0：否  v1.3\n" +
//                "        ,\n" +
//                "         t6.sub_id,\n" +
//                "         t6.chnl_type_id\n" +
//                "    from ft_mid_user_join_user_2_tmp t1\n" +
//                "    left join ft_mid_user_join_realname_tmp t3 on t1.user_id = t3.user_id\n" +
//                "    left join (select user_number\n" +
//                "                 from XDBASE.tb_seu_np_sms_monitor_up_opt\n" +
//                "                where tx_date = to_date(20230301, 'yyyymmdd')\n" +
//                "                  and request_source = 304007\n" +
//                "                  and up_content like '%SQXZ%'\n" +
//                "                group by user_number\n" +
//                "               union\n" +
//                "               select user_number\n" +
//                "                 from XDBASE.tb_seu_np_sms_monitor_down_opt\n" +
//                "                where tx_date = to_date(20230301, 'yyyymmdd')\n" +
//                "                  and down_content like '%SQXZ%'\n" +
//                "                group by user_number) t4 on t1.msisdn = t4.user_number\n" +
//                "    left join (select user_number\n" +
//                "                 from XDBASE.tb_seu_np_sms_monitor_up_opt\n" +
//                "                where tx_date between to_date(20230301, 'yyyymmdd') and\n" +
//                "                      to_date(20230301, 'yyyymmdd')\n" +
//                "                  and request_source = 304007\n" +
//                "                  and up_content like '%SQXZ%'\n" +
//                "                group by user_number\n" +
//                "               union\n" +
//                "               select user_number\n" +
//                "                 from XDBASE.tb_seu_np_sms_monitor_down_opt\n" +
//                "                where tx_date between to_date(20230301, 'yyyymmdd') and\n" +
//                "                      to_date(20230301, 'yyyymmdd')\n" +
//                "                  and down_content like '%SQXZ%'\n" +
//                "                group by user_number) t5 on t1.msisdn = t5.user_number\n" +
//                "    left join ft_mid_user_join_channl_02_tmp t6 on t1.user_id = t6.user_id\n";
        sqlAnalyzerDemo.analyzerV1(sql);
    }

    @Test
    public void analyzerV2_1() throws SqlParseException {
        String sql = "insert into ft_mid_user_join_daily"
                + " (sum_date,user_id,name_request_source)"
                + " select 20230301,t1.user_id,t1.request_source name_request_source "
                + " from ft_mid_user_join_user_2_tmp t1 "
                + "";
        sqlAnalyzerDemo.analyzerV2(sql);
    }

    @Test
    public void analyzerV2_2() throws SqlParseException {
        String sql = "insert into ft_mid_user_join_daily"
                + " (sum_date,user_id,name_request_source)"
                + " select 20230301,t1.user_id,t3.request_source name_request_source "
                + " from ft_mid_user_join_user_2_tmp t1 "
                + " left join ft_mid_user_join_channl_02_tmp t3 on t1.msisdn=t3.user_number "
                + "";
        sqlAnalyzerDemo.analyzerV2(sql);
    }

    @Test
    public void analyzerV2_3() throws SqlParseException {
        String sql = "insert into ft_mid_user_join_daily"
                + " (sum_date,user_id,name_request_source)"
                + " select 20230301,t1.user_id,t3.request_source name_request_source "
                + " from ft_mid_user_join_user_2_tmp t1 "
                + " left join (select user_id,request_source from ft_mid_user_join_realname_tmp) t3 on t1.user_id = t3.user_id "
                + "";
        sqlAnalyzerDemo.analyzerV2(sql);
    }

    @Test
    public void analyzerV2_4() throws SqlParseException {
        String sql = "insert into ft_mid_user_join_daily"
                + " (sum_date,user_id,name_request_source)"
                + " select 20230301,t1.user_id,t3.request_source name_request_source "
                + " from ft_mid_user_join_user_2_tmp t1 "
                + " left join (select user_id,request_source from ft_mid_user_join_realname_tmp) t3 on t1.user_id = t3.user_id "
                + " left join ft_mid_user_join_channl_02_tmp t4 on t1.msisdn=t4.user_number "
                + "";
        sqlAnalyzerDemo.analyzerV2(sql);
    }

    @Test
    public void analyzerV2_5() throws SqlParseException {
        String sql = "insert into ft_mid_user_join_daily"
                + " (sum_date,user_id,name_request_source)"
                + " select 20230301,t1.user_id,t3.request_source name_request_source "
                + " from ft_mid_user_join_user_2_tmp t1 "
                + " left join (select user_id,request_source from ft_mid_user_join_realname_tmp" +
                "                  union" +
                "                  select user_id_1,request_source_1 from XDBASE.tb_seu_np_sms_monitor_down_opt) t3 on t1.user_id = t3.user_id "
                + " left join ft_mid_user_join_channl_02_tmp t4 on t1.msisdn=t4.user_number "
                + "";
        sqlAnalyzerDemo.analyzerV2(sql);
    }
}