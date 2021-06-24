package dev.mvmo.sicklang.evaluator;

import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.parser.ast.Node;
import dev.mvmo.sicklang.parser.ast.expression.BooleanExpressionNode;
import dev.mvmo.sicklang.parser.ast.expression.IntegerLiteralExpressionNode;
import dev.mvmo.sicklang.parser.ast.expression.PrefixExpressionNode;
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

        if (node instanceof PrefixExpressionNode prefixExpressionNode) {
            SickObject right = eval(prefixExpressionNode.right());
            return evalPrefixExpression(prefixExpressionNode.operator(), right);
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

    private static SickObject evalPrefixExpression(String operator, SickObject right) {
        return switch (operator) {
            case "!" -> evalBangOperatorExpression(right);
            case "-" -> evalMinusPrefixOperatorExpression(right);
            default -> NullObject.NULL;
        };
    }

    private static SickObject evalBangOperatorExpression(SickObject right) {
        if (BooleanObject.FALSE.equals(right) || NullObject.NULL.equals(right))
            return BooleanObject.TRUE;
        return BooleanObject.FALSE;
    }

    private static SickObject evalMinusPrefixOperatorExpression(SickObject right) {
        if (!right.objectType().equals(ObjectType.INTEGER)) // @TODO: maybe we could just do a instanceof check here - maybe also remove the objectType thingy??
            return NullObject.NULL;

        IntegerObject integerObject = (IntegerObject) right;

        return new IntegerObject(-integerObject.value());
    }

}
