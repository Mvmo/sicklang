package dev.mvmo.sicklang.parser.ast.program

import dev.mvmo.sicklang.parser.ast.Node
import dev.mvmo.sicklang.parser.ast.statement.StatementNode

class ProgramNode : Node {

    val statementNodes: MutableList<StatementNode> = arrayListOf()

    override fun tokenLiteral() =
        if (statementNodes.isNotEmpty()) statementNodes.first().tokenLiteral() else ""


    override fun toString() =
        statementNodes.joinToString(separator = "", transform = StatementNode::toString)

}