package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "newInstance")
public class PrefixExpressionNode implements ExpressionNode {

    private final Token token;
    private final String operator;
    private ExpressionNode right;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return "(" +
                operator +
                right.toString() +
                ')';
    }
}
