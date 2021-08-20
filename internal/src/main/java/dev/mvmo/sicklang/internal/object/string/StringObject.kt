package dev.mvmo.sicklang.internal.`object`.string

import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject
import dev.mvmo.sicklang.internal.`object`.hashkey.HashKey
import dev.mvmo.sicklang.internal.`object`.hashkey.Hashable

data class StringObject(val value: String) : SickObject, Hashable {

    override fun inspect() =
        value

    override fun objectType() =
        ObjectType.STRING

    override fun hashKey() =
        HashKey(objectType(), hashCode())

}