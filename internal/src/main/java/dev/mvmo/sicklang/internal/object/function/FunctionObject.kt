package dev.mvmo.sicklang.internal.object.function;

import dev.mvmo.sicklang.internal.env.SickEnvironment;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.parser.ast.expression.IdentifierExpressionNode;
import dev.mvmo.sicklang.parser.ast.statement.BlockStatementNode;

import java.util.List;
import java.util.stream.Collectors;

public record FunctionObject(List<IdentifierExpressionNode> parameters, BlockStatementNode body,
                             SickEnvironment environment) implements SickObject {

    @Override
    public String inspect() {
        return String.format("fn(%s) {\n%s\n}",
                parameters.stream()
                        .map(IdentifierExpressionNode::toString)
                        .collect(Collectors.joining(", ")),
                body.toString());
    }

    @Override
    public ObjectType objectType() {
        return ObjectType.FUNCTION;
    }

}
