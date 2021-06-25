package dev.mvmo.sicklang.evaluator;

import com.google.common.base.Preconditions;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.internal.object.ret.ReturnValueObject;
import dev.mvmo.sicklang.parser.ast.Node;
import dev.mvmo.sicklang.parser.ast.expression.*;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.BlockStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.ExpressionStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.ReturnStatementNode;
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

        if (node instanceof InfixExpressionNode infixExpressionNode) {
            SickObject left = eval(infixExpressionNode.left());
            SickObject right = eval(infixExpressionNode.right());

            return evalInfixExpression(infixExpressionNode.operator(), left, right);
        }

        if (node instanceof BlockStatementNode blockStatementNode) {
            return evalStatements(blockStatementNode.statementNodes());
        }

        if (node instanceof IfExpressionNode ifExpressionNode) {
            return evalIfExpression(ifExpressionNode);
        }

        if (node instanceof ReturnStatementNode returnStatementNode) {
            SickObject val = eval(returnStatementNode.returnValue());
            return new ReturnValueObject(val);
        }

        return null;
    }

    private static SickObject evalStatements(List<StatementNode> statementNodes) {
        SickObject result = null;

        for (StatementNode statementNode : statementNodes) {
            result = eval(statementNode);
            if (result instanceof ReturnValueObject returnValueObject)
                return returnValueObject.value();
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

    private static SickObject evalInfixExpression(String operator, SickObject left, SickObject right) {
        if (left.objectType().equals(ObjectType.INTEGER) && right.objectType().equals(ObjectType.INTEGER)) {
            return evalIntegerInfixExpression(operator, left, right);
        }

        if (operator.equals("==")) {
            return BooleanObject.fromNative(left == right);
        }

        if (operator.equals("!=")) {
            return BooleanObject.fromNative(left != right);
        }

        return NullObject.NULL;
    }

    private static SickObject evalIntegerInfixExpression(String operator, SickObject left, SickObject right) {
        Preconditions.checkArgument(left instanceof IntegerObject);
        Preconditions.checkArgument(right instanceof IntegerObject);

        int leftInt = ((IntegerObject) left).value();
        int rightInt = ((IntegerObject) right).value();

        return switch (operator) {
            case "+" -> new IntegerObject(leftInt + rightInt);
            case "-" -> new IntegerObject(leftInt - rightInt);
            case "*" -> new IntegerObject(leftInt * rightInt);
            case "/" -> new IntegerObject(leftInt / rightInt);

            case ">" -> BooleanObject.fromNative(leftInt > rightInt);
            case "<" -> BooleanObject.fromNative(leftInt < rightInt);
            case "==" -> BooleanObject.fromNative(leftInt == rightInt);
            case "!=" -> BooleanObject.fromNative(leftInt != rightInt);

            default -> NullObject.NULL;
        };
    }

    private static SickObject evalIfExpression(IfExpressionNode ifExpressionNode) {
        SickObject condition = eval(ifExpressionNode.conditionalExpressionNode());

        if (truthy(condition)) {
            return eval(ifExpressionNode.consequence());
        } else if (ifExpressionNode.alternative() != null) {
            return eval(ifExpressionNode.alternative());
        } else {
            return NullObject.NULL;
        }
    }

    private static boolean truthy(SickObject object) {
        if (object == BooleanObject.TRUE)
            return true;
        return object != BooleanObject.FALSE && object != NullObject.NULL;
    }

}
