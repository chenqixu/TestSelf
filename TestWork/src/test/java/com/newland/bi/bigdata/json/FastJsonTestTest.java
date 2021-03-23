package com.newland.bi.bigdata.json;

import com.cqx.collect.LruCache;
import org.junit.Test;

import java.util.*;

public class FastJsonTestTest {

    @Test
    public void jsonToMap() {
        FastJsonTest fs = new FastJsonTest();
        Map<Object, Object> map = fs.jsonToMap("{\"table\": \"FRTBASE.TB_SER_OGG_USERS\", \"op_type\": \"I\", \"op_ts\": \"2021-01-18 00:06:01.218029\", \"current_ts\": \"2021-01-18T00:06:10.005003\", \"pos\": \"00000009720010873007\", \"primary_keys\": \"[HOME_CITY, USER_ID, MSISDN]\", \"tokens\": \"{}\", \"before_home_city\": \"null\", \"before_home_city_ismissing\": false, \"before_user_id\": \"null\", \"before_user_id_ismissing\": false, \"before_network_type\": \"null\", \"before_network_type_ismissing\": false, \"before_customer_id\": \"null\", \"before_customer_id_ismissing\": false, \"before_type\": \"null\", \"before_type_ismissing\": false, \"before_service_type\": \"null\", \"before_service_type_ismissing\": false, \"before_msisdn\": \"null\", \"before_msisdn_ismissing\": false, \"before_imsi\": \"null\", \"before_imsi_ismissing\": false, \"before_user_brand\": \"null\", \"before_user_brand_ismissing\": false, \"before_home_county\": \"null\", \"before_home_county_ismissing\": false, \"before_creator\": \"null\", \"before_creator_ismissing\": false, \"before_create_time\": \"null\", \"before_create_time_ismissing\": false, \"before_create_site\": \"null\", \"before_create_site_ismissing\": false, \"before_service_status\": \"null\", \"before_service_status_ismissing\": false, \"before_password\": \"null\", \"before_password_ismissing\": false, \"before_transfer_time\": \"null\", \"before_transfer_time_ismissing\": false, \"before_stop_time\": \"null\", \"before_stop_time_ismissing\": false, \"before_modify_id\": \"null\", \"before_modify_id_ismissing\": false, \"before_modify_site\": \"null\", \"before_modify_site_ismissing\": false, \"before_modify_time\": \"null\", \"before_modify_time_ismissing\": false, \"before_modify_content\": \"null\", \"before_modify_content_ismissing\": false, \"before_rc_sn\": \"null\", \"before_rc_sn_ismissing\": false, \"before_rc_expire_time\": \"null\", \"before_rc_expire_time_ismissing\": false, \"before_order_seq\": \"null\", \"before_order_seq_ismissing\": false, \"before_broker_id\": \"null\", \"before_broker_id_ismissing\": false, \"before_history_seq\": \"null\", \"before_history_seq_ismissing\": false, \"before_lock_flag\": \"null\", \"before_lock_flag_ismissing\": false, \"before_bill_type\": \"null\", \"before_bill_type_ismissing\": false, \"before_bill_credit\": \"null\", \"before_bill_credit_ismissing\": false, \"before_bill_time\": \"null\", \"before_bill_time_ismissing\": false, \"before_expire_time\": \"null\", \"before_expire_time_ismissing\": false, \"before_archives_create_time\": \"null\", \"before_archives_create_time_ismissing\": false, \"before_password_get_type\": \"null\", \"before_password_get_type_ismissing\": false, \"before_password_get_time\": \"null\", \"before_password_get_time_ismissing\": false, \"before_password_reset_time\": \"null\", \"before_password_reset_time_ismissing\": false, \"before_sub_type\": \"null\", \"before_sub_type_ismissing\": false, \"after_home_city\": \"591\", \"after_home_city_ismissing\": false, \"after_user_id\": \"591500319216463\", \"after_user_id_ismissing\": false, \"after_network_type\": \"3\", \"after_network_type_ismissing\": false, \"after_customer_id\": \"591100419337164\", \"after_customer_id_ismissing\": false, \"after_type\": \"1\", \"after_type_ismissing\": false, \"after_service_type\": \"1\", \"after_service_type_ismissing\": false, \"after_msisdn\": \"18705007277\", \"after_msisdn_ismissing\": false, \"after_imsi\": \"460027050101342\", \"after_imsi_ismissing\": false, \"after_user_brand\": \"1000\", \"after_user_brand_ismissing\": false, \"after_home_county\": \"103\", \"after_home_county_ismissing\": false, \"after_creator\": \"224257\", \"after_creator_ismissing\": false, \"after_create_time\": \"null\", \"after_create_time_ismissing\": false, \"after_create_site\": \"1031065\", \"after_create_site_ismissing\": false, \"after_service_status\": \"0\", \"after_service_status_ismissing\": false, \"after_password\": \"E8C7EBEDA9E71102\", \"after_password_ismissing\": false, \"after_transfer_time\": \"null\", \"after_transfer_time_ismissing\": false, \"after_stop_time\": \"null\", \"after_stop_time_ismissing\": false, \"after_modify_id\": \"224257\", \"after_modify_id_ismissing\": false, \"after_modify_site\": \"1031065\", \"after_modify_site_ismissing\": false, \"after_modify_time\": \"2021-01-18 00:05:55\", \"after_modify_time_ismissing\": false, \"after_modify_content\": \"用户创建\", \"after_modify_content_ismissing\": false, \"after_rc_sn\": \"null\", \"after_rc_sn_ismissing\": false, \"after_rc_expire_time\": \"2021-01-18 00:05:55\", \"after_rc_expire_time_ismissing\": false, \"after_order_seq\": \"258142837325\", \"after_order_seq_ismissing\": false, \"after_broker_id\": \"null\", \"after_broker_id_ismissing\": false, \"after_history_seq\": \"192052473977\", \"after_history_seq_ismissing\": false, \"after_lock_flag\": \"0\", \"after_lock_flag_ismissing\": false, \"after_bill_type\": \"5\", \"after_bill_type_ismissing\": false, \"after_bill_credit\": \"99999999\", \"after_bill_credit_ismissing\": false, \"after_bill_time\": \"null\", \"after_bill_time_ismissing\": false, \"after_expire_time\": \"null\", \"after_expire_time_ismissing\": false, \"after_archives_create_time\": \"2021-01-18 00:05:55\", \"after_archives_create_time_ismissing\": false, \"after_password_get_type\": \"0\", \"after_password_get_type_ismissing\": false, \"after_password_get_time\": \"null\", \"after_password_get_time_ismissing\": false, \"after_password_reset_time\": \"null\", \"after_password_reset_time_ismissing\": false, \"after_sub_type\": \"0\", \"after_sub_type_ismissing\": false}");
        System.out.println(map);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
//            String val = entry.getValue().toString();
//            if (val.equals("false") || val.equals("true")) {
//                System.out.println("kafkaValue.put(\"" + entry.getKey() + "\", " + entry.getValue() + ");");
//            } else if (strToInt(val)) {
//                System.out.println("kafkaValue.put(\"" + entry.getKey() + "\", " + entry.getValue() + ");");
//            } else {
            System.out.println("kafkaValue.put(\"" + entry.getKey() + "\", \"" + entry.getValue() + "\");");
//            }
        }
    }

    private boolean strToInt(String val) {
        try {
            Integer.valueOf(val);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Test
    public void arrayTest() {
        int[] ret = {0, 1, 2, 3};
        int[] ret1 = {4, 5, 6, 3};
        List<Integer> list = new ArrayList<>();
//        Arrays.stream(ret).boxed().collect(Collectors.toList());
//        list.addAll(Arrays.asList(ret));
        System.out.println(Arrays.asList(ret));
        for (int i : ret) {
            list.add(i);
        }
        System.out.println(list);

        Object fieldValue = null;
        System.out.println("==" + (Integer) fieldValue);
    }

    private void print(String name, Map<Integer, String> map) {
        System.out.println("****** Print " + name + " ******");
        int count = 0;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            String value = entry.getValue();
            System.out.printf("%-5s, ", value);
            count++;
            if (count == 10) {
                System.out.println();
                count = 0;
            }
        }
    }

    @Test
    public void hashTest() {
        HashMap<Integer, String> hMap = new HashMap<>(2, 0.3f);
        LinkedHashMap<Integer, String> lMap = new LinkedHashMap<>(2, 0.3f);
        LruCache<Integer, String> lruMap = new LruCache<>(10);

        for (int i = 0; i < 50; i++) {
            hMap.put(i, "#" + i);
            lMap.put(i, "#" + i);
            lruMap.put(i, "#" + i);
        }

        print("HashMap", hMap);
        print("LinkedHashMap", lMap);
        print("LruCache", lruMap);
    }
}