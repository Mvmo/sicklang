package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class IntegerLiteralExpressionNode(val token: Token) : ExpressionNode {

    var value = 0

    override fun tokenLiteral(): String =
        token.literal()

    override fun toString(): String =
        token.literal()

}