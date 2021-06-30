package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "newInstance")
public class StringLiteralExpression implements ExpressionNode {

    private final Token token;
    private final String value;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return token.literal();
    }

}
