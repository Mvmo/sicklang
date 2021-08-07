package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class StringLiteralExpressionNode(val token: Token, val value: String) : ExpressionNode {

    override fun tokenLiteral(): String =
        token.literal()

    override fun toString(): String =
        token.literal()

}