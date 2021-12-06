parser grammar ExecStatements;

insertStatement
    :   insertClause selectStatement
    ;

insertClause
    :   KW_INSERT KW_INTO KW_STREAM? streamName
    ;

selectStatement
    :   selectClause
//        fromClause
//   		whereClause?
//   		groupByClause?
//  		havingClause?
//   		orderByClause?
//   		limitClause?
//   		parallelClause?
    ;

streamName
    :   cqlIdentifier
    ;

/**
 * 由于担心语法冲突或者其他导致别名失效等原因，暂时只修改函数名称相关
 * 即只修改函数名称中包含的这些关键字。
 * KW_UNIDIRECTION
 */
cqlIdentifier
	:	KW_DAY
	|	KW_HOUR
	|	KW_MINUTES
	|	KW_SECONDS
	|	KW_MILLISECONDS
	|	KW_BOOLEAN
	|	KW_INT
	|	KW_LONG
	|	KW_FLOAT
	|	KW_DOUBLE
	|	KW_STRING
	|	KW_TIMESTAMP
	|	KW_DATE
	|	KW_TIME
	|	KW_DECIMAL
	|	KW_OPERATOR
	|	Identifier
	;

selectClause
    :   KW_SELECT subSelectClause
    ;

subSelectClause
    :   distinct? selectList
    ;

distinct
	:	KW_DISTINCT
	;

selectList
    :	selectItem (COMMA selectItem)*
    ;

selectItem
    :	selectExpression
    ;

selectExpression
    :	expression selectAlias?
    |	streamAllColumns
    ;

selectAlias
	:	multialias
	|	singleAlias
	;

streamAllColumns
    :	(streamName DOT)? STAR
    ;

multialias
	:	KW_AS LPAREN columnALias (COMMA columnALias)* RPAREN
	;

singleAlias
	:	KW_AS? columnALias
	;

columnALias
	:	cqlIdentifier
	;

expression
    :	logicExpressionOr
    ;

logicExpressionOr
    :	logicExpressionAnd (KW_OR logicExpressionAnd)*
    ;

logicExpressionAnd
    :	logicExpressionNot (KW_AND logicExpressionNot)*
    ;

logicExpressionNot
    :	identifierNot? equalRelationExpression
    ;

equalRelationExpression
    :	isNullLikeInExpressions
    ;

isNullLikeInExpressions
	:   binaryExpression
		(KW_IS nullCondition)?
	;

binaryExpression
	:	bitExpression relationExpression*
	;

bitExpression
    :	arithmeticPlusMinusExpression (bitOperator arithmeticPlusMinusExpression)*
    ;

relationExpression
	:	relationOperator bitExpression
	;

arithmeticPlusMinusExpression
    :	arithmeticStarExpression (arithmeticPlusOperator arithmeticStarExpression)*
    ;

arithmeticPlusOperator
    :	PLUS
    |	MINUS
    |	CONCATENATION
    ;

arithmeticStarExpression
    :	fieldExpression (arithmeticStarOperator fieldExpression)*
    ;

identifierNot
	:	KW_NOT
	;

nullCondition
    :	identifierNot? KW_NULL
    ;

bitOperator
	:	BITWISEOR
	|	AMPERSAND
	|	BITWISEXOR
	;

relationOperator
    :	EQUAL
    |	EQUAL_NS
    |	NOTEQUAL
    |	LESSTHANOREQUALTO
    |	LESSTHAN
    |	GREATERTHANOREQUALTO
    |	GREATERTHAN
    ;

fieldExpression
	:	 (streamNameOrAlias DOT)? atomExpression
	;

arithmeticStarOperator
    :	STAR
    |	DIVIDE
    |	MOD
    |	DIV
    ;

streamNameOrAlias
	:	cqlIdentifier
	;

atomExpression
    :	constNull
    |	constant
    |	function
    |	castExpression
    |	columnName
    | 	expressionWithLaparen
    ;

constNull
	:	KW_NULL
	;

constant
    :	unaryOperator?
	    (
	    	constIntegerValue
	    |	constLongValue
	    |	constFloatValue
	    |	constDoubleValue
	    |	constBigDecimalValue
	    )
    |	constStingValue
    |	booleanValue
    ;

function
    :	functionName LPAREN distinct? (selectExpression (COMMA selectExpression)*)?  RPAREN
    ;

castExpression
    :	KW_CAST LPAREN expression KW_AS  primitiveType RPAREN
    ;

columnName
	:	cqlIdentifier
	;

expressionWithLaparen
	:	LPAREN expression RPAREN
	;

/*
	位操作符
	正，负等操作符号
*/
unaryOperator
	:	PLUS
	|	MINUS
	|	TILDE
	;

constStingValue
	:	StringLiteral
	;

constIntegerValue
	:	IntegerNumber
	;

constLongValue
	:	LongLiteral
	;

constFloatValue
	:	FloatLiteral
	;

constDoubleValue
	:	DoubleLiteral
	;

constBigDecimalValue
	:	DecimalLiteral
	;

booleanValue
    :	KW_TRUE
    |	KW_FALSE
    ;

functionName
	:	cqlIdentifier
	;

primitiveType
    :	KW_INT
    |	KW_LONG
    |	KW_BOOLEAN
    |	KW_FLOAT
    |	KW_DOUBLE
    |	KW_STRING
    |	KW_TIMESTAMP
    |	KW_DATE
    |	KW_TIME
    |	KW_DECIMAL
    ;


