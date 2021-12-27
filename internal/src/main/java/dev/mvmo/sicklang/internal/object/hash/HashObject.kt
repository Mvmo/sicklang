package dev.mvmo.sicklang.internal.`object`.hash

import dev.mvmo.sicklang.internal.`object`.ObjectType
import dev.mvmo.sicklang.internal.`object`.SickObject
import dev.mvmo.sicklang.internal.`object`.hashkey.HashKey

class HashObject(val pairs: Map<HashKey, Entry>) : SickObject {

    override fun inspect() =
        "{${pairs.map { it.value }.joinToString { "${it.key}: ${it.value}" }}}"

    override fun objectType() =
        ObjectType.HASH

    data class Entry(val key: SickObject, val value: SickObject)

}