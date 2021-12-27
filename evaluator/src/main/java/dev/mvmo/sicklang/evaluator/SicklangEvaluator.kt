package dev.mvmo.sicklang.evaluator

import com.google.common.base.Preconditions
import dev.mvmo.sicklang.internal.`object`.BuiltinFunctionObject
import dev.mvmo.sicklang.internal.`object`.NullObject
import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject
import dev.mvmo.sicklang.internal.`object`.array.ArrayObject
import dev.mvmo.sicklang.internal.`object`.bool.BooleanObject
import dev.mvmo.sicklang.internal.`object`.error.ErrorObject
import dev.mvmo.sicklang.internal.`object`.function.FunctionObject
import dev.mvmo.sicklang.internal.`object`.hash.HashObject
import dev.mvmo.sicklang.internal.`object`.hashkey.HashKey
import dev.mvmo.sicklang.internal.`object`.hashkey.Hashable
import dev.mvmo.sicklang.internal.`object`.number.IntegerObject
import dev.mvmo.sicklang.internal.`object`.ret.ReturnValueObject
import dev.mvmo.sicklang.internal.`object`.string.StringObject
import dev.mvmo.sicklang.internal.env.SickEnvironment
import dev.mvmo.sicklang.parser.ast.Node
import dev.mvmo.sicklang.parser.ast.expression.*
import dev.mvmo.sicklang.parser.ast.program.ProgramNode
import dev.mvmo.sicklang.parser.ast.statement.BlockStatementNode
import dev.mvmo.sicklang.parser.ast.statement.ExpressionStatementNode
import dev.mvmo.sicklang.parser.ast.statement.LetStatementNode
import dev.mvmo.sicklang.parser.ast.statement.ReturnStatementNode

object SicklangEvaluator {

    private val builtinFunctions = setOf(
        lenFunction,
        firstFunction,
        lastFunction,
        tailFunction,
        appendFunction,
        printFunction,
        printLineFunction,
        readLineFunction
    )

    fun eval(node: Node, environment: SickEnvironment): SickObject {
        return when (node) {
            is ProgramNode -> {
                return evalProgram(node, environment)
            }
            is ExpressionStatementNode -> return eval(node.expressionNode!!, environment)
            is IntegerLiteralExpressionNode -> return IntegerObject(node.value)
            is BooleanExpressionNode -> return BooleanObject.fromNative(node.value)
            is PrefixExpressionNode -> {
                val right = eval(node.right!!, environment)
                if (error(right))
                    return right
                return evalPrefixExpression(node.operator, right)
            }
            is InfixExpressionNode -> {
                val left = eval(node.left, environment)
                if (error(left))
                    return left

                val right = eval(node.right!!, environment)
                if (error(right))
                    return right

                return evalInfixExpression(node.operator, left, right)
            }
            is BlockStatementNode -> return evalBlockStatement(node, environment)
            is IfExpressionNode -> return evalIfExpression(node, environment)
            is ReturnStatementNode -> {
                val value = eval(node.returnValue!!, environment)
                if (error(value))
                    return value

                return ReturnValueObject(value)
            }
            is LetStatementNode -> {
                val value = eval(node.value!!, environment)
                if (error(value))
                    return value

                environment[node.identifier!!.value] = value // Todo the whole null handling is kinda weird
                return NullObject.NULL
            }
            is IdentifierExpressionNode -> return evalIdentifier(node, environment)
            is FunctionLiteralExpressionNode -> return FunctionObject(node.parameters!!, node.body!!, environment)
            is CallExpressionNode -> {
                val function = eval(node.function, environment)
                if (error(function))
                    return function

                val args = evalExpressions(node.arguments!!, environment)
                if (args.size == 1 && error(args[0]))
                    return args[0]

                return applyFunction(function, args)
            }
            is StringLiteralExpressionNode -> return StringObject(node.value)
            is ArrayLiteralExpressionNode -> {
                val elements = evalExpressions(node.elements, environment)
                if (elements.size == 1 && error(elements[0]))
                    return elements[0]

                return ArrayObject(elements)
            }
            is IndexExpressionNode -> {
                val left = eval(node.left, environment)
                if (error(left))
                    return left

                val index = eval(node.index, environment)
                if (error(index))
                    return index

                return evalIndexExpression(left, index)
            }
            is HashLiteralExpressionNode -> return evalHashLiteral(node, environment)
            else -> NullObject.NULL
        }
    }

