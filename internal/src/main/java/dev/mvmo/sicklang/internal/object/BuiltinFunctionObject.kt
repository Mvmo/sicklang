package dev.mvmo.sicklang.internal.`object`

import dev.mvmo.sicklang.internal.builtin.BuiltinFunction

open class BuiltinFunctionObject(val name: String, val function: BuiltinFunction) : SickObject {

    override fun inspect() =
        name

    override fun objectType() =
        ObjectType.BUILTIN

}