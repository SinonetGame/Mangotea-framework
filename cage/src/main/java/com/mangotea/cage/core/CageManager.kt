package com.mangotea.cage.core

import android.app.Application
import com.mangotea.cage.core.proxy.*
import io.paperdb.Paper

internal object CageManager {
    internal var cage: Cage? = null

    fun init(cage: Cage) {
        CageManager.cage = cage
    }
}

val cage get() = CageManager.cage ?: throw Throwable("Cage is not initialized!")

fun Application.initPaperCage() {
    Paper.init(this)
    CageManager.init(ArrayPaperCage(1))
    Settings.init(this)
}

fun <T> cage(key: String) = NormalProxy<T>(key)

fun <T> cage(key: String, value: T) = DefaultValueProxy(key, value)

fun <T> safeCage(key: String) = SafeProxy<T>(key)

fun <T> pileCage(key: String) = PileProxy<T>(key)

fun <T> uniquePileCage(key: String) = UniquePileProxy<T>(key)
