package dev.mvmo.sicklang.parser.ast.node.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "newInstance")
public class IdentifierExpressionNode implements ExpressionNode {

    private final Token token;
    private final String value;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

}
