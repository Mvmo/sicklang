package dev.mvmo.sicklang.evaluator;

import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.parser.ast.Node;
import dev.mvmo.sicklang.parser.ast.expression.BooleanExpressionNode;
import dev.mvmo.sicklang.parser.ast.expression.IntegerLiteralExpressionNode;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.ExpressionStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.StatementNode;

import java.util.List;

public class SicklangEvaluator {

    public static SickObject eval(Node node) {
        if (node instanceof ProgramNode programNode) {
            return evalStatements(programNode.statementNodes());
        }

        if (node instanceof ExpressionStatementNode expressionStatementNode) {
            return eval(expressionStatementNode.expressionNode());
        }

        if (node instanceof IntegerLiteralExpressionNode integerNode) {
            return new IntegerObject(integerNode.value());
        }

        if (node instanceof BooleanExpressionNode booleanExpressionNode) {
            return BooleanObject.fromNative(booleanExpressionNode.value());
        }

        return null;
    }

    private static SickObject evalStatements(List<StatementNode> statementNodes) {
        SickObject result = null;

        for (StatementNode statementNode : statementNodes) {
            result = eval(statementNode);
        }

        return result;
    }

}
