package com.mangotea.rely.timer

import android.os.Handler
import android.os.Message
import com.mangotea.rely.currentTimeMillis


class HandlerTimer(
    /**
     * unique code
     */
    private val what: Int,
    /**
     * 执行次数
     * <0 暂不执行 0无限次
     */
    private var repeatCount: Int = 0,
    /**
     * 重复间隔时间
     */
    private val period: Long = 0,
    /**
     * 首次延迟时间
     */
    private val delay: Long = 0,
    blo: () -> Unit
) : Handler() {
    var executedCount = 0
        private set

    private var todo: (() -> Unit)? = blo
    private var work = false

    override fun handleMessage(msg: Message) {
        if (msg.what == what && work) {
            todo?.let { it() }
            executedCount++
            if (repeatCount == 0 || executedCount < repeatCount)
                runCatching { sendEmptyMessageDelayed(what, period) }
        }
    }

    fun start() {
        if (!work) {
            work = true
            if (repeatCount == 0 || executedCount < repeatCount) {
                runCatching { sendEmptyMessageDelayed(what, delay) }
            }
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

fun handlerTimer(
    period: Long = 0,
    delay: Long = 0,
    todo: () -> Unit
) =
    HandlerTimer("$currentTimeMillis-${Math.random()}".hashCode(), 0, period, delay, todo)

fun handlerTimer(
    repeatCount: Int,
    period: Long = 0,
    delay: Long = 0,
    todo: () -> Unit
) =
    HandlerTimer("$currentTimeMillis-${Math.random()}".hashCode(), repeatCount, period, delay, todo)