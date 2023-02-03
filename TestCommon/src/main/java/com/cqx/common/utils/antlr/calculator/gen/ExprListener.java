// Generated from D:/Document/Workspaces/Git/TestSelf/TestCommon/src/main/resources/antlr\Expr.g4 by ANTLR 4.9.1

package com.cqx.common.utils.antlr.calculator.gen;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExprParser}.
 */
public interface ExprListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExprParser#cal}.
	 * @param ctx the parse tree
	 */
	void enterCal(ExprParser.CalContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#cal}.
	 * @param ctx the parse tree
	 */
	void exitCal(ExprParser.CalContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(ExprParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(ExprParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#mul}.
	 * @param ctx the parse tree
	 */
	void enterMul(ExprParser.MulContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#mul}.
	 * @param ctx the parse tree
	 */
	void exitMul(ExprParser.MulContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#div}.
	 * @param ctx the parse tree
	 */
	void enterDiv(ExprParser.DivContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#div}.
	 * @param ctx the parse tree
	 */
	void exitDiv(ExprParser.DivContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#add}.
	 * @param ctx the parse tree
	 */
	void enterAdd(ExprParser.AddContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#add}.
	 * @param ctx the parse tree
	 */
	void exitAdd(ExprParser.AddContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#sub}.
	 * @param ctx the parse tree
	 */
	void enterSub(ExprParser.SubContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#sub}.
	 * @param ctx the parse tree
	 */
	void exitSub(ExprParser.SubContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#num}.
	 * @param ctx the parse tree
	 */
	void enterNum(ExprParser.NumContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#num}.
	 * @param ctx the parse tree
	 */
	void exitNum(ExprParser.NumContext ctx);
}