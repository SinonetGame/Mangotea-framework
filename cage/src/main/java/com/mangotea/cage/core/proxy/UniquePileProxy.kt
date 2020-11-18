package com.mangotea.cage.core.proxy

import com.mangotea.cage.core.cage
import kotlin.reflect.KProperty

class UniquePileProxy<T>(private val key: String) {

    private var pileProxy: UniqueCagePile<T>? = null
    @Synchronized set
    @Synchronized get

    @Synchronized
    operator fun getValue(thisRef: Any?, property: KProperty<*>): UniqueCagePile<T> {
        return pileProxy ?: UniqueCagePile<T>(key).apply {
            pileProxy = this
        }
    }

    @Synchronized
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: UniqueCagePile<T>) {
        cage.removes(key)
        value.forEach {
            cage.add(key, it)
        }
    }
}