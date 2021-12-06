package com.cqx.common.utils.antlr;

import com.cqx.common.utils.antlr.calculator.CalculatorUtil;

/**
 * AntlrUtil
 *
 * @author chenqixu
 */
public class AntlrUtil {

    public void exprParser() {
        String rule = "1+2";
        CalculatorUtil calculatorUtil = new CalculatorUtil(rule);
//        calculatorUtil.visitor();
//        calculatorUtil.listener();
        calculatorUtil.props();
    }
}
