package dev.mvmo.sicklang.internal.builtin.function

import dev.mvmo.sicklang.internal.``object`
import dev.mvmo.sicklang.internal.`object`.BuiltinFunctionObject
import dev.mvmo.sicklang.internal.`object`.ObjectType

class TailFunction : BuiltinFunctionObject("tail", BuiltinFunction { args: List<SickObject> ->
    if (args.size != 1) return@BuiltinFunctionObject ErrorObject.formatted(
        "wrong number of arguments. got=%d, want=%d",
        args.size,
        1
    )
    if (args[0].objectType() != ObjectType.ARRAY) return@BuiltinFunctionObject ErrorObject.formatted(
        "argument to `tail` must be ARRAY, got %s",
        args[0].objectType()
    )
    val array: ArrayObject = args[0] as ArrayObject
    if (array.elements.size > 0) return@BuiltinFunctionObject ArrayObject(
        array.elements.subList(
            1,
            array.elements.size
        )
    )
    ArrayObject(Lists.newArrayList<SickObject>())
})