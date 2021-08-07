package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class ArrayLiteralExpressionNode(val token: Token, val elements: List<ExpressionNode>) : ExpressionNode {

    override fun tokenLiteral(): String =
        token.literal()

    override fun toString(): String =
        "[${elements.joinToString(transform = ExpressionNode::toString)}]"

}