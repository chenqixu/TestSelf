package com.cqx.calcite.example;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParser;

import java.util.List;

/**
 * Demo1
 *
 * @author chenqixu
 */
public class Demo1 {

    public static void main(String[] args) throws Exception {
        String sql = "select price from transactions";
        sql = "select price as a1 from transactions t";
        sql = "select price as a1 from transactions t where t.price='1'";
        sql = "select price as a1 from transactions t where t.price='1' or t.li='2'";
        sql = "insert into table1(p1,p2,p3) select t.price,t1.code,case when t.price>20 then 1 else 0 end from transactions t left join dim_home t1 on t.area_code=t1.code where t.price='1' or t.li='2'";
        //s1mme
        sql = "insert into nmc_tb_lte_s1mme_new " +
                "select city,xdr_id,imsi,imei,parserSubstrFirst(msisdn)," +
                "procedure_type,subprocedure_type,procedure_start_time,procedure_delay_time,procedure_end_time,procedure_status,old_mme_group_id,old_mme_code,lac,tac,cell_id,other_tac,other_eci,home_code,msisdn_home_code,old_mme_group_id_1,old_mme_code_1,old_m_tmsi,old_tac,old_eci,cause,keyword,mme_ue_s1ap_id,request_cause,keyword_2,keyword_3,keyword_4,bearer_qci1,bearer_status1,bearer_qci2,bearer_status2,bearer_qci3,bearer_status3 from s1mme_file";
        //http
        sql = "insert into nmc_tb_lte_http " +
                "select /*+ abc(1), def */ city_1,imsi,imei,msisdn,tac,eci,rat,procedure_start_time,app_class,host,uri,apply_classify,apply_name,web_classify,web_name,search_keyword,procedure_end_time,upbytes,downbytes " +
                "from http_file t1 " +
                "left join dfgx t2 on t2.send=concat(msisdn,app_class_top,app_class,unknow,eci,imei,tac,procedure_start_time,procedure_end_time,server_ip,destination_port,user_agent,uri,host,http_content_type,upbytes,downbytes,city_1,imsi,delay_time,ownclass,busi_bear_type,mcc,refer_uri,app_content,unknow,unknow,target_action,upflow,downflow) ";
        //融合
        sql = "insert into nmc_app_nl_loc_mix_v1 " +
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
        //实时位置
        sql = "merge into realtime_location as a " +
                "using (" +
                "select  msisdn,id " +
                "from nmc_app_nl_loc_mix_v1 t1 " +
                "inner join heapUserLacCiCache /*+ putLacCi(msisdn, lacCi, btime) */ t2 " +
                "on (t1.msisdn=t2.msisdn and t2.msisdn is null) " +
                "or (t1.msisdn=t2.msisdn and t2.msisdn is not null " +
                "and (" +
                "(t2.btime<t1.btime) " +
                "or (t2.lacci=t1.lacci and t1.btime-t2.btime>=2000)" +
                "or (t2.lacci<>t1.lacci)" +
                "))" +
                "left join UserInfoCache t3 on t1.msisdn=t3.msisdn" +
                "left join RegionConfigCache t4 on t1.msisdn=t4.msisdn " +
                "left join ResidentCache t5 on t1.msisdn=t5.msisdn " +
                "where length(t1.msisdn)=15" +
                ") as b on a.msisdn=b.msisdn " +
                "WHEN MATCHED THEN " +
                "UPDATE set a.id=b.id " +
                "WHEN NOT MATCHED THEN " +
                "insert values(b.msisdn)";
//        //实时轨迹
//        sql = "";

        System.out.println("sql：" + sql);

        //解析
        SqlParser.Config config = SqlParser.configBuilder().setLex(Lex.ORACLE).build();
        SqlParser parser = SqlParser.create(sql, config);
        SqlNode node = parser.parseStmt();
        System.out.println("node.getClass：" + node.getClass());
        //解析join
        switch (node.getKind()) {
            case SELECT:
                parserSelect(node);
                break;
            case INSERT:
                parserInsert(node);
                break;
            case MERGE:
                parserMerge(node);
                break;
            default:
                break;
        }
    }

