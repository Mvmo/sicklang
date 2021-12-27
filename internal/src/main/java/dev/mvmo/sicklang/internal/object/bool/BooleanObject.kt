package dev.mvmo.sicklang.internal.`object`.bool

import com.google.common.base.Objects
import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject
import dev.mvmo.sicklang.internal.`object`.hashkey.HashKey
import dev.mvmo.sicklang.internal.`object`.hashkey.Hashable

class BooleanObject(val value: Boolean) : SickObject, Hashable {
    override fun inspect() =
        "$value"

    override fun objectType() =
        ObjectType.BOOLEAN

    override fun hashKey() =
        HashKey(objectType(), hashCode())

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as BooleanObject
        return value == that.value
    }

    override fun hashCode(): Int {
        return Objects.hashCode(value)
    }

    companion object {
        @JvmField
        val TRUE = BooleanObject(true)
        @JvmField
        val FALSE = BooleanObject(false)
        @JvmStatic
        fun fromNative(bool: Boolean): BooleanObject {
            return if (bool) TRUE else FALSE
        }
    }
}