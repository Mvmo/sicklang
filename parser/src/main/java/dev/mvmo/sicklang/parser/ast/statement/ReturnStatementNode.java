package dev.mvmo.sicklang.parser.ast.statement;

import dev.mvmo.sicklang.parser.ast.expression.ExpressionNode;
import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor(staticName = "newInstance")
public class ReturnStatementNode implements StatementNode {

    private final Token token;

    private ExpressionNode returnValue;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(tokenLiteral())
                .append(" ");

        if (returnValue != null)
            builder.append(returnValue.toString());

        builder.append(";");

        return builder.toString();
    }
}
