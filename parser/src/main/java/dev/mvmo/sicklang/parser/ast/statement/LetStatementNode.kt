package dev.mvmo.sicklang.parser.ast.statement

import dev.mvmo.sicklang.parser.ast.expression.ExpressionNode
import dev.mvmo.sicklang.parser.ast.expression.IdentifierExpressionNode
import dev.mvmo.sicklang.token.Token
import lombok.Getter
import lombok.Setter

@Getter
@Setter
class LetStatementNode(val token: Token) : StatementNode {

    var identifier: IdentifierExpressionNode? = null
    var value: ExpressionNode? = null

    override fun tokenLiteral(): String =
        token.literal()

    override fun toString(): String =
        """
            ${token.literal}
            | ${identifier.toString()}
            | ${value.toString()}
            |;
        """.trimMargin()

}