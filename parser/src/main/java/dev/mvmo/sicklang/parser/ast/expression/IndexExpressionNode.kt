package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class IndexExpressionNode(val token: Token, val left: ExpressionNode, val index: ExpressionNode) : ExpressionNode {

    override fun tokenLiteral(): String =
        token.literal

    override fun toString(): String =
        "($left[$index])"

}