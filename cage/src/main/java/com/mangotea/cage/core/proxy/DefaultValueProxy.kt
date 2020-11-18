package com.mangotea.cage.core.proxy

import com.mangotea.cage.core.cage
import kotlin.reflect.KProperty

class DefaultValueProxy<T>(private val key: String, private val default: T) {
    @Synchronized
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return cage[key] ?: default
    }

    @Synchronized
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (value == null)
            cage.delete(key)
        else
            cage[key] = value
    }
}