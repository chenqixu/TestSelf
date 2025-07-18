package com.bussiness.bi.bigdata.label.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class RuleUtilTest {
    private RuleUtil ru;

    @Before
    public void setUp() {
        ru = new RuleUtil();
    }

    @Test
    public void test1() {
        // 表达式
        String expression = "((a>1 && b>1) || (a>2 && c =~ list)) && d>1";
        // 表达式对应的值
        Map<String, Object> env = new HashMap<>();
        env.put("a", 1);
        env.put("b", 2);
        env.put("c", 4);
        env.put("d", 2);
        env.put("list", Arrays.asList(1, 2, 3));

        // 执行表达式，并解析跟踪数据
        List<SubResultBean> srbList = ru.parseTraceOutput(ru.execute(expression, env));

        // 根因查找
        ru.rootCauseLookup(srbList);
    }

    @Test
    public void test2() {
        // 表达式
        String expression = "((中台_是否我号异宽用户包含【是】\n" +
                " 或\n" +
                " 中台_是否移动副卡_异网主卡包含【是】\n" +
                " 或\n" +
                " 是否宽带用户包含【否】)\n" +
                " 或\n" +
                " (低消客户4722732_泉包含【是】\n" +
                " 且\n" +
                " 近三月月均流量dou大于等于【500】)\n" +
                " 或\n" +
                " (是否宽带用户包含【否】\n" +
                " 且\n" +
                " 客户有线宽带带宽 =~【list】\n" +
                " 且\n" +
                " 中台_是否宽带提速包客户包含【否】))\n" +
                " 且\n" +
                " 是否通信用户包含【是】\n" +
                " 且\n" +
                " 营销互斥矩阵客户_泉包含【否】\n" +
                " 且\n" +
                " 是否机器卡包含【否】\n" +
                " 且\n" +
                " 是否物联网卡包含【否】\n" +
                " 且\n" +
                " 三个月月均真实通信费小于等于【69】";
        expression = expression.replaceAll("包含", " == ");
        expression = expression.replaceAll("小于", " < ");
        expression = expression.replaceAll("大于", " > ");
        expression = expression.replaceAll("小于等于", " <= ");
        expression = expression.replaceAll("大于等于", " >= ");
        expression = expression.replaceAll("且", " && ");
        expression = expression.replaceAll("或", " || ");
        expression = expression.replaceAll("【是】", "1");
        expression = expression.replaceAll("【否】", "0");
        expression = expression.replaceAll("【", "");
        expression = expression.replaceAll("】", "");

        // 表达式对应的值
        Map<String, Object> env = new HashMap<>();
        env.put("中台_是否我号异宽用户", 0);
        env.put("中台_是否移动副卡_异网主卡", 0);
        env.put("是否宽带用户", 1);
        env.put("低消客户4722732_泉", 0);
        env.put("近三月月均流量dou", 8457.36);
        env.put("客户有线宽带带宽", 100);
        env.put("中台_是否宽带提速包客户", 0);
        env.put("是否通信用户", 1);
        env.put("营销互斥矩阵客户_泉", 0);
        env.put("是否机器卡", 0);
        env.put("是否物联网卡", 0);
        env.put("三个月月均真实通信费", 58.08);
        env.put("list", Arrays.asList(0, 10, 100, 12, 2, 20, 200, 300, 4, 50, 500, 68));

        // 执行表达式，并解析跟踪数据
        List<SubResultBean> srbList = ru.parseTraceOutput(ru.execute(expression, env));

        // 根因查找
        ru.rootCauseLookup(srbList);
    }

    @Test
    public void test3() {
        // 表达式
        String expression = "a > 1 && b >1";
        // 表达式对应的值
        Map<String, Object> env = new HashMap<>();
        env.put("a", 2);
        env.put("b", 1);

        // 执行表达式，并解析跟踪数据
        List<SubResultBean> srbList = ru.parseTraceOutput(ru.execute(expression, env));

        // 根因查找
        ru.rootCauseLookup(srbList);
    }

    @Test
    public void test4() {
        // 表达式
        String expression = "TG5910010021000561 > 1 && TG5910010021000557 >1";
        // 表达式对应的值
        Map<String, Object> env = new HashMap<>();
        env.put("TG5910010021000561", 2);
        env.put("TG5910010021000557", 1);

        // 执行表达式，并解析跟踪数据
        List<SubResultBean> srbList = ru.parseTraceOutput(ru.execute(expression, env));

        // 根因查找
        ru.rootCauseLookup(srbList);
    }

    @Test
    public void paramTest() {
        String p1 = "{\"respResult\":\"1\",\"respData\":[{\"tagId\":\"TG5910010021001962\",\"tagName\":\"是否集团统谈合约接续用户\",\"tagAlias\":null,\"tagValue\":\"0\",\"tagValueDesc\":\"否\",\"tagValueAlias\":null,\"tagType\":\"4\",\"tagTypeDesc\":\"布尔类型\"},{\"tagId\":\"TG5910010021000561\",\"tagName\":\"合约到期月类型\",\"tagAlias\":null,\"tagValue\":\"5\",\"tagValueDesc\":\"当月\",\"tagValueAlias\":null,\"tagType\":\"3\",\"tagTypeDesc\":\"枚举类型\"},{\"tagId\":\"TG5910010021000557\",\"tagName\":\"推荐升档提价接续策略套餐资费\",\"tagAlias\":null,\"tagValue\":\"219\",\"tagValueDesc\":\"219\",\"tagValueAlias\":null,\"tagType\":\"2\",\"tagTypeDesc\":\"数值类型\"},{\"tagId\":\"TG5910010021000558\",\"tagName\":\"推荐升档提价接续策略套餐折扣率\",\"tagAlias\":null,\"tagValue\":\"0.55\",\"tagValueDesc\":\"0.55\",\"tagValueAlias\":null,\"tagType\":\"2\",\"tagTypeDesc\":\"数值类型\"}],\"respErrorMsg\":null}";
        JSONObject obj = (JSONObject) JSON.parse(p1);
        JSONArray ja = obj.getJSONArray("respData");
        Map<String, Object> env = new HashMap<>();
        for (int i = 0; i < ja.size(); i++) {
            System.out.println(ja.get(i));
            env.put(ja.getJSONObject(i).getString("tagName"), ja.getJSONObject(i).getBigDecimal("tagValue"));
        }
        System.out.println(env);

        String list = "list|0, 10, 100, 12, 2, 20, 200, 300, 4, 50, 500, 68";
        String[] arr = list.split("\\|");
        System.out.println(arr[0]);
        System.out.println(arr[1]);
        String[] valList = arr[1].split(",", -1);
        List<Integer> val = new ArrayList<>();
        for (String v : valList) {
            val.add(Integer.valueOf(v.trim()));
        }
        System.out.println(val);
    }
}