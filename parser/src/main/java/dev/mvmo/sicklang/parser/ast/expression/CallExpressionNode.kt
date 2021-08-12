package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class CallExpressionNode(val token: Token, val function: ExpressionNode) : ExpressionNode {

    var arguments: List<ExpressionNode>? = null

    override fun tokenLiteral(): String =
        token.literal

    override fun toString(): String =
        "$function(${arguments?.joinToString(transform = ExpressionNode::toString) ?: ""})"

}