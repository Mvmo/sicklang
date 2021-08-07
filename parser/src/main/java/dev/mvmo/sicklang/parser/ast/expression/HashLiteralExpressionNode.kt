package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(staticName = "newInstance")
public class HashLiteralExpressionNode implements ExpressionNode {

    private final Token token;
    private final Map<ExpressionNode, ExpressionNode> pairs;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return String.format("{%s}", pairs.entrySet().stream()
                .map(entry -> entry.getKey().toString() + ":" + entry.getValue().toString())
                .collect(Collectors.joining(", ")));
    }

}
