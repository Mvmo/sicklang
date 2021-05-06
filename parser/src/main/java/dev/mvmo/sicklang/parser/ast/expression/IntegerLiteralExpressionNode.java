package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "newInstance")
public class IntegerLiteralExpressionNode implements ExpressionNode {

    private final Token token;
    private String value;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return token.literal();
    }

}
