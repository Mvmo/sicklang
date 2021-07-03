package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "newInstance")
public class IndexExpressionNode implements ExpressionNode {

    private final Token token;
    private final ExpressionNode left;
    private final ExpressionNode right;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return String.format("(%s[%s])", left.toString(), right.toString());
    }

}
