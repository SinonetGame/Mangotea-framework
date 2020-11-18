package com.mangotea.view.component.cover

import android.view.View
import com.mangotea.rely.Blo

interface ICoverView<V : View> {

    operator fun <T> get(key: String): T?
    operator fun set(key: String, value: Any)

    fun entrust(key: String, value: Any)

    fun cover()

    fun fade()

    fun beforeCover(block: Blo)

    fun afterCover(block: Blo)

    fun beforeFade(block: Blo)

    fun afterFade(block: Blo)

    val cover: V
}