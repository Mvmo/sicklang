package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class PrefixExpressionNode(val token: Token, val operator: String) : ExpressionNode {

    var right: ExpressionNode? = null

    override fun tokenLiteral() =
        token.literal

    override fun toString() =
        "($operator$right)"

}