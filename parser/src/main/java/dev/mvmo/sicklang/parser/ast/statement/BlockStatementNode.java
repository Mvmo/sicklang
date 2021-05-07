package dev.mvmo.sicklang.parser.ast.statement;

import com.google.common.collect.Lists;
import dev.mvmo.sicklang.token.Token;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "newInstance")
public class BlockStatementNode implements StatementNode {

    private final Token token;
    private final List<StatementNode> statementNodes = Lists.newArrayList();

    @Override
    public String tokenLiteral() {
        return token.literal();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (StatementNode statementNode : statementNodes)
            stringBuilder.append(statementNode);

        return stringBuilder.toString();
    }
}
