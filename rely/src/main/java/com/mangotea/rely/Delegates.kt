package com.mangotea.rely

import kotlin.reflect.KProperty

class Attribute<T>(private val default: T? = null) {

    private val expendAttributes by lazy {
        hashMapOf<Any?, MutableList<Pair<String, Any?>>>()
    }

    private fun attribute(thisRef: Any?, new: Pair<String, Any?>) {
        val target = expendAttributes[thisRef] ?: mutableListOf()
        if (!expendAttributes.contains(thisRef))
            expendAttributes[thisRef] = target
        target.takeIf {
            it.replaceFirst(new) { old ->
                old.first == new.first
            }
        } ?: target.add(new)
    }

    private fun attribute(thisRef: Any?, key: String): T =
            expendAttributes[thisRef]?.find { it.first == key }?.second as T

    private fun removeAttribute(thisRef: Any?, key: String) {
        expendAttributes[thisRef]?._removeIf {
            it.first == key
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return attribute(thisRef, property.name) ?: default as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (value != null)
            attribute(thisRef, property.name to value)
        else
            removeAttribute(thisRef, property.name)
    }

}

/**
 * 线程安全字符串
 */
class BufferString<S : CharSequence>(str: S) {
    private val stringBuffer by lazy {
        StringBuffer(str)
    }

    @Synchronized
    operator fun getValue(thisRef: Any?, property: KProperty<*>): S {
        return stringBuffer.toString() as S
    }

    @Synchronized
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: S) {
        if (value != stringBuffer.toString()) {
            "write buffer [$value] to $stringBuffer".d("ApiHost")
            stringBuffer.delete(0, stringBuffer.length)
            stringBuffer.append(value)
        }
    }
}
