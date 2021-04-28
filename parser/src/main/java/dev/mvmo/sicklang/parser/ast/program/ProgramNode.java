package dev.mvmo.sicklang.parser.ast.program;

import dev.mvmo.sicklang.parser.ast.Node;
import dev.mvmo.sicklang.parser.ast.statement.StatementNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "newInstance")
public class ProgramNode implements Node {

    private final List<StatementNode> statementNodes = new ArrayList<>();

    @Override
    public String tokenLiteral() {
        if (statementNodes.size() > 0) {
            return statementNodes.get(0).tokenLiteral();
        } else
            return "";
    }

}