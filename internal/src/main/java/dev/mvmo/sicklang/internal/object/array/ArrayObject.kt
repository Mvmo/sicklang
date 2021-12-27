package dev.mvmo.sicklang.internal.`object`.array

import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject

class ArrayObject(val elements: List<SickObject>) : SickObject {

    override fun inspect() =
        "[${elements.joinToString(transform = SickObject::inspect)}}"

    override fun objectType() =
        ObjectType.ARRAY

}