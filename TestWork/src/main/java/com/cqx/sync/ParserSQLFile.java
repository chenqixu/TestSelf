package com.cqx.sync;

import com.cqx.common.utils.file.FileMangerCenter;
import com.cqx.common.utils.jdbc.*;
import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 读取plsql导出的insert into.sql文件，解析成prepareStatement批量入库，防止中间的内容出现换行而产生入不进去的异常
 *
 * @author chenqixu
 */
public class ParserSQLFile {
    private static final MyLogger logger = MyLoggerFactory.getLogger(QueryAndSave.class);
    private static final String RS = "insert into";
    private static final String RS1 = "prompt";
    private static final String RS2 = "set ";
    private static final String R1 = "values (";
    private static final String R2 = "to_date(";
    private static final String SP1 = ", ";
    private static final String NULL = "null";

    static {
        DBFormatUtil.setOracleDbType();
    }

    private JDBCUtil jdbcUtil;
    private FileMangerCenter fileMangerCenter;

    public ParserSQLFile(DBBean dbBean, String fileName) throws IOException {
        jdbcUtil = new JDBCUtil(dbBean);
        fileMangerCenter = new FileMangerCenter(fileName);
        fileMangerCenter.initReader();
    }

    public void readAndExec() throws Exception {
        String fields = "HOME_CITY, USER_ID, NETWORK_TYPE, CUSTOMER_ID, TYPE, SERVICE_TYPE, MSISDN, IMSI, USER_BRAND, HOME_COUNTY, CREATOR, CREATE_TIME, CREATE_SITE, SERVICE_STATUS, PASSWORD, TRANSFER_TIME, STOP_TIME, MODIFY_ID, MODIFY_SITE, MODIFY_TIME, MODIFY_CONTENT, RC_SN, RC_EXPIRE_TIME, ORDER_SEQ, BROKER_ID, HISTORY_SEQ, LOCK_FLAG, BILL_TYPE, BILL_CREDIT, BILL_TIME, EXPIRE_TIME, ARCHIVES_CREATE_TIME, PASSWORD_GET_TYPE, PASSWORD_GET_TIME, PASSWORD_RESET_TIME, SUB_TYPE";
        String table_name = "USERS";
        BeanUtil beanUtil = jdbcUtil.generateBeanByTabeNameAndFields(fields.toLowerCase(), table_name);
        LinkedHashMap<String, Object> fieldsMap = beanUtil.getFieldsMap();
        logger.info("FieldsType：{}，FieldsMap：{}", beanUtil.getFieldsType(), fieldsMap);
        StringBuilder questionMarkSB = new StringBuilder();
        questionMarkSB.append(" values(");
        for (int i = 0; i < beanUtil.getFieldsType().size(); i++) {
            questionMarkSB.append("?");
            if ((i + 1) == beanUtil.getFieldsType().size()) {
            } else {
                questionMarkSB.append(",");
            }
        }
        questionMarkSB.append(")");
        String sql = "insert into " + table_name + "(" + fields + ")" + questionMarkSB.toString();
        logger.info("sql：{}", sql);

        String tmp;
        int cnt = 0;
        StringBuilder sb = new StringBuilder();
        List<List<QueryResult>> valuesResultList = new ArrayList<>();
        while ((tmp = fileMangerCenter.readLine()) != null) {
            if (tmp.startsWith(RS)) {//遇到insert into开头就结束
                //结束
                String vv = sb.toString();
                if (!vv.endsWith(";")) {
                    logger.warn("values：{}，line：{}", sb.toString(), fileMangerCenter.getLineNum());
                } else {
                    buildInsertInto(vv, beanUtil, valuesResultList);
                    cnt++;
                    if (cnt % 2000 == 0) {
                        jdbcUtil.executeBatch(sql, valuesResultList, beanUtil.getFieldsType());
                        logger.info("exec_num：{}", cnt);
                        valuesResultList = new ArrayList<>();
                    }
                }
                sb = new StringBuilder();
            } else if (tmp.startsWith(RS1) || tmp.startsWith(RS2)) {//遇到prompt或set就跳过
                continue;
            } else {//其他则当成正常内容进行拼接
                //内容拼接
                sb.append(tmp);
            }
//            if (cnt > 3) break;
        }
        //最后一条处理
        if (sb.toString().length() > 0) {
            buildInsertInto(sb.toString(), beanUtil, valuesResultList);
            cnt++;
            jdbcUtil.executeBatch(sql, valuesResultList, beanUtil.getFieldsType());
            logger.info("exec_num：{}", cnt);
        }
    }

