package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class IdentifierExpressionNode(val token: Token, val value: String) : ExpressionNode {

    override fun tokenLiteral(): String =
        token.literal()

    override fun toString(): String =
        value

}