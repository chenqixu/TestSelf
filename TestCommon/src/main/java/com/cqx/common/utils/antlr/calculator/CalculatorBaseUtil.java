package com.cqx.common.utils.antlr.calculator;

import com.cqx.common.utils.antlr.calculator.gen.ExprParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 计算器基础工具
 *
 * @author chenqixu
 */
public class CalculatorBaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorBaseUtil.class);
    private Map<ExprParser.ExprContext, ParserRuleContext> exprRuleMap = new HashMap<>();

    /**
     * 增加操作符，返回null，要到聚合阶段才会真正操作
     *
     * @param ctx
     */
    public Integer addOp(ParserRuleContext ctx) {
        RuleContext ruleContext = ctx.parent;
        if (ruleContext instanceof ExprParser.ExprContext) {
            ExprParser.ExprContext _ruleContext = (ExprParser.ExprContext) ruleContext;
            exprRuleMap.put(_ruleContext, ctx);
            logger.debug("[增加操作符] {}", objToString(_ruleContext));
        }
        return null;
    }

    /**
     * 获取expr对应的规则
     *
     * @param node
     * @return
     */
    public ParserRuleContext get(ExprParser.ExprContext node) {
        return exprRuleMap.get(node);
    }

    /**
     * 移除已经计算过的规则
     *
     * @param node
     */
    public void remove(ExprParser.ExprContext node) {
        exprRuleMap.remove(node);
    }

    /**
     * 计算结果
     *
     * @param _node
     * @param aggregate
     * @param nextResult
     * @return
     */
    public Integer expr(ExprParser.ExprContext _node, Integer aggregate, Integer nextResult) {
        // 获取标记
        ParserRuleContext parserRuleContext = get(_node);
        // 加法
        if (parserRuleContext instanceof ExprParser.AddContext) {
            // 移除标记
            remove(_node);
            // 返回计算结果
            return aggregate + nextResult;
        }
        // 减法
        else if (parserRuleContext instanceof ExprParser.SubContext) {
            // 移除标记
            remove(_node);
            // 返回计算结果
            return aggregate - nextResult;
        }
        // 乘法
        else if (parserRuleContext instanceof ExprParser.MulContext) {
            // 移除标记
            remove(_node);
            // 返回计算结果
            return aggregate * nextResult;
        }
        // 除法
        else if (parserRuleContext instanceof ExprParser.DivContext) {
            // 移除标记
            remove(_node);
            // 返回计算结果
            return aggregate / nextResult;
        }
        return null;
    }

    /**
     * 打印对象
     *
     * @param obj
     * @return
     */
    public String objToString(Object obj) {
        return obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode());
    }
}
