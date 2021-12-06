package com.cqx.common.utils.antlr.calculator;

import com.cqx.common.utils.antlr.calculator.gen.ExprBaseListener;
import com.cqx.common.utils.antlr.calculator.gen.ExprParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用标注来遍历语法分析树
 * <p>
 * 这个树不是很好用，暂时没有补充完整
 *
 * @author chenqixu
 */
public class CalculatorWithProps extends ExprBaseListener {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorWithProps.class);
    private CalculatorBaseUtil baseUtil = new CalculatorBaseUtil();
    // 使用Map<ParseTree, Integer>将节点映射到对应的结果值
    private ParseTreeProperty<Integer> result = new ParseTreeProperty<>();

    public void setValues(ParseTree node, int value) {
        result.put(node, value);
    }

    public int getValues(ParseTree node) {
        return result.get(node);
    }

    @Override
    public void exitCal(ExprParser.CalContext ctx) {
        // 将表达式放入树中
//        setValues(ctx, getValues(ctx.expr()));
    }

    @Override
    public void exitExpr(ExprParser.ExprContext ctx) {
        int rule_size = ctx.expr().size();
        logger.info("expr，rule_size：{}", rule_size);
        for (ExprParser.ExprContext exprContext : ctx.expr()) {
            logger.info("ExprContext：{}", baseUtil.objToString(exprContext.getChild(0)));
        }
    }

    @Override
    public void exitMul(ExprParser.MulContext ctx) {
    }

    @Override
    public void exitDiv(ExprParser.DivContext ctx) {
//        baseUtil.addOp(ctx);
    }

    @Override
    public void exitAdd(ExprParser.AddContext ctx) {
        logger.info("exitAdd {}", baseUtil.objToString(ctx));
//        baseUtil.addOp(ctx);
        // 子树节点有三个，两个操作数和一个操作符，1 + 2
//        int left = getValues(ctx.getChild(0));
//        int right = getValues(ctx.getChild(2));
//        setValues(ctx, left + right);
    }

    @Override
    public void exitSub(ExprParser.SubContext ctx) {
//        baseUtil.addOp(ctx);
    }

    @Override
    public void exitNum(ExprParser.NumContext ctx) {
        logger.info("exitNum {}", baseUtil.objToString(ctx));
        // 放入树中
        setValues(ctx, Integer.valueOf(ctx.getText()));
    }
}
