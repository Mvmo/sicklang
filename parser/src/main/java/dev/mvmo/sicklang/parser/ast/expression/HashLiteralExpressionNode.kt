package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.token.Token

class HashLiteralExpressionNode(val token: Token, val pairs: Map<ExpressionNode, ExpressionNode>) : ExpressionNode {

    override fun tokenLiteral(): String =
        token.literal

    override fun toString(): String =
        "{${pairs.entries.joinToString { "${it.key}:${it.value}" }}}"

}