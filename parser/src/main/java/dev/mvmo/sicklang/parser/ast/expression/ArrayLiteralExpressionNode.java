package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "newInstance")
public class ArrayLiteralExpressionNode implements ExpressionNode {

    private Token startToken; // [
    private List<ExpressionNode> elements;

    @Override
    public String tokenLiteral() {
        return startToken.literal();
    }

    @Override
    public String toString() {
        return String.format("[%s]", elements.stream().map(ExpressionNode::toString).collect(Collectors.joining(", ")));
    }

}
