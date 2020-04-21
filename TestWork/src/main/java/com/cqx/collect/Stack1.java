package com.cqx.collect;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.util.Stack;

/**
 * Stack1
 * <pre>
 *     如果没有右括号，这个算法就不成立
 * </pre>
 *
 * @author chenqixu
 */
public class Stack1 {
    private static final MyLogger logger = MyLoggerFactory.getLogger(Stack1.class);

    /**
     * 栈测试
     */
    public void stackTest() {
        Stack<Integer> s = new Stack<>();
        logger.info("push-------------------------------");
        for (int i = 0; i < 6; i++) {
            s.push(i);
        }
        for (int i : s) {
            logger.info(i);
        }
        logger.info("pop-------------------------------");
        while (!s.empty()) {
            logger.info(s.pop());
        }
    }

    private void evaluate(Stack<String> ops, Stack<Double> vals, String val) {
        logger.debug("evaluate，val：{}", val);
        //运算符压入 操作符栈
        if (val.equals("(")) ;//忽略左括号，其他运算符压入运算符栈
        else if (val.equals("+")) ops.push(val);
        else if (val.equals("-")) ops.push(val);
        else if (val.equals("*")) ops.push(val);
        else if (val.equals("/")) ops.push(val);
        else if (val.equals(")")) {
            //遇到右括号开始计算
            String op = ops.pop();
            double v_right = vals.pop();
            double v_left = vals.pop();
            double v_eva = 0;
            logger.debug("遇到右括号开始计算，op：{}，v_left：{}，v_right：{}", op, v_left, v_right);
            if (op.equals("+")) v_eva = v_left + v_right;
            if (op.equals("-")) v_eva = v_left - v_right;
            if (op.equals("*")) v_eva = v_left * v_right;
            if (op.equals("/")) v_eva = v_left / v_right;
            logger.debug("计算结果推入操作数栈，v_eva：{}", v_eva);
            vals.push(v_eva);
        } else {
            //数字压入 操作数栈
            vals.push(Double.parseDouble(val));
            logger.debug("数字压入 操作数栈：{}", val);
        }
    }

    /**
     * 迪克斯特拉双栈算术表达式求值算法
     *
     * @param tmp
     * @return
     */
    public double evaluate(String tmp) {
        //符栈
        Stack<String> ops = new Stack<>();
        //数栈
        Stack<Double> vals = new Stack<>();
        //清空不必要的空格
        String evl = tmp.replaceAll(" ", "");
        //当前值先暂存起来，需要判断下一个才能操作
        //当前值的类型和上一个值类型不一致，就可以进行切换
        //当前值是最后一个，也需要切换
        EvalBean current = new EvalBean();
        for (int i = 0; i < evl.length(); i++) {
            String s = String.valueOf(evl.charAt(i));
            boolean tag = true;
            if (i == 0) {
                tag = false;
            }
            switch (current.checkType(s)) {
                case OP:
                    if (tag && current.hasVal()) {
                        //值
                        evaluate(ops, vals, current.getVal());
                        current = new EvalBean();
                    }
                    //操作符
                    evaluate(ops, vals, s);
                    break;
                case VAL:
                    current.addVal(s);
                    break;
            }
            //最后一次数据入栈操作
            if (i + 1 == evl.length() && ops.size() > 0 && current.hasVal()) {
                evaluate(ops, vals, current.getVal());
                current = new EvalBean();
            }
        }
        //最后一次操作
        if (ops.size() > 0) {
            logger.debug("ops：{}，vals：{}", ops, vals);
            evaluate(ops, vals, ")");
        }
        Double result = vals.pop();
        logger.info("result：{}", result);
        return result;
    }

    enum EvalType {
        OP, VAL,
        ;
    }

    class EvalBean {
        String val = "";

        private boolean hasVal() {
            return val.length() > 0;
        }

        private String getVal() {
            return val;
        }

        private void addVal(String val) {
            this.val = this.val + val;
            logger.debug("addVal：{}", this.val);
        }

        private EvalType checkType(String val) {
            if (val.equals("+") || val.equals("-") || val.equals("*") || val.equals("/") || val.equals("(") || val.equals(")")) {
                return EvalType.OP;
            } else {
                return EvalType.VAL;
            }
        }
    }
}
