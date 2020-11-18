package com.mangotea.cage.core.proxy

import com.mangotea.cage.core.cage
import kotlin.reflect.KProperty

class NormalProxy<T>(private val key: String) {
    @Synchronized
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return cage[key]
    }

    @Synchronized
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value == null)
            cage.delete(key)
        else
            cage[key] = value
    }
}