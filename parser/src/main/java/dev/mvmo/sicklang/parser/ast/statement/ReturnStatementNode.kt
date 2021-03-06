package dev.mvmo.sicklang.parser.ast.statement

import dev.mvmo.sicklang.parser.ast.expression.ExpressionNode
import dev.mvmo.sicklang.token.Token

class ReturnStatementNode(val token: Token) : StatementNode {

    var returnValue: ExpressionNode? = null

    override fun tokenLiteral() =
        token.literal

    override fun toString() =
        StringBuilder().apply {
            append(tokenLiteral())
            append(" ")
            returnValue?.run { append(toString()) }
            append(";")
        }.toString()

}