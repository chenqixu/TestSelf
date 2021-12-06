package com.cqx.common.utils.antlr.calculator;

import com.cqx.common.utils.antlr.calculator.gen.ExprLexer;
import com.cqx.common.utils.antlr.calculator.gen.ExprParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 计算器工具
 *
 * @author chenqixu
 */
public class CalculatorUtil {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorUtil.class);
    private ExprParser parser;
    private ParseTree parseTree;

    public CalculatorUtil(String rule) {
        logger.info("rule：{}", rule);
        // 将输入转成antlr的input流
        CharStream inputStream = CharStreams.fromString(rule);
        // 旧版本的转换
//        ANTLRInputStream inputStream = new ANTLRInputStream(rule);
        // 词法分析
        ExprLexer lexer = new ExprLexer(inputStream);
        // 转成token流
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        // 语法分析
        parser = new ExprParser(tokenStream);
        // 获取某一个规则树，这里获取的是最外层的规则
        parseTree = parser.cal();
        // 打印规则树
        logger.info("parseTree：{}", parseTree.toStringTree(parser));
    }

    public void visitor() {
        // 使用访问器遍历语法分析树
        CalculatorVisitor visitor = new CalculatorVisitor(parser);
        // 执行并返回结果
        int result = visitor.visit(parseTree);
        logger.info("Visitor calculate result：{}", result);
    }

    public void listener() {
        ParseTreeWalker walker = new ParseTreeWalker();
        CalculatorListener listener = new CalculatorListener();
        walker.walk(listener, parseTree);
        int result = listener.getResult();
        logger.info("Listener calculate result：{}", result);
    }

    public void props() {
        ParseTreeWalker walker = new ParseTreeWalker();
        CalculatorWithProps props = new CalculatorWithProps();
        walker.walk(props, parseTree);
        int result = props.getValues(parseTree);
        logger.info("ParseTreeProperty calculate result：{}", result);
    }
}
