package dev.mvmo.sicklang.internal.`object`.number

import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject
import dev.mvmo.sicklang.internal.`object`.hashkey.HashKey
import dev.mvmo.sicklang.internal.`object`.hashkey.Hashable

data class IntegerObject(val value: Int) : SickObject, Hashable {

    override fun inspect() =
        String.format("%d", value)

    override fun objectType() =
        ObjectType.INTEGER

    override fun hashKey() =
        HashKey(objectType(), hashCode())

}