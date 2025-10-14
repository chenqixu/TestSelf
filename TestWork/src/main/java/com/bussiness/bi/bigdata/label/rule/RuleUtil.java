package com.bussiness.bi.bigdata.label.rule;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.Options;
import com.googlecode.aviator.lexer.token.OperatorType;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * 规则工具
 *
 * @author chenqixu
 */
public class RuleUtil {
    // 正则表达式匹配整数和小数（含正负数）
    private static final String NUMBER_PATTERN = "^-?\\d+(\\.\\d+)?$";
    // 标签id和标签名称的映射
    private Map<String, String> tagMap;
    // 结果（是否有根因分析）
    private AtomicBoolean result = new AtomicBoolean(true);

    /**
     * 检查字符串是否表示有效的数字（整数或小数）
     *
     * @param str 要检查的字符串
     * @return true表示有效数字，false表示无效
     */
    public static boolean isNumericS(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return Pattern.matches(NUMBER_PATTERN, str);
    }

    /**
     * 解析跟踪输出的方法
     */
    public List<SubResultBean> parseTraceOutput(ByteArrayOutputStream traceStream) {
        // 转换为字符串
        String traceLog = new String(traceStream.toByteArray());
        System.out.println("\n=== TRACE日志解析结果 ===");

        // 按行分割日志
        String[] lines = traceLog.split("\\r?\\n");

        // 叶子节点的栈，先进后出
        Stack<SubResultBean> leafStack = new Stack<>();
        // 序号
        AtomicInteger seq = new AtomicInteger(1);
        // 每一层操作结果
        List<SubResultBean> srbList = new ArrayList<>();

        // 具体日志
        System.out.println("[具体日志]\n" + traceLog);
        // 解析每行日志
        for (String line : lines) {
            if (line.trim().contains("Result :")) {
            } else if (line.trim().isEmpty() || // 跳过无意义行
                    line.startsWith("AVIATOR: [") ||
                    // 没有计算结果
                    !line.contains("=> <Boolean")) {
                continue;
            }

            // 解析关键信息（格式示例："[TRACE] 进入表达式: (a == 1)"）
            String key = "[Aviator TRACE]";
            if (line.startsWith(key)) {
                String message = line.substring(key.length()).trim(); // 移除"[TRACE] "

                // 分解关键元素
                int colonPos = message.indexOf("=>");
                String action = colonPos > 0 ? message.substring(0, colonPos) : "";
                // 截取第一个>
                // 截取最后一个<
                int firstEndPos = action.indexOf(">");
                int lastStartPos = action.lastIndexOf("<");
                String op = action;
                String sub1 = "";
                String sub2 = "";
                SubTypeBean subTypeBean1 = null;
                SubTypeBean subTypeBean2 = null;
                if (firstEndPos >= 0 && lastStartPos >= 0) {
                    op = action.substring(firstEndPos + 1, lastStartPos);
                    // 得看是JavaType（4个），还是基本类型（2个）
                    // JavaType：类型，名称，对象自身的值，值类型
                    // 基本类型：类型，对象自身的值

                    // 对象1
                    sub1 = action.substring(0, firstEndPos + 1);
                    String[] sub1arr = subDeal(sub1);
                    subTypeBean1 = subtypeDeal(sub1arr);

                    // 对象2
                    sub2 = action.substring(lastStartPos);
                    String[] sub2arr = subDeal(sub2);
                    subTypeBean2 = subtypeDeal(sub2arr);
                }
                String details = colonPos > 0 ? message.substring(colonPos + 2).trim() : message;

                // 打印解析结果
                if (op.trim().length() > 0 && subTypeBean1 != null) {
                    String[] detailsarr = subDeal(details);
                    SubTypeBean subTypeBeandetail = subtypeDeal(detailsarr);
                    if (subTypeBean1.getName() != null) {
                        // 叶子节点，进栈
                        SubResultBean srb = new SubResultBean(subTypeBean1, subTypeBean2
                                , op, Boolean.valueOf(subTypeBeandetail.getVal()), seq.getAndIncrement());
                        leafStack.push(srb);
                        srbList.add(srb);
                        System.out.printf("%s操作符: %-5s | 具体操作: %s 计算结果: %s | %s%n"
                                , srb.getLevel(), parseAction(op), action, details, srb.getLeaf());
                    } else {
                        // 叶子节点出栈
                        SubResultBean srb2 = leafStack.pop();
                        SubResultBean srb1 = leafStack.pop();
                        // 非叶子节点，进栈
                        SubResultBean srb = new SubResultBean(srb1, srb2, op
                                , Boolean.valueOf(subTypeBeandetail.getVal())
                                , seq.getAndIncrement());
                        leafStack.push(srb);
                        srbList.add(srb);
//                        String parent = String.format("(%s) %s (%s) = %s", srb1.getLevelAndRs()
//                                , op, srb2.getLevelAndRs(), subTypeBeandetail.getVal());
                        System.out.printf("%s操作符: %-5s | 具体操作: %s 计算结果: %s | %s%n"
                                , srb.getLevel(), parseAction(op), action, details, srb.getParent());
                    }
                } else {
                    System.out.printf("结果: %s%n", details);
                }

            }
        }
        return srbList;
    }

