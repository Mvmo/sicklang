package dev.mvmo.sicklang.parser.ast.program;

import com.google.common.collect.Lists;
import dev.mvmo.sicklang.parser.ast.Node;
import dev.mvmo.sicklang.parser.ast.statement.StatementNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(staticName = "newInstance")
public class ProgramNode implements Node {

    private final List<StatementNode> statementNodes = Lists.newArrayList();

    @Override
    public String tokenLiteral() {
        if (statementNodes.size() > 0) {
            return statementNodes.get(0).tokenLiteral();
        } else
            return "";
    }

    @Override
    public String toString() {
        return statementNodes.stream()
                .map(Node::toString)
                .collect(Collectors.joining());
    }
}