    fun evalProgram(programNode: ProgramNode, environment: SickEnvironment): SickObject {
        var result: SickObject = NullObject.NULL

        for (node in programNode.statementNodes) {
            result = eval(node, environment)
            if (result is ReturnValueObject)
                return result.value
            if (result is ErrorObject)
                return result
        }

        return result
    }

    fun evalBlockStatement(blockStatementNode: BlockStatementNode, environment: SickEnvironment): SickObject {
        var result: SickObject = NullObject.NULL

        for (statementNode in blockStatementNode.statementNodes) {
            result = eval(statementNode, environment)
            if (result is ReturnValueObject || result is ErrorObject) {
                return result
            }
        }

        return result
    }

    fun evalPrefixExpression(operator: String, right: SickObject): SickObject =
        when (operator) {
            "!" -> evalBangOperatorExpression(right)
            "-" -> evalMinusPrefixOperatorExpression(right)
            else -> ErrorObject.formatted("unknown operator: %s%s", operator, right.objectType())
        }

    fun evalBangOperatorExpression(right: SickObject): SickObject {
        return if (BooleanObject.FALSE.equals(right) || NullObject.NULL == right) BooleanObject.TRUE else BooleanObject.FALSE
    }

    fun evalMinusPrefixOperatorExpression(right: SickObject): SickObject =
        when (right) {
            is IntegerObject -> IntegerObject(-right.value)
            else -> ErrorObject.formatted("unknown operator: -%s", right.objectType())
        }

    fun evalInfixExpression(operator: String, left: SickObject, right: SickObject): SickObject {
        if (left.objectType() != right.objectType())
            return ErrorObject.formatted("type mismatch: %s %s %s", left.objectType(), operator, right.objectType())

        if (operator == "==")
            return BooleanObject.fromNative(left == right)
        if (operator == "!=")
            return BooleanObject.fromNative(left != right)

        return if (left is IntegerObject && right is IntegerObject)
            evalIntegerInfixExpression(operator, left, right)
        else if (left is StringObject && right is StringObject)
            evalStringInfixExpression(operator, left, right)
        else
            ErrorObject.formatted("unknown operator: %s %s %s", left.objectType(), operator, right.objectType())
    }

    fun evalIntegerInfixExpression(operator: String, left: SickObject, right: SickObject): SickObject {
        Preconditions.checkArgument(left is IntegerObject)
        Preconditions.checkArgument(right is IntegerObject)

        val leftInt = (left as IntegerObject).value
        val rightInt = (right as IntegerObject).value

        return when (operator) {
            "+" -> IntegerObject(leftInt + rightInt)
            "-" -> IntegerObject(leftInt - rightInt)
            "*" -> IntegerObject(leftInt * rightInt)
            "/" -> IntegerObject(leftInt / rightInt)

            ">" -> BooleanObject.fromNative(leftInt > rightInt)
            "<" -> BooleanObject.fromNative(leftInt < rightInt)
            "==" -> BooleanObject.fromNative(leftInt == rightInt) // TODO: not required?
            "!=" -> BooleanObject.fromNative(leftInt != rightInt) // TODO: not required?

            else -> ErrorObject.formatted("unknown operator: %s %s %s", left.objectType(), operator, right.objectType())
        }
    }

    fun evalStringInfixExpression(operator: String, left: SickObject, right: SickObject): SickObject {
        Preconditions.checkArgument(left is StringObject)
        Preconditions.checkArgument(right is StringObject)

        if (operator != "+")
            return ErrorObject.formatted("unknown operator: %s %s %s", left.objectType(), operator, right.objectType())

        val leftValue = (left as StringObject).value
        val rightValue = (right as StringObject).value

        return StringObject(leftValue + rightValue)
    }

    // TODO: norm param names
    fun evalIfExpression(node: IfExpressionNode, env: SickEnvironment): SickObject {
        val condition = eval(node.conditionalExpressionNode!!, env) // TODO: should this really be nullable
        if (error(condition))
            return condition

        if (truthy(condition))
            return eval(node.consequence!!, env)
        else if (node.alternative != null)
            return eval(node.alternative!!, env)

        return NullObject.NULL
    }

