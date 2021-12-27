package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class InfixExpressionNode(val token: Token, val left: ExpressionNode, val operator: String) : ExpressionNode {

    var right: ExpressionNode? = null

    override fun tokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String =
        "($left $operator $right)"

}