    private static String paserTableName(SqlNode tbl) {
        if (tbl.getKind() == SqlKind.AS) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall) tbl;
            return sqlBasicCall.operands[1].toString();
        }
        return ((SqlIdentifier) tbl).toString();
    }

    private static void parserMerge(SqlNode node) {
        SqlMerge sqlMerge = (SqlMerge) node;
        //目标表
        sqlMerge.getTargetTable();
        //条件
        sqlMerge.getCondition();
        //源
        sqlMerge.getSourceTableRef();
        //插入操作
        SqlNode insert = sqlMerge.getInsertCall();
        parserInsert(insert);
        //更新操作
        SqlNode update = sqlMerge.getUpdateCall();
        parserUpdate(update);
    }

    private static void parserInsert(SqlNode node) {
        SqlInsert sqlInsert = (SqlInsert) node;
        System.out.println("=======source=======");
        SqlNode source = sqlInsert.getSource();
        switch (source.getKind()) {
            case UNION:
                parserUnion(source);
                break;
            case SELECT:
                parserSelect(source);
                break;
            case VALUES:
                parserValues(source);
                break;
            default:
                break;
        }
        System.out.println("=======target=======");
        SqlNode targetTable = sqlInsert.getTargetTable();
        System.out.println("targetTable：" + targetTable);
        SqlNodeList targetColumnList = sqlInsert.getTargetColumnList();
        if (targetColumnList != null)
            for (SqlNode targetColumn : targetColumnList) {
                System.out.println("targetColumn：" + targetColumn);
            }
    }

    private static void parserUpdate(SqlNode node) {
        SqlUpdate sqlUpdate = (SqlUpdate) node;
        System.out.println("=======target=======");
        SqlNode targetTable = sqlUpdate.getTargetTable();
        System.out.println("targetTable：" + targetTable);
        SqlNodeList targetColumnList = sqlUpdate.getTargetColumnList();
        if (targetColumnList != null)
            for (SqlNode targetColumn : targetColumnList) {
                System.out.println("targetColumn：" + targetColumn);
            }
        SqlNodeList sourceExpressionList = sqlUpdate.getSourceExpressionList();
        if (sourceExpressionList != null)
            for (SqlNode sourceExpression : sourceExpressionList) {
                System.out.println("sourceExpression：" + sourceExpression);
            }
        System.out.println("=======condition=======");
        SqlNode condition = sqlUpdate.getCondition();
        parserCondition(condition);
    }

    private static void parserCondition(SqlNode node) {

    }

    private static void parserUnion(SqlNode node) {
        SqlBasicCall sqlBasicCall = (SqlBasicCall) node;
        List<SqlNode> operandlist = sqlBasicCall.getOperandList();
        for (SqlNode sqlNode : operandlist) {
            parserSelect(sqlNode);
        }
    }

    private static void parserSelect(SqlNode node) {
        SqlSelect sqlSelect = (SqlSelect) node;

        //hints
        SqlNodeList hints = sqlSelect.getHints();
        if (hints != null)
            for (SqlNode hint : hints) {
                SqlHint sqlHint = (SqlHint) hint;
                List<String> options = sqlHint.getOptionList();
                System.out.println("sqlHint：" + sqlHint + " " + sqlHint.getClass() + " options：" + (options != null ? options : ""));
            }

        //query fields
        for (SqlNode selectNode : sqlSelect.getSelectList()) {
            System.out.println(selectNode + " " + selectNode.getClass());
        }

        //join
        SqlNode sqlFrom = sqlSelect.getFrom();
        if (sqlFrom.getKind() == SqlKind.JOIN) {
            SqlJoin sqlJoin = (SqlJoin) sqlFrom;
            SqlNode left = sqlJoin.getLeft();
            SqlNode right = sqlJoin.getRight();
            String leftTable = paserTableName(left);
            String rightTable = paserTableName(right);
            System.out.println("getJoinType：" + sqlJoin.getJoinType());
            System.out.println("getConditionType：" + sqlJoin.getConditionType());
            System.out.println("getCondition：" + sqlJoin.getCondition());
            System.out.println("leftTable：" + leftTable);
            System.out.println("rightTable：" + rightTable);
        }

        //from
        System.out.println("form：" + sqlSelect.getFrom());

        //where
        SqlNode where = sqlSelect.getWhere();
        parserWhere(where);
    }

    private static void parserWhere(SqlNode where) {
        if (where != null) {
            if (where instanceof SqlBasicCall) {
                SqlBasicCall sqlBasicCallWhere = (SqlBasicCall) where;
                List<SqlNode> whereList = sqlBasicCallWhere.getOperandList();
                for (SqlNode _where : whereList) {
                    if (_where instanceof SqlBasicCall) {
                        SqlBasicCall _where1 = (SqlBasicCall) _where;
                        SqlKind _where1SqlKink = _where1.getOperator().getKind();
                        switch (_where1SqlKink) {
                            case IN:
                                System.out.println("field：" + _where1.getOperandList().get(0) + " condition：" + _where1.getOperandList().get(1));
                                break;
                            case AND:
                                parserWhere(_where);
                                break;
                            default:
                                System.out.println("field：" + _where1.getOperandList().get(0) + " condition：" + _where1.getOperator());
                                break;
                        }
                    }
                }
            }
        }
    }

    private static void parserValues(SqlNode node) {
        SqlBasicCall sqlBasicCall = (SqlBasicCall) node;
        List<SqlNode> operandlist = sqlBasicCall.getOperandList();
        for (SqlNode sqlNode : operandlist) {

        }
    }
}
