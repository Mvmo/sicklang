package dev.mvmo.sicklang.parser.ast.expression;

import com.google.common.collect.Lists;
import dev.mvmo.sicklang.parser.ast.statement.BlockStatementNode;
import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "newInstance")
public class FunctionLiteralExpressionNode implements ExpressionNode {

    private final Token token;
    private final List<IdentifierExpressionNode> parameters = Lists.newArrayList();
    private BlockStatementNode body;

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        return tokenLiteral() +
                "(" +
                parameters.stream().map(IdentifierExpressionNode::toString).collect(Collectors.joining(", ")) +
                ") " +
                body;
    }

}
