package dev.mvmo.sicklang.parser.ast.expression

import dev.mvmo.sicklang.parser.ast.statement.BlockStatementNode
import dev.mvmo.sicklang.token.Token

class IfExpressionNode(val token: Token) : ExpressionNode {

    var conditionalExpressionNode: ExpressionNode? = null
    var consequence: BlockStatementNode? = null
    var alternative: BlockStatementNode? = null

    override fun tokenLiteral(): String =
        token.literal

    override fun toString(): String =
        "if${conditionalExpressionNode.toString()} $consequence ${if (alternative != null) "else$alternative" else ""}"

}