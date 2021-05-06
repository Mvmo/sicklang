package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "newInstance")
public class InfixExpressionNode implements ExpressionNode {

    private final Token token;
    private ExpressionNode left;
    private String operator;
    private ExpressionNode right;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return "(" +
                left +
                " " + operator + " " +
                right +
                ')';
    }
}
