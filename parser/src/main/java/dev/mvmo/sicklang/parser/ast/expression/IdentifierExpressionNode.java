package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "newInstance")
public class IdentifierExpressionNode implements ExpressionNode {

    private final Token token;
    private final String value;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

}
