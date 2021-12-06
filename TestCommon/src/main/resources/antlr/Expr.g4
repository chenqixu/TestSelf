grammar Expr;

@header {
package com.cqx.common.utils.antlr.calculator.gen;
}

cal : expr;

expr : '(' expr ')'
       | expr mul expr
       | expr div expr
       | expr add expr
       | expr sub expr
       | num
       ;
mul : MUL;
div : DIV;
add : ADD;
sub : SUB;
num : INT;

MUL : '*';
DIV : '/';
ADD : '+';
SUB : '-';
INT : '0' | [1-9][0-9]*;

NEWLINE : '\r'?'\n';
WS : [ \t\n] -> skip;