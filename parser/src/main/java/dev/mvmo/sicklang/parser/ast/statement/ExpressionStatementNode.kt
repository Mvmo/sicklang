package dev.mvmo.sicklang.parser.ast.statement

import dev.mvmo.sicklang.parser.ast.expression.ExpressionNode
import dev.mvmo.sicklang.token.Token

class ExpressionStatementNode(val token: Token) : StatementNode {

    var expressionNode: ExpressionNode? = null

    override fun tokenLiteral() =
        token.literal

    override fun toString() =
        expressionNode?.toString() ?: ""

}