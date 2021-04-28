package dev.mvmo.sicklang.parser.ast.node.statement;

import dev.mvmo.sicklang.parser.ast.node.expression.ExpressionNode;
import dev.mvmo.sicklang.token.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "newInstance")
public class LetStatementNode implements StatementNode {

    private final Token token;
    private final String identifier;
    private final ExpressionNode value;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

}
