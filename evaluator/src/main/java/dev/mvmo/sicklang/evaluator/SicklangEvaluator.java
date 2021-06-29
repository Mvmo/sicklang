package dev.mvmo.sicklang.evaluator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import dev.mvmo.sicklang.internal.env.SickEnvironment;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;
import dev.mvmo.sicklang.internal.object.function.FunctionObject;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.internal.object.ret.ReturnValueObject;
import dev.mvmo.sicklang.parser.ast.Node;
import dev.mvmo.sicklang.parser.ast.expression.*;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.*;

import java.util.List;

public class SicklangEvaluator {

    public static SickObject eval(Node node, SickEnvironment environment) {
        if (node instanceof ProgramNode programNode) {
            return evalProgram(programNode, environment);
        }

        if (node instanceof ExpressionStatementNode expressionStatementNode) {
            return eval(expressionStatementNode.expressionNode(), environment);
        }

        if (node instanceof IntegerLiteralExpressionNode integerNode) {
            return new IntegerObject(integerNode.value());
        }

        if (node instanceof BooleanExpressionNode booleanExpressionNode) {
            return BooleanObject.fromNative(booleanExpressionNode.value());
        }

        if (node instanceof PrefixExpressionNode prefixExpressionNode) {
            var right = eval(prefixExpressionNode.right(), environment);
            if (error(right))
                return right;
            return evalPrefixExpression(prefixExpressionNode.operator(), right);
        }

        if (node instanceof InfixExpressionNode infixExpressionNode) {
            var left = eval(infixExpressionNode.left(), environment);
            if (error(left))
                return left;

            var right = eval(infixExpressionNode.right(), environment);
            if (error(right))
                return right;

            return evalInfixExpression(infixExpressionNode.operator(), left, right);
        }

        if (node instanceof BlockStatementNode blockStatementNode) {
            return evalBlockStatement(blockStatementNode, environment);
        }

        if (node instanceof IfExpressionNode ifExpressionNode) {
            return evalIfExpression(ifExpressionNode, environment);
        }

        if (node instanceof ReturnStatementNode returnStatementNode) {
            var val = eval(returnStatementNode.returnValue(), environment);
            if (error(val))
                return val;
            return new ReturnValueObject(val);
        }

        if (node instanceof LetStatementNode letStatementNode) {
            var val = eval(letStatementNode.value(), environment);
            if (error(val))
                return val;

            environment.set(letStatementNode.identifier().value(), val);
        }

        if (node instanceof IdentifierExpressionNode identifierExpressionNode) {
            return evalIdentifier(identifierExpressionNode, environment);
        }

        if (node instanceof FunctionLiteralExpressionNode functionLiteralExpressionNode) {
            var params = functionLiteralExpressionNode.parameters();
            var body = functionLiteralExpressionNode.body();

            return new FunctionObject(params, body, environment);
        }

        if (node instanceof CallExpressionNode callExpressionNode) {
            var function = eval(callExpressionNode.function(), environment);
            if (error(function))
                return function;

            var args = evalExpressions(callExpressionNode.arguments(), environment);
            if (args.size() == 1 && error(args.get(0)))
                return args.get(0);

            return applyFunction(function, args);
        }

        return NullObject.NULL;
    }

    private static SickObject evalProgram(ProgramNode programNode, SickEnvironment environment) {
        SickObject result = null;

        for (StatementNode statementNode : programNode.statementNodes()) {
            result = eval(statementNode, environment);
            if (result instanceof ReturnValueObject returnValueObject)
                return returnValueObject.value();
            if (result instanceof ErrorObject)
                return result;
        }

        return result;
    }

    private static SickObject evalBlockStatement(BlockStatementNode blockStatementNode, SickEnvironment environment) {
        SickObject result = null;

        for (StatementNode statementNode : blockStatementNode.statementNodes()) {
            result = eval(statementNode, environment);
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
            default -> ErrorObject.newInstance("unknown operator: %s%s", operator, right.objectType());
        };
    }

    private static SickObject evalBangOperatorExpression(SickObject right) {
        if (BooleanObject.FALSE.equals(right) || NullObject.NULL.equals(right))
            return BooleanObject.TRUE;
        return BooleanObject.FALSE;
    }

    private static SickObject evalMinusPrefixOperatorExpression(SickObject right) {
        if (!right.objectType().equals(ObjectType.INTEGER)) // @TODO: maybe we could just do a instanceof check here - maybe also remove the objectType thingy??
            return ErrorObject.newInstance("unknown operator: -%s", right.objectType());

        var integerObject = (IntegerObject) right;

        return new IntegerObject(-integerObject.value());
    }

    private static SickObject evalInfixExpression(String operator, SickObject left, SickObject right) {
        if (!left.objectType().equals(right.objectType())) {
            return ErrorObject.newInstance("type mismatch: %s %s %s", left.objectType(), operator, right.objectType());
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

        return ErrorObject.newInstance("unknown operator: %s %s %s", left.objectType(), operator, right.objectType());
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

            default -> ErrorObject.newInstance("unknown operator: %s %s %s", left.objectType(), operator, right.objectType());
        };
    }

    private static SickObject evalIfExpression(IfExpressionNode ifExpressionNode, SickEnvironment environment) {
        var condition = eval(ifExpressionNode.conditionalExpressionNode(), environment);
        if (error(condition))
            return condition;

        if (truthy(condition)) {
            return eval(ifExpressionNode.consequence(), environment);
        } else if (ifExpressionNode.alternative() != null) {
            return eval(ifExpressionNode.alternative(), environment);
        } else {
            return NullObject.NULL;
        }
    }

    private static SickObject evalIdentifier(IdentifierExpressionNode node, SickEnvironment environment) {
        if (environment.hasKey(node.value()))
            return environment.get(node.value());
        return ErrorObject.newInstance("identifier not found: " + node.value());
    }

    private static List<SickObject> evalExpressions(List<ExpressionNode> expressions, SickEnvironment environment) {
        List<SickObject> result = Lists.newArrayListWithExpectedSize(expressions.size());

        for (ExpressionNode expression : expressions) {
            var evaluated = eval(expression, environment);
            if (error(evaluated))
                return Lists.newArrayList(evaluated);

            result.add(evaluated);
        }

        return result;
    }

    private static SickObject applyFunction(SickObject object, List<SickObject> args) {
        if (!(object instanceof FunctionObject functionObject))
            return ErrorObject.newInstance("not a function: %s", object.objectType());

        var extendedEnvironment = extendFunctionEnvironment(functionObject, args);
        var evaluated = eval(functionObject.body(), extendedEnvironment);

        return unwrapReturnValue(evaluated);
    }

    private static SickEnvironment extendFunctionEnvironment(FunctionObject functionObject, List<SickObject> args) {
        var environment = SickEnvironment.newEnclosedInstance(functionObject.environment());

        for (int i = 0; i < functionObject.parameters().size(); i++) {
            var identifier = functionObject.parameters().get(i);
            var value = args.get(i);

            environment.set(identifier.value(), value);
        }

        return environment;
    }

    private static SickObject unwrapReturnValue(SickObject sickObject) {
        if (sickObject instanceof ReturnValueObject returnValueObject)
            return returnValueObject.value();
        return sickObject;
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
