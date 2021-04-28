package dev.mvmo.sicklang.parser.ast.node.program;

import dev.mvmo.sicklang.parser.ast.node.Node;
import dev.mvmo.sicklang.parser.ast.node.statement.StatementNode;

import java.util.List;

public class Program implements Node {

    private List<StatementNode> statementNodes;

    @Override
    public String tokenLiteral() {
        if (statementNodes.size() > 0) {
            return statementNodes.get(0).tokenLiteral();
        } else
            return "";
    }

}
