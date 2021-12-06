parser grammar SQLParser;

options {
tokenVocab=SQLLexer;
}

import DdlStatements,ExecStatements;

@header {
package com.cqx.common.utils.antlr.sqlparser.gen;
}

statement
    : ddlStatement EOF
    | execStatement EOF
    ;

ddlStatement
    : createInputStreamStatement
    ;

execStatement
    : insertStatement
    ;