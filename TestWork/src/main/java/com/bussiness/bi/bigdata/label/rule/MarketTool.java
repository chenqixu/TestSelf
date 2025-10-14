package com.bussiness.bi.bigdata.label.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 *
 * @author chenqixu
 */
public class MarketTool {

    public void check(String[] args) {
        // 需要两个参数，一个是解析规则（从前端界面拷贝），一个是对应标签值（可以从画像接口查询），第三个是列表对象
        if (args.length == 2) {
            String expressionArg = args[0];
            String valueJSON = args[1];

            JSONObject obj = (JSONObject) JSON.parse(valueJSON);
            JSONArray ja = obj.getJSONArray("respData");
            List<TagBean> tagBeanList = new ArrayList<>();
            Map<String, String> tagMap = new HashMap<>();

            Map<String, Object> env = new HashMap<>();
            for (int i = 0; i < ja.size(); i++) {
                String tagId = ja.getJSONObject(i).getString("tagId").trim();
                String tagName = ja.getJSONObject(i).getString("tagName").trim();
                String tagValueDesc = ja.getJSONObject(i).getString("tagValueDesc").trim();
                if (tagValueDesc.isEmpty()) {
                    env.put(tagId, null);
                } else if (RuleUtil.isNumericS(tagValueDesc)) {
                    env.put(tagId, ja.getJSONObject(i).getBigDecimal("tagValueDesc"));
                } else {
                    env.put(tagId, tagValueDesc);
                }
                tagBeanList.add(new TagBean(tagId, tagName));
                tagMap.put(tagId, tagName);
            }
            // 从最长的开始排序
            tagBeanList.sort(new Comparator<TagBean>() {
                @Override
                public int compare(TagBean o1, TagBean o2) {
                    /*
                      负整数、零或正整数作为第一个参数小于、等于或大于第二个参数。
                     */
                    return Integer.compare(o1.getTagName().length(), o2.getTagName().length());
                }
            });

            Map<String, List> listMap = new HashMap<>();
            AtomicInteger seq = new AtomicInteger(0);

            String[] expressions = expressionArg.split("\n", -1);
            StringBuilder sb = new StringBuilder();
            for (String expression : expressions) {
                for (TagBean tagBean : tagBeanList) {
                    if (expression.contains(tagBean.getTagName())) {
                        expression = expression.replace(tagBean.getTagName(), tagBean.getTagId());
                        break;
                    }
                }

                // 小于等于 大于等于
                // 小于等于 大于
                // 大于等于 小于
                // 小于 大于
                if (expression.contains("小于等于") && expression.contains("大于等于")) {
                    expression = comparisonValueDeal(expression, "小于等于", "<=", "大于等于", ">=");
                } else if (expression.contains("小于等于") && expression.contains("大于")) {
                    expression = comparisonValueDeal(expression, "小于等于", "<=", "大于", ">");
                } else if (expression.contains("小于") && expression.contains("大于等于")) {
                    expression = comparisonValueDeal(expression, "小于", "<", "大于等于", ">=");
                } else if (expression.contains("小于") && expression.contains("大于")) {
                    expression = comparisonValueDeal(expression, "小于", "<", "大于", ">");
                }
                if (expression.contains("包含")) {
                    String val = expression.substring(expression.indexOf("【") + "【".length(), expression.indexOf("】"));
                    String _splitStr = null;
                    if (val.contains(",")) {
                        _splitStr = ",";
                    } else if (val.contains("，")) {
                        _splitStr = "，";
                    }
                    if (_splitStr != null) {
                        String mapKey = "list" + seq.incrementAndGet();
                        String[] vals = val.split(_splitStr, -1);
                        boolean isNumeric = false;
                        for (String _v : vals) {
                            if (!RuleUtil.isNumericS(_v)) {
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
                    } else {
                        // 字符串必须使用引号扩起来
                        if (!RuleUtil.isNumericS(val)) {
                            expression = expression.replace(val, "'" + val + "'");
                        }
                    }
                }
                expression = expression.replace("包含", " == ");
                expression = expression.replace("小于等于", " <= ");
                expression = expression.replace("大于等于", " >= ");
                expression = expression.replace("小于", " < ");
                expression = expression.replace("大于", " > ");
                expression = expression.replace("且", " && ");
                expression = expression.replace("或", " || ");
//                expression = expression.replace("【是】", "1");
//                expression = expression.replace("【否】", "0");
//                expression = expression.replace("【是】", "【'是'】");
//                expression = expression.replace("【否】", "【'否'】");
                expression = expression.replace("【", "");
                expression = expression.replace("】", "");
                expression = expression.replace("等于", " == ");
                sb.append(expression);
            }

            // 增加list参数
            env.putAll(listMap);
            System.out.printf("【参数】%s%n", env);

            RuleUtil ru = new RuleUtil();
            // 设置标签id、标签名称的映射
            ru.setTagMap(tagMap);

            // 执行表达式，并解析跟踪数据
            List<SubResultBean> srbList = ru.parseTraceOutput(ru.execute(sb.toString(), env));

            // 根因查找
            ru.rootCauseLookup(srbList);
            if (!ru.getResult()) {
                System.exit(-1);
            }
        } else {
            System.out.println("参数不满足！需要两个参数，一个是解析规则（从前端界面拷贝），一个是对应标签值（可以从画像接口查询）。");
            System.exit(-1);
        }
    }

    public String comparisonValueDeal(String expression
            , String key1, String key1v, String key2, String key2v) {
        int index1 = expression.indexOf(key1);
        int index2 = expression.indexOf(key2);
        if (index2 < index1) {
            // 中台_套餐基本费_日大于等于【38000】小于等于【30000000】
            // key2在前
            String key = expression.substring(0, expression.indexOf(key2)).trim();
            key = key.replace("(", "");
            expression = expression.replaceAll(key2, key2v);
            expression = expression.replaceAll(key1, " && " + key + key1v);
        } else {
            // 中台_套餐基本费_日小于等于【30000000】大于等于【38000】
            // key1在前
            String key = expression.substring(0, expression.indexOf(key1)).trim();
            key = key.replace("(", "");
            expression = expression.replaceAll(key1, key1v);
            expression = expression.replaceAll(key2, " && " + key + key2v);
        }
        return expression;
    }

    public static void main(String[] args) {
        new MarketTool().check(args);
    }
}
