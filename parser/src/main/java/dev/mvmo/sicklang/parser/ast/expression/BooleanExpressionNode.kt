package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class BooleanExpressionNode(val token: Token, val value: Boolean) : ExpressionNode {

    override fun tokenLiteral(): String =
        token.literal()

    override fun toString(): String =
        token.literal()

}