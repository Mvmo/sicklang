package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class PrefixExpressionNode(val token: Token, val operator: String) : ExpressionNode {

    var right: ExpressionNode? = null

    override fun tokenLiteral(): String =
        token.literal

    override fun toString(): String =
        "($operator$right)"

}