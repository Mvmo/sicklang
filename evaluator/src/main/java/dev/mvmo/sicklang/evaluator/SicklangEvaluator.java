package dev.mvmo.sicklang.evaluator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.mvmo.sicklang.internal.builtin.function.*;
import dev.mvmo.sicklang.internal.env.SickEnvironment;
import dev.mvmo.sicklang.internal.object.BuiltinFunctionObject;
import dev.mvmo.sicklang.internal.object.NullObject;
import dev.mvmo.sicklang.internal.object.ObjectType;
import dev.mvmo.sicklang.internal.object.SickObject;
import dev.mvmo.sicklang.internal.object.array.ArrayObject;
import dev.mvmo.sicklang.internal.object.bool.BooleanObject;
import dev.mvmo.sicklang.internal.object.error.ErrorObject;
import dev.mvmo.sicklang.internal.object.function.FunctionObject;
import dev.mvmo.sicklang.internal.object.hash.HashObject;
import dev.mvmo.sicklang.internal.object.hashkey.HashKey;
import dev.mvmo.sicklang.internal.object.hashkey.Hashable;
import dev.mvmo.sicklang.internal.object.number.IntegerObject;
import dev.mvmo.sicklang.internal.object.ret.ReturnValueObject;
import dev.mvmo.sicklang.internal.object.string.StringObject;
import dev.mvmo.sicklang.parser.ast.Node;
import dev.mvmo.sicklang.parser.ast.expression.*;
import dev.mvmo.sicklang.parser.ast.program.ProgramNode;
import dev.mvmo.sicklang.parser.ast.statement.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SicklangEvaluator {

    private static final Set<BuiltinFunctionObject> builtinFunctions = Sets.newHashSet(
            new LenFunction(),
            new FirstFunction(),
            new LastFunction(),
            new TailFunction(),
            new AppendFunction(),
            new PrintFunction(),
            new PrintlnFunction(),
            new ReadLineFunction()
    );

    public static SickObject eval(Node node, SickEnvironment environment) {
        if (node instanceof ProgramNode programNode) {
            return evalProgram(programNode, environment);
        }

        if (node instanceof ExpressionStatementNode expressionStatementNode) {
            return eval(expressionStatementNode.getExpressionNode(), environment);
        }

        if (node instanceof IntegerLiteralExpressionNode integerNode) {
            return new IntegerObject(integerNode.getValue());
        }

        if (node instanceof BooleanExpressionNode booleanExpressionNode) {
            return BooleanObject.fromNative(booleanExpressionNode.getValue());
        }

        if (node instanceof PrefixExpressionNode prefixExpressionNode) {
            var right = eval(prefixExpressionNode.getRight(), environment);
            if (error(right))
                return right;
            return evalPrefixExpression(prefixExpressionNode.getOperator(), right);
        }

        if (node instanceof InfixExpressionNode infixExpressionNode) {
            var left = eval(infixExpressionNode.getLeft(), environment);
            if (error(left))
                return left;

            var right = eval(infixExpressionNode.getRight(), environment);
            if (error(right))
                return right;

            return evalInfixExpression(infixExpressionNode.getOperator(), left, right);
        }

        if (node instanceof BlockStatementNode blockStatementNode) {
            return evalBlockStatement(blockStatementNode, environment);
        }

        if (node instanceof IfExpressionNode ifExpressionNode) {
            return evalIfExpression(ifExpressionNode, environment);
        }

        if (node instanceof ReturnStatementNode returnStatementNode) {
            var val = eval(returnStatementNode.getReturnValue(), environment);
            if (error(val))
                return val;
            return new ReturnValueObject(val);
        }

        if (node instanceof LetStatementNode letStatementNode) {
            var val = eval(letStatementNode.getValue(), environment);
            if (error(val))
                return val;

            environment.set(letStatementNode.getIdentifier().getValue(), val);
        }

        if (node instanceof IdentifierExpressionNode identifierExpressionNode) {
            return evalIdentifier(identifierExpressionNode, environment);
        }

        if (node instanceof FunctionLiteralExpressionNode functionLiteralExpressionNode) {
            var params = functionLiteralExpressionNode.getParameters();
            var body = functionLiteralExpressionNode.getBody();

            return new FunctionObject(params, body, environment);
        }

        if (node instanceof CallExpressionNode callExpressionNode) {
            var function = eval(callExpressionNode.getFunction(), environment);
            if (error(function))
                return function;

            var args = evalExpressions(callExpressionNode.getArguments(), environment);
            if (args.size() == 1 && error(args.get(0)))
                return args.get(0);

            return applyFunction(function, args);
        }

        if (node instanceof StringLiteralExpressionNode stringLiteralExpressionNode) {
            return new StringObject(stringLiteralExpressionNode.getValue());
        }

        if (node instanceof ArrayLiteralExpressionNode arrayLiteralExpressionNode) {
            var elements = evalExpressions(arrayLiteralExpressionNode.getElements(), environment);
            if (elements.size() == 1 && error(elements.get(0)))
                return elements.get(0);

            return new ArrayObject(elements);
        }

        if (node instanceof IndexExpressionNode indexExpressionNode) {
            var left = eval(indexExpressionNode.getLeft(), environment);
            if (error(left))
                return left;

            var index = eval(indexExpressionNode.getIndex(), environment);
            if (error(index))
                return index;

            return evalIndexExpression(left, index);
        }

        if (node instanceof HashLiteralExpressionNode hashLiteralExpressionNode)
            return evalHashLiteral(hashLiteralExpressionNode, environment);

        return NullObject.NULL;
    }

    private static SickObject evalProgram(ProgramNode programNode, SickEnvironment environment) {
        SickObject result = null;

        for (StatementNode statementNode : programNode.getStatementNodes()) {
            result = eval(statementNode, environment);
            if (result instanceof ReturnValueObject returnValueObject)
                return returnValueObject.getValue();
            if (result instanceof ErrorObject)
                return result;
        }

        return result;
    }

    private static SickObject evalBlockStatement(BlockStatementNode blockStatementNode, SickEnvironment environment) {
        SickObject result = null;

        for (StatementNode statementNode : blockStatementNode.getStatementNodes()) {
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

        return new IntegerObject(-integerObject.getValue());
    }

    private static SickObject evalInfixExpression(String operator, SickObject left, SickObject right) {
        if (!left.objectType().equals(right.objectType())) {
            return ErrorObject.newInstance("type mismatch: %s %s %s", left.objectType(), operator, right.objectType());
        }

        if (left.objectType().equals(ObjectType.INTEGER) && right.objectType().equals(ObjectType.INTEGER)) {
            return evalIntegerInfixExpression(operator, left, right);
        }

        if (left.objectType().equals(ObjectType.STRING) && right.objectType().equals(ObjectType.STRING)) {
            return evalStringInfixExpression(operator, left, right);
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

        int leftInt = ((IntegerObject) left).getValue();
        int rightInt = ((IntegerObject) right).getValue();

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

    private static SickObject evalStringInfixExpression(String operator, SickObject left, SickObject right) {
        Preconditions.checkArgument(left instanceof StringObject);
        Preconditions.checkArgument(right instanceof StringObject);

        if (!operator.equals("+"))
            return ErrorObject.newInstance("unknown operator: %s %s %s", left.objectType(), operator, right.objectType());

        var leftValue = ((StringObject) left).getValue();
        var rightValue = ((StringObject) right).getValue();

        return new StringObject(leftValue + rightValue);
    }

    private static SickObject evalIfExpression(IfExpressionNode ifExpressionNode, SickEnvironment environment) {
        var condition = eval(ifExpressionNode.getConditionalExpressionNode(), environment);
        if (error(condition))
            return condition;

        if (truthy(condition)) {
            return eval(ifExpressionNode.getConsequence(), environment);
        } else if (ifExpressionNode.getAlternative() != null) {
            return eval(ifExpressionNode.getAlternative(), environment);
        } else {
            return NullObject.NULL;
        }
    }

    private static SickObject evalIndexExpression(SickObject left, SickObject index) {
        if (left.objectType().equals(ObjectType.ARRAY) && index.objectType().equals(ObjectType.INTEGER))
            return evalArrayIndexExpression(left, index);
        if (left.objectType().equals(ObjectType.HASH))
            return evalHashIndexExpression(left, index);
        return ErrorObject.newInstance("index operator not supported: %s", left.objectType());
    }

    private static SickObject evalArrayIndexExpression(SickObject left, SickObject index) {
        Preconditions.checkArgument(left instanceof ArrayObject);
        Preconditions.checkArgument(index instanceof IntegerObject);

        var array = (ArrayObject) left;
        var elementIndex = ((IntegerObject) index).getValue();

        int maxIndex = array.elements().size() - 1;

        if (elementIndex < 0 || elementIndex > maxIndex)
            return NullObject.NULL;

        return array.elements().get(elementIndex);
    }

    private static SickObject evalHashIndexExpression(SickObject left, SickObject index) {
        Preconditions.checkArgument(left instanceof HashObject);

        var hashObject = (HashObject) left;
        if (!(index instanceof Hashable hashable))
            return ErrorObject.newInstance("unusable as hash key: %s", index.objectType());

        var hashKey = hashable.hashKey();

        if (!hashObject.getPairs().containsKey(hashKey))
            return NullObject.NULL;

        return hashObject.getPairs().get(hashKey).getValue();
    }

    private static SickObject evalIdentifier(IdentifierExpressionNode node, SickEnvironment environment) {
        if (environment.hasKey(node.getValue()))
            return environment.get(node.getValue());

        Optional<BuiltinFunctionObject> builtinOptional = builtinFunctions.stream()
                .filter(function -> function.getName().equals(node.getValue()))
                .findFirst();

        if (builtinOptional.isPresent())
            return builtinOptional.get();

        return ErrorObject.newInstance("identifier not found: " + node.getValue());
    }

    private static SickObject evalHashLiteral(HashLiteralExpressionNode hashNode, SickEnvironment environment) {
        Map<HashKey, HashObject.Entry> pairs = Maps.newHashMap();

        for (Map.Entry<ExpressionNode, ExpressionNode> nodeEntry : hashNode.getPairs().entrySet()) {
            var key = eval(nodeEntry.getKey(), environment);
            if (error(key))
                return key;

            if (!(key instanceof Hashable keyWithHash))
                return ErrorObject.newInstance("unusable as hash key: %s", key.objectType());

            var value = eval(nodeEntry.getValue(), environment);
            if (error(value))
                return value;

            pairs.put(keyWithHash.hashKey(), new HashObject.Entry(key, value));
        }

        return new HashObject(pairs);
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
        if (object instanceof FunctionObject functionObject) {
            var extendedEnvironment = extendFunctionEnvironment(functionObject, args);
            var evaluated = eval(functionObject.getBody(), extendedEnvironment);

            return unwrapReturnValue(evaluated);
        }

        if (object instanceof BuiltinFunctionObject builtinFunctionObject) {
            return builtinFunctionObject.getFunction().call(args);
        }

        return ErrorObject.newInstance("not a function: %s", object.objectType());
    }

    private static SickEnvironment extendFunctionEnvironment(FunctionObject functionObject, List<SickObject> args) {
        var environment = SickEnvironment.newEnclosedInstance(functionObject.getEnvironment());

        for (int i = 0; i < functionObject.getParameters().size(); i++) {
            var identifier = functionObject.getParameters().get(i);
            var value = args.get(i);

            environment.set(identifier.getValue(), value);
        }

        return environment;
    }

    private static SickObject unwrapReturnValue(SickObject sickObject) {
        if (sickObject instanceof ReturnValueObject returnValueObject)
            return returnValueObject.getValue();
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
