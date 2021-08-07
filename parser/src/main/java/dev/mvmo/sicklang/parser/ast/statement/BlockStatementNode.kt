package dev.mvmo.sicklang.parser.ast.statement

import dev.mvmo.sicklang.token.Token

class BlockStatementNode(val token: Token) : StatementNode {

    val statementNodes: MutableList<StatementNode> = arrayListOf()

    override fun tokenLiteral(): String =
        token.literal()

    override fun toString(): String =
        StringBuilder().apply {
            statementNodes.forEach(::append)
        }.toString()

}