    private void buildInsertInto(String vv, BeanUtil beanUtil, List<List<QueryResult>> valuesResultList) {
        //吃掉values ( 和 );
        vv = vv.trim().substring(R1.length(), vv.length() - 2);
        //用, 进行切割
        String[] array = vv.split(SP1, -1);
        //遇到to_date往前合并1个
        List<QueryResult> values = new ArrayList<>();
        int value_num = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].startsWith(R2)) {
                String filedType = beanUtil.getFieldsType().get(value_num);
                values.add(buildQueryResult(array[i] + SP1 + array[i + 1], filedType));
                i = i + 1;
                value_num++;
            } else {
                String fieldType = beanUtil.getFieldsType().get(value_num);
                values.add(buildQueryResult(array[i], fieldType));
                value_num++;
            }
        }
        valuesResultList.add(values);
    }

    private QueryResult buildQueryResult(String value, String fieldType) {
        QueryResult queryResult = new QueryResult();
        switch (fieldType) {
            case "java.math.BigDecimal":
                if (value.equals(NULL)) {
                    queryResult.setValue(null);
                } else {
                    try {
                        queryResult.setValue(new BigDecimal(value));
                    } catch (NumberFormatException e) {
                        logger.warn("value：{}，line：{}", value, fileMangerCenter.getLineNum());
                        throw e;
                    }
                }
                queryResult.setColumnClassName(fieldType);
                break;
            case "java.sql.Timestamp":
                if (value.equals(NULL)) {
                    queryResult.setValue(null);
                } else {
                    //按SP1分割：to_date('24-12-2013 05:36:28', 'dd-mm-yyyy hh24:mi:ss')
                    String[] value_arr = value.split(SP1, -1);
                    String v = value_arr[0];
                    String f = value_arr[1];
                    //去掉R2，去掉)，去掉'
                    v = v.replace(R2, "")
                            .replace("'", "");
                    f = f.replace(")", "")
                            .replace("'", "");
                    //根据后面的格式对前面的时间进行格式化
                    //替换成java识别的格式
                    f = f.replace("mm", "MM")
                            .replace("hh24", "HH")
                            .replace("mi", "mm");
//                    logger.info("v：{}，f：{}", v, f);
                    SimpleDateFormat sdf = new SimpleDateFormat(f);
                    try {
                        queryResult.setValue(new java.sql.Timestamp(sdf.parse(v).getTime()));
                    } catch (ParseException e) {
                        logger.warn("value：{}，v：{}，f：{}，line：{}", value, v, f, fileMangerCenter.getLineNum());
                        throw new NullPointerException(e.getMessage());
                    }
                }
                queryResult.setColumnClassName(fieldType);
                break;
            case "java.lang.String":
                if (value.equals(NULL)) {
                    queryResult.setValue(null);
                } else {
                    //去掉'
                    value = value.replace("'", "");
                    queryResult.setValue(value);
                }
                queryResult.setColumnClassName(fieldType);
                break;
        }
        return queryResult;
    }

    /**
     * 资源释放
     */
    public void release() throws IOException {
        if (jdbcUtil != null) jdbcUtil.close();
        if (fileMangerCenter != null) fileMangerCenter.close();
    }

}
