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
}