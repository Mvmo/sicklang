package dev.mvmo.sicklang.parser.ast.statement

import com.google.common.collect.Lists
import dev.mvmo.sicklang.token.Token

class BlockStatementNode(val token: Token) : StatementNode {
    val statementNodes: List<StatementNode> = Lists.newArrayList()

    override fun tokenLiteral(): String =
        token.literal()

    override fun toString(): String =
        StringBuilder().apply {
            statementNodes.forEach(::append)
        }.toString()

}