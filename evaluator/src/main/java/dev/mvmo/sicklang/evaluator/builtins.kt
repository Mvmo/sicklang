package dev.mvmo.sicklang.evaluator

import dev.mvmo.sicklang.internal.`object`.BuiltinFunctionObject
import dev.mvmo.sicklang.internal.`object`.NullObject
import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject
import dev.mvmo.sicklang.internal.`object`.array.ArrayObject
import dev.mvmo.sicklang.internal.`object`.error.ErrorObject.Companion.formatted
import dev.mvmo.sicklang.internal.`object`.number.IntegerObject
import dev.mvmo.sicklang.internal.`object`.string.StringObject
import dev.mvmo.sicklang.internal.builtin.BuiltinFunction
import java.util.*

fun createBuiltinFunction(name: String, func: BuiltinFunction) = BuiltinFunctionObject(name, func)

val printFunction = createBuiltinFunction("print") {
    if (it.isEmpty())
        return@createBuiltinFunction formatted("wrong number of arguments to 'print'")

    print(it.joinToString(transform = SickObject::inspect))

    return@createBuiltinFunction NullObject.NULL
}

val printLineFunction = createBuiltinFunction("println") {
    it.map(SickObject::inspect)
        .forEach(::println)

    NullObject.NULL
}

val readLineFunction = createBuiltinFunction("readLine") {
    if (it.size > 1)
        return@createBuiltinFunction formatted("wrong number of arguments to 'readLine'")

    if (it.size == 1) {
        if (it[0].objectType() != ObjectType.STRING)
            return@createBuiltinFunction formatted(
                "expected string got=%s", it[0].objectType()
            )
        print(it[0].inspect())
    }

    val scanner = Scanner(System.`in`)
    val input = scanner.nextLine()

    return@createBuiltinFunction StringObject(input)
}

val lenFunction = createBuiltinFunction("len") {
    if (it.size != 1)
        return@createBuiltinFunction formatted("wrong number of arguments. got=%d, want=%d", it.size, 1)

    val arg = it[0]
    if (arg is StringObject)
        return@createBuiltinFunction IntegerObject(arg.value.length)
    else if (arg is ArrayObject)
        return@createBuiltinFunction IntegerObject(arg.elements.size);

    return@createBuiltinFunction formatted("argument to `len` not supported. got %s", arg.objectType());
}

val tailFunction = createBuiltinFunction("tail") {
    if (it.size != 1)
        return@createBuiltinFunction formatted(
            "wrong number of arguments. got=%d, want=%d",
            it.size,
            1
        )

    if (it[0].objectType() != ObjectType.ARRAY)
        return@createBuiltinFunction formatted(
            "argument to `tail` must be ARRAY, got %s",
            it[0].objectType()
        )

    val array: ArrayObject = it[0] as ArrayObject
    if (array.elements.isNotEmpty())
        return@createBuiltinFunction ArrayObject(array.elements.subList(1, array.elements.size))

    ArrayObject(arrayListOf())
}

val lastFunction = createBuiltinFunction("last") {
    if (it.size != 1)
        return@createBuiltinFunction formatted("wrong number of arguments. got=%d, want=%d", it.size, 1)

    if (it[0].objectType() != ObjectType.ARRAY)
        return@createBuiltinFunction formatted(
            "argument to `last` must be ARRAY, got %s",
            it[0].objectType()
        )

    val array = it[0] as ArrayObject
    val length = array.elements.size

    return@createBuiltinFunction if (length > 0)
        array.elements[length - 1]
    else
        NullObject.NULL
}

val firstFunction = createBuiltinFunction("first") {
    if (it.size != 1)
        return@createBuiltinFunction formatted("wrong number of arguments. got=%d, want=%d", it.size, 1)

    if (it[0].objectType() != ObjectType.ARRAY)
        return@createBuiltinFunction formatted(
            "argument to `first` must be ARRAY, got %s",
            it[0].objectType()
        )

    val array = it[0] as ArrayObject
    return@createBuiltinFunction if (array.elements.isNotEmpty())
        array.elements[0]
    else
        NullObject.NULL
}

val appendFunction = createBuiltinFunction("append") {
    if (it.size != 2)
        return@createBuiltinFunction formatted("wrong number of arguments. got=%d, want=%d", it.size, 2)
    if (it[0].objectType() != ObjectType.ARRAY)
        return@createBuiltinFunction formatted("first argument to `append` must be ARRAY, got %s", it.get(0).objectType())

    val array = it[0] as ArrayObject

    val newElements = ArrayList(array.elements)
    newElements.add(it[1])

    return@createBuiltinFunction ArrayObject(newElements)
}