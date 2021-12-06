package com.cqx.common.utils.antlr.calculator;

import com.cqx.common.utils.antlr.calculator.gen.ExprBaseVisitor;
import com.cqx.common.utils.antlr.calculator.gen.ExprParser;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用访问器遍历语法分析树
 *
 * @author chenqixu
 */
public class CalculatorVisitor extends ExprBaseVisitor<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorVisitor.class);
    private Parser parser;
    private CalculatorBaseUtil baseUtil = new CalculatorBaseUtil();

    /**
     * 构造，用于打印解析树
     *
     * @param parser
     */
    public CalculatorVisitor(Parser parser) {
        this.parser = parser;
    }

    /**
     * Cal计算
     *
     * @param ctx
     * @return
     */
    @Override
    public Integer visitCal(ExprParser.CalContext ctx) {
//        printPRC(ctx);
        // 访问子节点
        logger.debug("visitCal-访问子节点");
        return visitChildren(ctx);
    }

    /**
     * Expr计算
     *
     * @param ctx
     * @return
     */
    @Override
    public Integer visitExpr(ExprParser.ExprContext ctx) {
//        printPRC(ctx);
        // 访问子节点
        logger.debug("visitExpr-访问子节点");
        return visitChildren(ctx);
    }

    /**
     * Mul操作符
     *
     * @param ctx
     * @return
     */
    @Override
    public Integer visitMul(ExprParser.MulContext ctx) {
        return baseUtil.addOp(ctx);
    }

    /**
     * Div操作符
     *
     * @param ctx
     * @return
     */
    @Override
    public Integer visitDiv(ExprParser.DivContext ctx) {
        return baseUtil.addOp(ctx);
    }

    /**
     * Add操作符
     *
     * @param ctx
     * @return
     */
    @Override
    public Integer visitAdd(ExprParser.AddContext ctx) {
        return baseUtil.addOp(ctx);
    }

    /**
     * Sub操作符
     *
     * @param ctx
     * @return
     */
    @Override
    public Integer visitSub(ExprParser.SubContext ctx) {
        return baseUtil.addOp(ctx);
    }

    /**
     * Num的实现
     *
     * @param ctx
     * @return
     */
    @Override
    public Integer visitNum(ExprParser.NumContext ctx) {
        logger.debug("visitNum-获取值");
        return Integer.valueOf(ctx.getText());
    }

    /**
     * 重写了观察子节点的方法，改变了聚合结果的传参
     *
     * @param node
     * @return
     */
    @Override
    public Integer visitChildren(RuleNode node) {
        logger.debug("[visitChildren] node.getText()：{}，node：{}", node.getText(), baseUtil.objToString(node));
        Integer result = defaultResult();
        int n = node.getChildCount();
        for (int i = 0; i < n; i++) {
            if (!shouldVisitNextChild(node, result)) {
                break;
            }

            ParseTree c = node.getChild(i);
            logger.debug("visitChildren[{}] 表达式：{}，parent：{}，node：{}，nodeText：{}"
                    , i, c.toStringTree(parser), baseUtil.objToString(node), baseUtil.objToString(c), c.getText());

            // accept，要么visitCal，要么visitChildren
            // 那应该是要实现visitCal？
            // 对于visitCall来说，就是visitChildren
            // 对于visitExpr来说，也是visitChildren
            Integer childResult = c.accept(this);
            logger.debug("[visitChildren.aggregateResult] node.getText：{}，node：{}，c：{}，result：{}，childResult：{}"
                    , node.getText(), baseUtil.objToString(node), baseUtil.objToString(c), result, childResult);
            // 聚合结果
            result = aggregateResult(node, result, childResult);
        }
        return result;
    }

    /**
     * 聚合结果
     *
     * @param node
     * @param aggregate
     * @param nextResult
     * @return
     */
    private Integer aggregateResult(RuleNode node, Integer aggregate, Integer nextResult) {
        logger.debug("[node] {}, [aggregate] {}, [nextResult] {}", baseUtil.objToString(node), aggregate, nextResult);
        // 如果都有值，并且node是Expr，获取标记，进行计算
        if (aggregate != null && nextResult != null && node instanceof ExprParser.ExprContext) {
            ExprParser.ExprContext _node = (ExprParser.ExprContext) node;
            Integer result = baseUtil.expr(_node, aggregate, nextResult);
            if (result != null) return result;
        }
        // 默认返回非空的值
        return nextResult == null ? aggregate : nextResult;
    }

    /**
     * 打印解析规则下的解析树
     *
     * @param ctx
     */
    private void printPRC(ParserRuleContext ctx) {
        int cc = ctx.getChildCount();
        if (cc > 0) {
            for (ParseTree parseTree : ctx.children) {
                printPT(parseTree);
            }
        } else {
            logger.info("[ctx] {}", ctx.getText());
        }
    }

    /**
     * 打印解析树
     *
     * @param parseTree
     */
    private void printPT(ParseTree parseTree) {
        int cc = parseTree.getChildCount();
        if (cc > 0) {
            for (int i = 0; i < cc; i++) {
                printPT(parseTree.getChild(i));
            }
        } else {
            logger.info("[parseTree] {}", parseTree.getText());
        }
    }
}
