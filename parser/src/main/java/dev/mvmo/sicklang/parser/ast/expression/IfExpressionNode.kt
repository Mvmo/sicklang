package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.parser.ast.statement.BlockStatementNode;
import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor(staticName = "newInstance")
public class IfExpressionNode implements ExpressionNode {

    private final Token token;
    private ExpressionNode conditionalExpressionNode;
    private BlockStatementNode consequence;
    private BlockStatementNode alternative;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("if")
                .append(conditionalExpressionNode)
                .append(" ")
                .append(consequence);

        if (alternative != null)
            stringBuilder.append("else")
                    .append(alternative);

        return stringBuilder.toString();
    }
}
