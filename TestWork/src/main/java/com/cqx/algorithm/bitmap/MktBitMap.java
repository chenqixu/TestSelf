package com.cqx.algorithm.bitmap;

import com.cqx.bean.MktBean;
import com.cqx.bean.UserMktBean;
import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.JDBCUtil;
import com.bussiness.bi.bigdata.time.TimeCostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 营销案
 *
 * @author chenqixu
 */
public class MktBitMap {
    private static final Logger logger = LoggerFactory.getLogger(MktBitMap.class);
    private ConcurrentSkipListMap<Long, Long> mktData = new ConcurrentSkipListMap<>(new MapKeyComparator());
    private ConcurrentHashMap<Long, Long> sortData = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, BitMap> userMktBitMaps = new ConcurrentHashMap<>();
    private JDBCUtil jdbcUtil;
    private int count;
    private int sum_date = 20210303;

    public void init(DBBean dbBean) {
        jdbcUtil = new JDBCUtil(dbBean);
    }

    public void close() {
        if (jdbcUtil != null) jdbcUtil.close();
    }

    private String getSumDate(String tag) {
        return tag + "sum_date=" + sum_date + " ";
    }

    private String getSumDate() {
        return getSumDate("");
    }

    //全部在订和互斥，关系转换
    public void mktToIndex() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        StringBuilder sql = new StringBuilder();
        sql.append("select sale_ids from (select distinct unnest(string_to_array(sale_id,',')) as sale_ids from rec_mkt_mutual_user_relation where ");
        sql.append(getSumDate());
        sql.append(") t where sale_ids<>''");
        sql.append(" union ");
        sql.append(" select rela_sale_ids from (select distinct unnest(string_to_array(rela_sale_id,',')) as rela_sale_ids from rec_mkt_mutual_relation where ");
        sql.append(getSumDate());
        sql.append(") t where rela_sale_ids<>''");
        logger.info("mktToIndex sql：{}", sql);
        try {
            for (MktBean mktBean : jdbcUtil.executeQuery(sql.toString(), MktBean.class)) {
                addMkt(mktBean.getSale_ids());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        //写入顺序
        sort();
        logger.info("mktToIndex cost：{}", tc.stopAndGet());
    }

    //用户在订和互斥关系
    public void createUserMktList() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        StringBuilder sql = new StringBuilder();
//        sql.append("select msisdn,rela_sale_ids from ( ");
//        sql.append(" select t1.msisdn,t2.rela_sale_ids  from (select msisdn,unnest(string_to_array(sale_id,',')) as sale_ids from rec_mkt_mutual_user_relation").append(getWhere()).append(") t1 ");
//        sql.append(" inner join (select sale_id,rela_sale_ids from (select sale_id,unnest(string_to_array(rela_sale_id,',')) as rela_sale_ids from rec_mkt_mutual_relation").append(getWhere()).append(") t where rela_sale_ids<>'') t2 ");
//        sql.append(" on t1.sale_ids=t2.sale_id ");
//        sql.append(" union ");
//        sql.append(" select msisdn,unnest(string_to_array(sale_id,',')) as rela_sale_ids from rec_mkt_mutual_user_relation ").append(getWhere());
//        sql.append(" ) tt order by msisdn");
        sql.append("select msisdn,rela_sale_ids from(");
        sql.append(" select msisdn,t2.rela_sale_id as rela_sale_ids ");
        sql.append(" from (select msisdn,unnest(string_to_array(sale_id,',')) as sale_id from rec_mkt_mutual_user_relation where ").append(getSumDate()).append(") t1 ");
        sql.append(" inner join rec_mkt_mutual_relation t2 ");
        sql.append(" on t1.sale_id=t2.sale_id and ").append(getSumDate("t2."));
        sql.append(" union all ");
        sql.append(" select msisdn,unnest(string_to_array(sale_id,',')) as rela_sale_ids from rec_mkt_mutual_user_relation where ").append(getSumDate()).append(") tt group by msisdn,rela_sale_ids order by msisdn");
        logger.info("createUserMktList sql：{}", sql);
        try {
            long his_msisdn = 0L;
            for (UserMktBean userMktBean : jdbcUtil.executeQuery(sql.toString(), UserMktBean.class)) {
                if (his_msisdn == 0L) {
                    his_msisdn = userMktBean.getMsisdn();
                    BitMap bitMapTest = new BitMap(count);
                    userMktBitMaps.put(his_msisdn, bitMapTest);
                } else if (his_msisdn != userMktBean.getMsisdn()) {
                    his_msisdn = userMktBean.getMsisdn();
                    BitMap bitMapTest = new BitMap(count);
                    userMktBitMaps.put(his_msisdn, bitMapTest);
                }
                String rela_sale_ids = userMktBean.getRela_sale_ids();
                if (rela_sale_ids.contains(",")) {
                    String[] rela_sale_ids_array = rela_sale_ids.split(",", -1);
                    for (String rela_sale_id : rela_sale_ids_array) {
                        if (rela_sale_id.trim().length() > 0) {
                            long index = getIndexByValue(Long.valueOf(rela_sale_id));
                            userMktBitMaps.get(his_msisdn).add((int) index);
                        }
                    }
                } else {
                    if (rela_sale_ids.trim().length() > 0) {
                        long index = getIndexByValue(Long.valueOf(rela_sale_ids));
                        userMktBitMaps.get(his_msisdn).add((int) index);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("createUserMktList cost：{}", tc.stopAndGet());
    }

    //通过手机号、营销案进行判断
    public boolean isRe(long msisdn, long mkt) {
        //获取mkt的index
        Long index = getIndexByValue(mkt);
        if (index != null) {
            //判断用户是否在订或互斥
            boolean result = userMktBitMaps.get(msisdn).contain((int) index.longValue());
            logger.info("msisdn：{}，mkt：{}，result：{}", msisdn, mkt, result);
            return result;
        } else {
            logger.info("msisdn：{}，mkt：{}，result：{}", msisdn, mkt, false);
            return false;
        }
    }

    public void addMkt(long mkt) {
        mktData.put(mkt, 0L);
    }

    public void sort() {
        long index = 1;
        for (Map.Entry<Long, Long> entry : mktData.entrySet()) {
            mktData.put(entry.getKey(), index);
            sortData.put(index, entry.getKey());
            index++;
        }
        count = (int) (index - 1);
    }

    public Long getIndexByValue(long value) {
        return mktData.get(value);
    }

    public Long getValueByIndex(long index) {
        return sortData.get(index);
    }

    static private class MapKeyComparator implements Comparator<Long> {

        @Override
        public int compare(Long l1, Long l2) {
            return l1.compareTo(l2);
        }
    }
}
