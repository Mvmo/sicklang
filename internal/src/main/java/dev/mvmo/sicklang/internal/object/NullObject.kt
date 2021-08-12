package dev.mvmo.sicklang.internal.`object`

class NullObject : SickObject {
    override fun inspect() =
        "null"

    override fun objectType() =
        ObjectType.NULL

    companion object {
        @JvmField
        val NULL = NullObject()
        fun get() = NULL
    }
}