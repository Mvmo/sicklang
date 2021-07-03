package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "newInstance")
public class IndexExpressionNode implements ExpressionNode {

    private final Token token;
    private final ExpressionNode left;
    private final ExpressionNode index;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return String.format("(%s[%s])", left.toString(), index.toString());
    }

}
