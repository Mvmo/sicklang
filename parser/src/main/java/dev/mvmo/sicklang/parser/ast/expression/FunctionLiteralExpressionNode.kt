package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.parser.ast.statement.BlockStatementNode
import dev.mvmo.sicklang.token.Token

class FunctionLiteralExpressionNode(val token: Token) : ExpressionNode {

    var parameters: List<IdentifierExpressionNode>? = null
    var body: BlockStatementNode? = null

    override fun tokenLiteral(): String =
        token.literal()

    override fun toString(): String =
        "${tokenLiteral()}(${parameters?.joinToString(transform = IdentifierExpressionNode::toString) ?: ""}) $body"

}