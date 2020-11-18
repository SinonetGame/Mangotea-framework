package com.mangotea.view.component.cover

import android.view.View
import com.mangotea.rely.Blo

abstract class CoverView<V : View> : ICoverView<V> {
    private var _isCovering = false
    val isCovering
        get() = _isCovering

    override fun cover() {
        if (isCovering)
            return
        _beforeStart?.invoke()
        onCover()
        _isCovering = true
        _afterStarted?.invoke()
    }

    override fun fade() {
        _beforeEnd?.invoke()
        entrustMap.clear()
        onFade()
        _isCovering = false
        _afterEnded?.invoke()
    }

    open fun onCover() {}

    open fun onFade() {}

    private val entrustMap by lazy { hashMapOf<String, Any>() }

    override operator fun <T> get(key: String): T? = entrustMap[key] as? T
    override operator fun set(key: String, value: Any) {
        entrust(key, value)
    }

    override fun entrust(key: String, value: Any) {
        entrustMap[key] = value
    }

    private var _beforeStart: Blo? = null
    override fun beforeCover(block: Blo) {
        _beforeStart = block
    }

    private var _afterStarted: Blo? = null
    override fun afterCover(block: Blo) {
        _afterStarted = block
    }

    private var _beforeEnd: Blo? = null
    override fun beforeFade(block: Blo) {
        _beforeEnd = block
    }

    private var _afterEnded: Blo? = null
    override fun afterFade(block: Blo) {
        _afterEnded = block
    }
}