    /**
     * 根因查找
     *
     * @param srbList
     */
    public void rootCauseLookup(List<SubResultBean> srbList) {
        // 找到问题的层级
        SubResultBean s1 = null;
        for (int i = srbList.size() - 1; i >= 0; i--) {
            SubResultBean srb = srbList.get(i);
            if (srb.isResult()) break;
//            System.out.printf("%s%s%n", srb.getLevel(), srb.isResult());
            s1 = srb;
        }
        // 确认是主因还是要深入排查
        if (s1 != null) {
            if (result.getAndSet(false)) {
            }
            System.out.println("=== 根因分析 ===");
            rootCauseLookupCheck(s1);
        }
    }

    private void rootCauseLookupCheck(SubResultBean s1) {
        // 判断是不是叶子
        if (!s1.isLeaf()) {
            // 判断是s1导致 还是 s2导致
//            System.out.printf("%s%s %s%s%n", s1.getS1().getLevel(), s1.getS1().isResult()
//                    , s1.getS2().getLevel(), s1.getS2().isResult());
            if (!s1.getS1().isResult()) {
                rootCauseLookupCheck(s1.getS1());
            }
            if (!s1.getS2().isResult()) {
                rootCauseLookupCheck(s1.getS2());
            }
        } else {
            System.out.printf("%s%n", s1.getLeaf(this.tagMap));
        }
    }


    private SubTypeBean subtypeDeal(String[] arr) {
        switch (arr[0]) {
            case "JavaType":
            case "Boolean":
            case "Long":
            case "String":
                return new SubTypeBean(arr);
            default:
                return new SubTypeBean();
        }
    }

    /**
     * sub字符处理
     *
     * @param str
     * @return
     */
    private String[] subDeal(String str) {
        // 步骤1：去左右尖括号
        str = str.trim();
        str = str.substring(1, str.length() - 1);
        // 步骤2：按,分隔
        return str.split(",", -1);
    }

    /**
     * 翻译跟踪动作
     */
    public static String parseAction(String action) {
        switch (action.trim()) {
            case ">":
                return "大于";
            case "<":
                return "小于";
            case "=":
            case "==":
                return "等于";
            case ">=":
                return "大于等于";
            case "<=":
                return "小于等于";
            case "&&":
                return "且";
            case "||":
                return "或";
            case "=~":
                return "匹配";
            default:
                return action;
        }
    }

    /**
     * 执行表达式
     *
     * @param expression
     * @param env
     * @return
     */
    public ByteArrayOutputStream execute(String expression, Map<String, Object> env) {
        // debug模式
        AviatorEvaluator.setOption(Options.TRACE_EVAL, true);
        // 重写操作符：=~ ，替代in，但是表达式中还是必须使用=~
        AviatorEvaluator.addOpFunction(OperatorType.MATCH, new AbstractFunction() {
            @Override
            public AviatorObject call(final Map<String, Object> env, final AviatorObject arg1, final AviatorObject arg2) {
                System.out.printf("arg1=%s, arg2=%s%n", arg1, arg2);
                boolean flag;
                Object val1Obj = env.get(((AviatorJavaType) arg1).getName());
                String val1 = val1Obj != null ? val1Obj.toString() : null;
                if (isNumericS(val1)) {
                    Number val = FunctionUtils.getNumberValue(arg1, env);
                    Class valClass = val.getClass();
                    Object _listVal = FunctionUtils.getJavaObject(arg2, env);
                    List listVal = (List) _listVal;
                    List<Number> val2 = new ArrayList<>();
                    switch (valClass.getSimpleName()) {
                        case "Integer":
                            for (Object obj : listVal) {
                                val2.add(Integer.valueOf(obj.toString().trim()));
                            }
                            break;
                        case "Double":
                            for (Object obj : listVal) {
                                val2.add(Double.valueOf(obj.toString().trim()));
                            }
                            break;
                        default:
                            break;
                    }
                    flag = val2.contains(val);
                    System.out.printf("[操作符~=] arg1=%s, arg2=%s, arg1_val=%s, arg2_val=%s, calc=%s %n", arg1, arg2, val, listVal, flag);
                } else {
                    String val = FunctionUtils.getStringValue(arg1, env);
                    Object _listVal = FunctionUtils.getJavaObject(arg2, env);
                    List listVal = (List) _listVal;
                    List<String> val2 = new ArrayList<>();
                    for (Object obj : listVal) {
                        val2.add(obj.toString().trim());
                    }
                    flag = val2.contains(val);
                    System.out.printf("[操作符~=] arg1=%s, arg2=%s, arg1_val=%s, arg2_val=%s, calc=%s %n", arg1, arg2, val, listVal, flag);
                }
                return AviatorBoolean.valueOf(flag);
            }

            @Override
            public String getName() {
                return "in";
            }
        });

        // 创建临时ByteArrayOutputStream接收跟踪数据
        ByteArrayOutputStream traceOutput = new ByteArrayOutputStream();
        AviatorEvaluator.setTraceOutputStream(traceOutput);

        System.out.printf("表达式：%s%n", expression);
        Expression compiledExp = AviatorEvaluator.compile(expression);
        compiledExp.execute(env);

        return traceOutput;
    }

    public void setTagMap(Map<String, String> tagMap) {
        this.tagMap = tagMap;
    }

    public Boolean getResult() {
        return result.get();
    }
}
