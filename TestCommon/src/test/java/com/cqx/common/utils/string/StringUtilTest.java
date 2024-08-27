package com.cqx.common.utils.string;

import org.junit.Test;

import java.text.SimpleDateFormat;

public class StringUtilTest {

    @Test
    public void toCharArray() throws Exception {
        String str = "%00DD";
        char[] chars = str.toCharArray();
        for (char c : chars) {
            System.out.println(c);
        }

        String taskId = "100000000000@2019011014181300";
        //切割@，后面16位时间
        String[] taskId_array = taskId.split("@", -1);
        if (taskId_array.length != 2) throw new NullPointerException("任务ID格式不对，需要task_tempalte_id@时间");
        String time = taskId_array[1];
        if (time.length() != 16) throw new NullPointerException("任务ID格式不对，需要task_tempalte_id@时间，时间需要16位");
        //截取前面14位
        String _time = time.substring(0, 14);
        System.out.println(_time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(simpleDateFormat.parse(_time));
    }

    @Test
    public void getID() {
        System.out.println(StringUtil.getID("select a.scene_id ,case when b.S00000001270_2 is null or b.S00000001270_2=9999 then 99 else b.S00000001270_2 end as sex ,case when b.S00000002518_2 is null then 99 else b.S00000002518_2 end as age_lev ,count(distinct a.msisdn) as real_cust_cnt from (select scene_id,msisdn from posi_scene_footprint where (out_time is null or out_time>=to_date(:SUM_TIME,'YYYYMMDDHH24MISS')) and update_time>(to_date(:SUM_TIME,'YYYYMMDDHH24MISS')-2/24) and in_time< to_date(:SUM_TIME,'YYYYMMDDHH24MISS') and length(scene_id)<30 and scene_id like 'fzgajk_%' ) a left join #locate_mart_1# b on a.msisdn=b.msisdn group by a.scene_id ,case when b.S00000001270_2 is null or b.S00000001270_2=9999 then 99 else b.S00000001270_2 end ,case when b.S00000002518_2 is null then 99 else b.S00000002518_2 end; ", "S"));
    }
}