package com.cqx.common.utils.system;

import com.cqx.common.bean.system.TimeVarBean;
import com.cqx.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间变量工具
 *
 * @author chenqixu
 */
public class TimeVarUtils {
    private static final Logger logger = LoggerFactory.getLogger(TimeVarUtils.class);
    private static Map<String, TimeVarBean> dateMap = new HashMap<>();

    static {
        dateMap.put("[_data_time]", new TimeVarBean("yyyyMMddHHmmss"));// 数据时间yyyyMMddHHmmss
        dateMap.put("[_data_hour]", new TimeVarBean("yyyyMMddHH"));// 数据日期yyyyMMddHH
        dateMap.put("[_data_date]", new TimeVarBean("yyyyMMdd"));// 数据日期yyyyMMdd
        dateMap.put("[_data_month]", new TimeVarBean("yyyyMM"));// 数据月yyyyMM
        dateMap.put("[_data_year]", new TimeVarBean("yyyy"));// 数据年份yyyy
        dateMap.put("[_data_pre_hour]", new TimeVarBean("yyyyMMddHH", false, 1000 * 60 * 60));// 数据日期上一小时yyyyMMddHH
        dateMap.put("[_data_pre_date]", new TimeVarBean("yyyyMMdd", false, 1000 * 60 * 60 * 24));// 数据日期上一天yyyyMMdd
    }

    public void parserTaskID(String task_id, Map<String, ?> params) {
        // 解析出时间，并计算时间偏移量，替换参数中所有的时间关键字
        // task_id规则，前面是task_template_id，中间是@，后面是时间，格式：yyyyMMddHHmmss+两位任务重做的序号
        // 101067676513@2021062415000000
        String[] task_id_array = task_id.split("@", -1);
        if (task_id_array.length == 2) {
            String task_template_id = task_id_array[0];
            String time_seq = task_id_array[1];
            String seq = time_seq.substring(14);
            String time = time_seq.substring(0, 14);
            // 计算时间偏移
            try {
                long time_long = Utils.getTime(time);
                for (Map.Entry<String, TimeVarBean> entry : dateMap.entrySet()) {
                    TimeVarBean timeVarBean = entry.getValue();
                    timeVarBean.setBaseTime(time_long);
                    timeVarBean.calc();
                }
                // 替换参数中可能存在的时间偏移占位符
                for (Map.Entry entry : params.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof java.lang.String) {
                        for (Map.Entry<String, TimeVarBean> dateEntry : dateMap.entrySet()) {
                            String _value = (String) value;
                            if (_value.contains(dateEntry.getKey())) {
                                _value = _value.replace(dateEntry.getKey(), dateEntry.getValue().getResultStrTime());
                                entry.setValue(_value);
                                logger.info("原始参数：{}，找到时间变量：{}，进行替换：{}"
                                        , value, dateEntry.getKey(), dateEntry.getValue().getResultStrTime());
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                throw new NullPointerException(String.format("task_id解析异常！%s", task_id));
            }
        } else {
            throw new NullPointerException(String.format("task_id解析异常！%s", task_id));
        }
    }
}
