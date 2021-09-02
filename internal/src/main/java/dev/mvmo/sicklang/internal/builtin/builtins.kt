package dev.mvmo.sicklang.internal.builtin

import dev.mvmo.sicklang.internal.`object`.BuiltinFunctionObject
import dev.mvmo.sicklang.internal.`object`.NullObject
import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject
import dev.mvmo.sicklang.internal.`object`.array.ArrayObject
import dev.mvmo.sicklang.internal.`object`.error.ErrorObject
import dev.mvmo.sicklang.internal.`object`.number.IntegerObject
import dev.mvmo.sicklang.internal.`object`.string.StringObject
import java.util.*

fun createBuiltinFunction(name: String, func: BuiltinFunction) = BuiltinFunctionObject(name, func)

val printLineFunction = createBuiltinFunction("println") {
    it.map(SickObject::inspect)
        .forEach(::println)

    NullObject.NULL
}

val readLineFunction = createBuiltinFunction("readLine") {
    if (it.size > 1)
        return@createBuiltinFunction ErrorObject.formatted("wrong number of arguments to 'readLine'")

    if (it.size == 1) {
        if (it[0].objectType() != ObjectType.STRING)
            return@createBuiltinFunction ErrorObject.formatted(
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
        return@createBuiltinFunction ErrorObject.formatted("wrong number of arguments. got=%d, want=%d", it.size, 1)

    val arg = it[0]
    if (arg is StringObject)
        return@createBuiltinFunction IntegerObject(arg.value.length)
    else if (arg is ArrayObject)
        return@createBuiltinFunction IntegerObject(arg.elements.size);

    return@createBuiltinFunction ErrorObject.formatted("argument to `len` not supported. got %s", arg.objectType());
}