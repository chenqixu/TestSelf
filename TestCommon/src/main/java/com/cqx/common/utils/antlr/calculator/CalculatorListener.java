package com.cqx.common.utils.antlr.calculator;

import com.cqx.common.utils.antlr.calculator.gen.ExprBaseListener;
import com.cqx.common.utils.antlr.calculator.gen.ExprParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

/**
 * 使用监听器遍历语法分析树
 *
 * @author chenqixu
 */
public class CalculatorListener extends ExprBaseListener {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorListener.class);
    private CalculatorBaseUtil baseUtil = new CalculatorBaseUtil();
    // 定义一个栈（先进后出），存放中间计算结果
    private Stack<Integer> result = new Stack<>();

    public int getResult() {
        // 将最后的结果返回
        return result.pop();
    }

    @Override
    public void exitExpr(ExprParser.ExprContext ctx) {
        // 可以获取到规则，就进行计算
        ParserRuleContext parserRuleContext = baseUtil.get(ctx);
        if (parserRuleContext != null) {
            // 右边的值会先出栈
            Integer right = result.pop();
            Integer left = result.pop();
            if (right != null && left != null) {
                Integer res = baseUtil.expr(ctx, left, right);
                logger.debug("right：{}，left：{}，result：{}", right, left, res);
                // 再将计算后的值放入栈中
                result.push(res);
            }
        }
    }

    @Override
    public void exitMul(ExprParser.MulContext ctx) {
        baseUtil.addOp(ctx);
    }

    @Override
    public void exitDiv(ExprParser.DivContext ctx) {
        baseUtil.addOp(ctx);
    }

    @Override
    public void exitAdd(ExprParser.AddContext ctx) {
        baseUtil.addOp(ctx);
    }

    @Override
    public void exitSub(ExprParser.SubContext ctx) {
        baseUtil.addOp(ctx);
    }

    @Override
    public void exitNum(ExprParser.NumContext ctx) {
        // 将INT的值放入栈中
        result.push(Integer.valueOf(ctx.getText()));
    }
}
