package com.mangotea.rely.io

import com.mangotea.rely.currentTime
import com.mangotea.rely.millisBetween
import java.io.Serializable
import java.util.*

class CacheObejct<T> : Serializable {
    val value: T
    val shelfLife: Long
    val cacheTime: Date

    val isExpired: Boolean
        get() {
            val millis = cacheTime.millisBetween()
            return this.shelfLife != -1L && this.shelfLife - millis <= 0L
        }

    constructor(value: T, deadline: Date) : this(value, currentTime.millisBetween(deadline))

    @JvmOverloads
    constructor(value: T, shelfLife: Long = -1L) {
        this.value = value
        this.shelfLife = shelfLife
        this.cacheTime = Date()
    }

    companion object {
        //1.4.5版本serialVersionUID为-510693636506077113，故设置为-510693636506077113
        private const val serialVersionUID = -510693636506077113L
    }

}
