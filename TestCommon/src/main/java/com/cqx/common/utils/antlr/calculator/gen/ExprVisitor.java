// Generated from D:/Document/Workspaces/Git/TestSelf/TestCommon/src/main/resources/antlr\Expr.g4 by ANTLR 4.9.1

package com.cqx.common.utils.antlr.calculator.gen;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ExprParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ExprVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ExprParser#cal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCal(ExprParser.CalContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(ExprParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExprParser#mul}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMul(ExprParser.MulContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExprParser#div}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDiv(ExprParser.DivContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExprParser#add}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdd(ExprParser.AddContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExprParser#sub}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSub(ExprParser.SubContext ctx);
	/**
	 * Visit a parse tree produced by {@link ExprParser#num}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNum(ExprParser.NumContext ctx);
}