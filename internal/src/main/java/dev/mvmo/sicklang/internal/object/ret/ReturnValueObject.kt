package dev.mvmo.sicklang.internal.`object`.ret

import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject

class ReturnValueObject(val value: SickObject) : SickObject {

    override fun inspect() =
        value.inspect()

    override fun objectType() =
        ObjectType.RETURN_VALUE

}