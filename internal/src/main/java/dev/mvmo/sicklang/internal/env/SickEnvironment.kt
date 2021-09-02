package dev.mvmo.sicklang.internal.env

import dev.mvmo.sicklang.internal.`object`.SickObject

class SickEnvironment private constructor(
    private val storageMap: MutableMap<String, SickObject>,
    private val parent: SickEnvironment?
) {

    operator fun set(key: String, value: SickObject): SickEnvironment {
        storageMap[key] = value
        return this
    }

    fun setAndGet(key: String, value: SickObject): SickObject {
        set(key, value)
        return value
    }

    operator fun get(key: String): SickObject? {
        val `val`: SickObject? = storageMap[key]
        return if (`val` == null && parent != null) parent[key] else `val`
    }

    fun getOrDefault(key: String, defaultValue: SickObject?): SickObject? {
        return if (hasKey(key)) get(key) else defaultValue
    }

    fun hasKey(key: String): Boolean {
        val contains = storageMap.containsKey(key)
        return if (!contains && parent != null) parent.hasKey(key) else contains
    }

    companion object {
        @JvmStatic
        fun newEnclosedInstance(environment: SickEnvironment): SickEnvironment {
            return SickEnvironment(mutableMapOf(), environment)
        }

        @JvmStatic
        fun newInstance(): SickEnvironment {
            return SickEnvironment(mutableMapOf(), null)
        }
    }

}