package dev.mvmo.sicklang.parser.ast.expression;

import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "newInstance")
public class CallExpressionNode implements ExpressionNode {

    private final Token token;
    private final ExpressionNode function; // identifier or function
    private List<ExpressionNode> arguments;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return function.toString() +
                "(" +
                arguments.stream().map(ExpressionNode::toString).collect(Collectors.joining(", ")) +
                ")";
    }

}
