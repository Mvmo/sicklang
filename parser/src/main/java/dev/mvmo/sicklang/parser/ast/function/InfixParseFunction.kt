package dev.mvmo.sicklang.parser.ast.function

import dev.mvmo.sicklang.parser.ast.expression.ExpressionNode

interface InfixParseFunction {
    fun parse(leftExpression: ExpressionNode?): ExpressionNode?
}