package com.mangotea.cage.core.proxy

import com.mangotea.cage.core.cage
import kotlin.reflect.KProperty

class PileProxy<T>(private val key: String) {

    private var pileProxy: CagePile<T>? = null
    @Synchronized set
    @Synchronized get

    @Synchronized
    operator fun getValue(thisRef: Any?, property: KProperty<*>): CagePile<T> {
        return pileProxy ?: CagePile<T>(key).apply {
            pileProxy = this
        }
    }

    @Synchronized
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: CagePile<T>) {
        cage.removes(key)
        value.forEach {
            cage.add(key, it)
        }
    }
}