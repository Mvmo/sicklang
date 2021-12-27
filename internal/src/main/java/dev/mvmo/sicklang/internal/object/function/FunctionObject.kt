package dev.mvmo.sicklang.internal.`object`.function

import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject
import dev.mvmo.sicklang.internal.env.SickEnvironment
import dev.mvmo.sicklang.parser.ast.expression.IdentifierExpressionNode
import dev.mvmo.sicklang.parser.ast.statement.BlockStatementNode

class FunctionObject(
    val parameters: List<IdentifierExpressionNode>, val body: BlockStatementNode, val environment: SickEnvironment
) : SickObject {

    override fun inspect() =
        """fn(${parameters.joinToString(transform = IdentifierExpressionNode::toString)}) {
                $body
           }
        """

    override fun objectType() =
        ObjectType.FUNCTION

}