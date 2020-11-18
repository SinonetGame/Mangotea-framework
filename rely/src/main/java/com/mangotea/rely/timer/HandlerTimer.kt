package com.mangotea.rely.timer

import android.content.Context
import android.os.Handler
import android.os.Message

class HandlerTimer(
        private val what: Int,
        private val period: Long = 0,
        private val delay: Long = 0,
        blo: () -> Unit
) : Handler() {
    private var todo: (() -> Unit)? = blo
    private var work = false

    override fun handleMessage(msg: Message) {
        if (msg.what == what && work) {
            todo?.let { it() }
            runCatching { sendEmptyMessageDelayed(what, period) }
        }
    }

    fun start() {
        if (!work) {
            work = true
            runCatching { sendEmptyMessageDelayed(what, delay) }
        }
    }

    fun stop() {
        work = false
        runCatching { removeMessages(what) }
    }

    fun destory() {
        stop()
        todo = null
    }
}

fun Context.handlerTimer(period: Long = 0, delay: Long = 0, todo: () -> Unit) = HandlerTimer(hashCode(), period, delay, todo)