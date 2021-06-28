package dev.mvmo.sicklang.parser.ast.statement;

import dev.mvmo.sicklang.parser.ast.expression.ExpressionNode;
import dev.mvmo.sicklang.parser.ast.expression.IdentifierExpressionNode;
import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "newInstance")
public class LetStatementNode implements StatementNode {

    private final Token token;
    private IdentifierExpressionNode identifier; // name
    private ExpressionNode value;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return token.literal() +
                " " +
                identifier.toString() +
                " = " +
                value.toString() +
                ";";
    }
}
