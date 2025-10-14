package com.bussiness.bi.bigdata.label.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bussiness.bi.bigdata.utils.string.StringUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        expression = expression.replaceAll("小于等于", " <= ");
        expression = expression.replaceAll("大于等于", " >= ");
        expression = expression.replaceAll("小于", " < ");
        expression = expression.replaceAll("大于", " > ");
        expression = expression.replaceAll("且", " && ");
        expression = expression.replaceAll("或", " || ");
        expression = expression.replaceAll("【是】", "1");
        expression = expression.replaceAll("【否】", "0");
        expression = expression.replaceAll("【", "");
        expression = expression.replaceAll("】", "");
        expression = expression.replaceAll("等于", " == ");

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
        String rule = "(一证多卡客户月均总价值大于等于【0】小于等于【200】\n" +
                "且\n" +
                "是否近3个月降档客户包含【A, B, C, D+, 集团客户】)\n" +
                "且\n" +
                "统计日_是否生效有价流量合约包含【否】";

        Map<String, List> listMap = new HashMap<>();
        AtomicInteger seq = new AtomicInteger(0);

        String[] rules = rule.split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (String expression : rules) {
            if (expression.contains("小于等于") && expression.contains("大于等于")) {
                String key = expression.substring(0, expression.indexOf("大于等于")).trim();
                key = key.replace("(", "");
                expression = expression.replaceAll("大于等于", " >= ");
                expression = expression.replaceAll("小于等于", " && " + key + " <= ");
            }
            if (expression.contains("包含")) {
                String val = expression.substring(expression.indexOf("【") + "【".length(), expression.indexOf("】"));
                if (val.contains(",")) {
                    String mapKey = "list" + seq.incrementAndGet();
                    String[] vals = val.split(",", -1);
                    boolean isNumeric = false;
                    for (String _v : vals) {
                        if (!StringUtil.isNumeric(_v)) {
                            isNumeric = false;
                            break;
                        } else {
                            isNumeric = true;
                        }
                    }
                    if (isNumeric) {
                        List<Number> valList = new ArrayList<>();
                        for (String _v : vals) {
                            valList.add(Double.valueOf(_v));
                        }
                        listMap.put(mapKey, valList);
                    } else {
                        List<String> valList = new ArrayList<>();
                        for (String _v : vals) {
                            valList.add(_v);
                        }
                        listMap.put(mapKey, valList);
                    }
                    expression = expression.replace(val, mapKey);
                    expression = expression.replace("包含", " =~ ");
                }
            }
            expression = expression.replace("包含", " == ");
            expression = expression.replace("小于等于", " <= ");
            expression = expression.replace("大于等于", " >= ");
            expression = expression.replace("小于", " < ");
            expression = expression.replace("大于", " > ");
            expression = expression.replace("且", " && ");
            expression = expression.replace("或", " || ");
//            expression = expression.replace("【是】", "1");
//            expression = expression.replace("【否】", "0");
            expression = expression.replace("【是】", "【'是'】");
            expression = expression.replace("【否】", "【'否'】");
            expression = expression.replace("【", "");
            expression = expression.replace("】", "");
            expression = expression.replace("等于", " == ");
            sb.append(expression);
        }
        System.out.println("[测试]" + sb.toString());

        // 表达式
        String expression = "一证多卡客户月均总价值 >= 0 && 一证多卡客户月均总价值 <= 200";
        // 表达式对应的值
        Map<String, Object> env = new HashMap<>();
        env.put("一证多卡客户月均总价值", 2);
        env.put("是否近3个月降档客户", "A");
        env.put("统计日_是否生效有价流量合约", "否");
        env.putAll(listMap);

        // 执行表达式，并解析跟踪数据
        List<SubResultBean> srbList = ru.parseTraceOutput(ru.execute(sb.toString(), env));

        // 根因查找
        ru.rootCauseLookup(srbList);
    }

    @Test
    public void test4() {
//        // 表达式
//        String expression = "TG5910010021000561 > 1 && TG5910010021000557 >1";
//        // 表达式对应的值
//        Map<String, Object> env = new HashMap<>();
//        env.put("TG5910010021000561", 2);
//        env.put("TG5910010021000557", 1);
//
//        // 执行表达式，并解析跟踪数据
//        List<SubResultBean> srbList = ru.parseTraceOutput(ru.execute(expression, env));
//
//        // 根因查找
//        ru.rootCauseLookup(srbList);
        String[] args = {
                "是否异网主卡用户202402_总部包含【是】\n" +
                        "且\n" +
                        "近6个月月均套外流量收入大于等于【-2】小于等于【10】"
                , "{\"respResult\":\"1\",\"respData\":[{\"tagId\":\"TG5910010010000834\",\"tagName\":\"是否异网主卡用户202402_总部\",\"tagAlias\":null,\"tagValue\":\"1\",\"tagValueDesc\":\"是\",\"tagValueAlias\":null,\"tagType\":\"4\",\"tagTypeDesc\":\"布尔类型\"},{\"tagId\":\"TG5910010010001423\",\"tagName\":\"近6个月月均套外流量收入\",\"tagAlias\":null,\"tagValue\":\"0.0000\",\"tagValueDesc\":\"0.0000\",\"tagValueAlias\":null,\"tagType\":\"2\",\"tagTypeDesc\":\"数值类型\"}],\"respErrorMsg\":null}"
        };
        MarketTool mt = new MarketTool();
        mt.check(args);

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