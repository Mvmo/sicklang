package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(staticName = "newInstance")
public class ArrayLiteralExpressionNode implements ExpressionNode {

    private final Token token; // [
    private final List<ExpressionNode> elements;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return String.format("[%s]", elements.stream().map(ExpressionNode::toString).collect(Collectors.joining(", ")));
    }

}
