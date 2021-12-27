package dev.mvmo.sicklang.internal.`object`.error

import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject

class ErrorObject(val message: String) : SickObject {

    override fun inspect() =
        "ERROR: $message"

    override fun objectType() =
        ObjectType.ERROR

    companion object {
        @JvmStatic
        fun formatted(message: String, vararg args: Any): ErrorObject {
            return ErrorObject(String.format(message, *args))
        }
    }

}