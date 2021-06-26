package dev.mvmo.sicklang.evaluator;

import com.google.common.base.Preconditions;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.internal.object.ret.ReturnValueObject;
import dev.mvmo.sicklang.parser.ast.Node;
import dev.mvmo.sicklang.parser.ast.expression.*;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.BlockStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.ExpressionStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.ReturnStatementNode;
import dev.mvmo.sicklang.parser.ast.statement.StatementNode;

public class SicklangEvaluator {

    public static SickObject eval(Node node) {
        if (node instanceof ProgramNode programNode) {
            return evalProgram(programNode);
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
            var right = eval(prefixExpressionNode.right());
            if (error(right))
                return right;
            return evalPrefixExpression(prefixExpressionNode.operator(), right);
        }

        if (node instanceof InfixExpressionNode infixExpressionNode) {
            var left = eval(infixExpressionNode.left());
            if (error(left))
                return left;

            var right = eval(infixExpressionNode.right());
            if (error(right))
                return right;

            return evalInfixExpression(infixExpressionNode.operator(), left, right);
        }

        if (node instanceof BlockStatementNode blockStatementNode) {
            return evalBlockStatement(blockStatementNode);
        }

        if (node instanceof IfExpressionNode ifExpressionNode) {
            return evalIfExpression(ifExpressionNode);
        }

        if (node instanceof ReturnStatementNode returnStatementNode) {
            var val = eval(returnStatementNode.returnValue());
            if (error(val))
                return val;
            return new ReturnValueObject(val);
        }

        return null;
    }

    private static SickObject evalProgram(ProgramNode programNode) {
        SickObject result = null;

        for (StatementNode statementNode : programNode.statementNodes()) {
            result = eval(statementNode);
            if (result instanceof ReturnValueObject returnValueObject)
                return returnValueObject.value();
            if (result instanceof ErrorObject)
                return result;
        }

        return result;
    }

    private static SickObject evalBlockStatement(BlockStatementNode blockStatementNode) {
        SickObject result = null;

        for (StatementNode statementNode : blockStatementNode.statementNodes()) {
            result = eval(statementNode);
            if (result instanceof ReturnValueObject || result instanceof ErrorObject) {
                return result;
            }
        }

        return result;
    }

    private static SickObject evalPrefixExpression(String operator, SickObject right) {
        return switch (operator) {
            case "!" -> evalBangOperatorExpression(right);
            case "-" -> evalMinusPrefixOperatorExpression(right);
            default -> ErrorObject.newError("unknown operator: %s%s", operator, right.objectType());
        };
    }

    private static SickObject evalBangOperatorExpression(SickObject right) {
        if (BooleanObject.FALSE.equals(right) || NullObject.NULL.equals(right))
            return BooleanObject.TRUE;
        return BooleanObject.FALSE;
    }

    private static SickObject evalMinusPrefixOperatorExpression(SickObject right) {
        if (!right.objectType().equals(ObjectType.INTEGER)) // @TODO: maybe we could just do a instanceof check here - maybe also remove the objectType thingy??
            return ErrorObject.newError("unknown operator: -%s", right.objectType());

        var integerObject = (IntegerObject) right;

        return new IntegerObject(-integerObject.value());
    }

    private static SickObject evalInfixExpression(String operator, SickObject left, SickObject right) {
        if (!left.objectType().equals(right.objectType())) {
            return ErrorObject.newError("type mismatch: %s %s %s", left.objectType(), operator, right.objectType());
        }

        if (left.objectType().equals(ObjectType.INTEGER) && right.objectType().equals(ObjectType.INTEGER)) {
            return evalIntegerInfixExpression(operator, left, right);
        }

        if (operator.equals("==")) {
            return BooleanObject.fromNative(left == right);
        }

        if (operator.equals("!=")) {
            return BooleanObject.fromNative(left != right);
        }

        return ErrorObject.newError("unknown operator: %s %s %s", left.objectType(), operator, right.objectType());
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

            default -> ErrorObject.newError("unknown operator: %s %s %s", left.objectType(), operator, right.objectType());
        };
    }

    private static SickObject evalIfExpression(IfExpressionNode ifExpressionNode) {
        var condition = eval(ifExpressionNode.conditionalExpressionNode());
        if (error(condition))
            return condition;

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

    private static boolean error(SickObject object) {
        if (object != null)
            return object.objectType().equals(ObjectType.ERROR);
        return false;
    }

}
