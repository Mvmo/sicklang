package dev.mvmo.sicklang.parser.ast.statement

import dev.mvmo.sicklang.token.Token

class BlockStatementNode(val token: Token) : StatementNode {

    val statementNodes: MutableList<StatementNode> = arrayListOf()

    override fun tokenLiteral() =
        token.literal

    override fun toString() =
        StringBuilder().apply {
            statementNodes.forEach(::append)
        }.toString()

}