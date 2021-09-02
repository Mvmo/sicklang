package dev.mvmo.sicklang.internal.builtin

import dev.mvmo.sicklang.internal.`object`.SickObject

fun interface BuiltinFunction {
    fun call(args: List<SickObject>): SickObject
}