    fun evalIndexExpression(left: SickObject, index: SickObject): SickObject {
        if (left is ArrayObject && index is IntegerObject)
            return evalArrayIndexExpression(left, index)
        else if (left is HashObject)
            return evalHashIndexExpression(left, index)

        return ErrorObject.formatted("index operator not supported: %s", left.objectType())
    }

    fun evalArrayIndexExpression(left: SickObject, index: SickObject): SickObject {
        Preconditions.checkArgument(left is ArrayObject)
        Preconditions.checkArgument(index is IntegerObject)

        val array = left as ArrayObject
        val elementIndex = (index as IntegerObject).value

        val maxIndex = array.elements.size - 1

        if (elementIndex < 0 || elementIndex > maxIndex)
            return NullObject.NULL

        return array.elements[elementIndex]
    }

    fun evalHashIndexExpression(left: SickObject, index: SickObject): SickObject {
        Preconditions.checkArgument(left is HashObject)

        val hashObject = left as HashObject
        if (index !is Hashable)
            return ErrorObject.formatted("unusable as hash key: %s", index.objectType())

        val hashable = index as Hashable
        val hashKey = hashable.hashKey()

        return hashObject.pairs[hashKey]?.value ?: NullObject.NULL
    }

    fun evalIdentifier(node: IdentifierExpressionNode, env: SickEnvironment): SickObject {
        if (env.hasKey(node.value))
            return env[node.value]!!

        return builtinFunctions.firstOrNull { it.name.contentEquals(node.value) }
            ?: ErrorObject.formatted("identifier not found: " + node.value)
    }

    fun evalHashLiteral(node: HashLiteralExpressionNode, env: SickEnvironment): SickObject {
        val pairs = mutableMapOf<HashKey, HashObject.Entry>()

        node.pairs.entries.forEach {
            val key = eval(it.key, env)
            if (error(key))
                return key

            if (key !is Hashable)
                return ErrorObject.formatted("unusable as hash key: %s", key.objectType())

            val hashable = key as Hashable
            val value = eval(it.value, env)
            if (error(value))
                return value

            pairs[hashable.hashKey()] = HashObject.Entry(key, value)
        }

        return HashObject(pairs)
    }

    fun evalExpressions(expressionNodes: List<ExpressionNode>, env: SickEnvironment): List<SickObject> {
        val result = mutableListOf<SickObject>()

        expressionNodes.forEach {
            val evaluated = eval(it, env)
            if (error(evaluated))
                return listOf(evaluated)

            result.add(evaluated)
        }

        return result
    }

    private fun applyFunction(sickObject: SickObject, args: List<SickObject>): SickObject =
        when (sickObject) {
            is FunctionObject -> {
                val extendedEnv = extendFunctionEnvironment(sickObject, args)
                val evaluated = eval(sickObject.body, extendedEnv)

                unwrapReturnValue(evaluated)
            }
            is BuiltinFunctionObject -> sickObject.function.call(args)
            else -> ErrorObject.formatted("not a function: %s", sickObject.objectType())
        }


    fun extendFunctionEnvironment(functionObject: FunctionObject, args: List<SickObject>): SickEnvironment {
        val env = SickEnvironment.newEnclosedInstance(functionObject.environment)

        functionObject.parameters.forEachIndexed { index, node ->
            val identifier = functionObject.parameters[index]
            val value = args[index]

            env[identifier.value] = value
        }

        return env
    }

    private fun unwrapReturnValue(sickObject: SickObject) =
        when (sickObject) {
            is ReturnValueObject -> sickObject.value
            else -> sickObject
        }


    private fun truthy(sickObject: SickObject): Boolean {
        if (sickObject == BooleanObject.TRUE)
            return true
        return sickObject != BooleanObject.FALSE && sickObject != NullObject.NULL
    }

/*    private static boolean truthy(SickObject
    object) {
        if (
        object == BooleanObject.TRUE)
        return true;
        return
        object != BooleanObject.FALSE &&
        object != NullObject.NULL;
    }*/

    private fun error(sickObject: SickObject?): Boolean {
        if (sickObject != null)
            return sickObject.objectType() == ObjectType.ERROR
        return false
    }
}
