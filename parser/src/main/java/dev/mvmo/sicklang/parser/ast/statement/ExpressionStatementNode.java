package dev.mvmo.sicklang.parser.ast.statement;

import dev.mvmo.sicklang.parser.ast.expression.ExpressionNode;
import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "newInstance")
public class ExpressionStatementNode implements StatementNode {

    private final Token token;

    private ExpressionNode expressionNode;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        if (expressionNode != null)
            return expressionNode.toString();

        return "";
    }
}
