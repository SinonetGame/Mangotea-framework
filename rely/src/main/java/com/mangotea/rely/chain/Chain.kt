package com.mangotea.rely.chain

import com.mangotea.rely.*
import kotlinx.coroutines.*


/**
 * 任务链
 * 将多个任务节点[Knot]组装成任务链[Chain]，并交由该对象进行统一调度和管理
 */
class Chain<E>(cause: Knot<*, E>) {
    /**
     * 当前任务链所进行到的链节点位置（包含起止节点）
     */
    var currentStep: Int = UNDO

    /**
     * 任务链节点总数（包含起止节点）
     */
    var stepCount: Int = 0

    /**
     * 任务链别名
     * 标识字段，无实际意义
     */
    var alias: String

    private var destroyed = false
    private val cancellMessage = "CANCEL_CHAIN_KNOT"
    private var timeout = 0L
    private var leastTime = 0L
    private var job: Job? = null

    private var _onFailure: (Box.(Chain<E>, Throwable) -> Unit)? = null
    private var _onCancel: (Box?.(Chain<E>) -> Unit)? = null
    private var _onTimeout: (Box.(Chain<E>) -> Unit)? = null
    private var _finally: (Box.(Chain<E>) -> Unit)? = null
    private var _onStart: (Box.(Chain<E>) -> Unit)? = null
    private var _onBreak: (Box.(Chain<E>, String?) -> Unit)? = null

    private var _cause: Knot<*, E>? = null

    init {
        alias = "CHAIN-$currentTimeMillis"
        _cause = cause
        findOrigin(cause)
    }

    /**
     * 任务链别名
     * @param block 别名生成方法
     * @return 任务链
     */
    fun alias(block: () -> String): Chain<E> {
        alias = block()
        return this
    }

    /**
     * 捕获到异常时触发
     * [TimeoutCancellationException] [CancellationException] 除外
     * @param block 异常处理模块
     * @return 任务链
     */
    fun onFailure(block: Box.(Chain<E>, Throwable) -> Unit): Chain<E> {
        _onFailure = block
        return this
    }

//    /**
//     * 当手动取消任务链时触发
//     * 仅仅在捕获到[CancellationException]异常且message等于[cancellMessage]时才会派发该事件
//     * @return 任务链
//     */
//    fun onCancel(block: Box?.(Chain<E>) -> Unit): Chain<E> {
//        _onCancel = block
//        return this
//    }

    /**
     * 当运行超时后触发
     * @param time 如果[time]小于1 则视为无超时逻辑，单位毫秒
     * @param block 超时处理模块
     * @return 任务链
     */
    fun onTimeout(time: Long, block: (Box.(Chain<E>) -> Unit)? = null): Chain<E> {
        timeout = time
        _onTimeout = block
        return this
    }

    /**
     * 任务链执行完成后一定会调用
     * @param block 处理模块
     * @return 任务链
     */
    fun onFinally(leastTime: Long = 0L, block: Box.(Chain<E>) -> Unit): Chain<E> {
        this.leastTime = leastTime
        _finally = block
        return this
    }

    /**
     * 任务链开始执行前调用
     * @param block 处理模块
     * @return 任务链
     */
    fun onStart(block: Box.(Chain<E>) -> Unit): Chain<E> {
        _onStart = block
        return this
    }

    /**
     * 任务链开始执行前调用
     * @param block 处理模块
     * @return 任务链
     */
    fun onBreak(block: Box.(Chain<E>, String?) -> Unit): Chain<E> {
        _onBreak = block
        return this
    }

    /**
     * 开始执行任务链，该方法会阻塞协程
     * @param dispatcher 指定对应协程
     * @param return 返回最后一个链节点的处理结果
     */
    suspend fun await(dispatcher: CoroutineDispatcher = childCoroutine, vararg pair: Pair<Any, Any?>): E? =
            async(dispatcher) {
                try {
                    if (_cause == null)
                        throw Throwable("this chain was destoryed!")
                    _cause?.let { theCause ->
//                    theCause.box.mainJob=this
                        val cTime = currentTimeMillis
                        runCatching {
                            pair.forEach { theCause.box.add(*pair) }
                            _onStart?.invoke(theCause.box, this@Chain)
                            if (timeout > 0)
                                withTimeout(timeout) {
                                    theCause.start()
                                }
                            else
                                theCause.start()
                        }.onFailure { throwable ->
                            when (throwable) {
                                is BreakChainThrowable -> _onBreak?.invoke(
                                        theCause.box,
                                        this@Chain, throwable.message
                                )
                                is TimeoutCancellationException -> _onTimeout?.invoke(
                                        theCause.box,
                                        this@Chain
                                )
                                is CancellationException -> {
                                    if (throwable.message == cancellMessage)
                                        _onCancel?.invoke(theCause.box, this@Chain)
                                }
                                else -> {
                                    throwable.e("CHAIN-ERROR")
                                    _onFailure?.invoke(theCause.box, this@Chain, throwable)
                                }
                            }
                        }.getOrNull().apply {
                            await {
                                val usedTime = currentTimeMillis - cTime
                                if (usedTime < leastTime) {
                                    delay(leastTime - usedTime)
                                }
                            }
                            currentStep = DONE
                            _finally?.invoke(theCause.box, this@Chain)
                            next?.run { first.call(second) }
                        }
                    }
                } catch (e: CancellationException) {
                    if (e.message == cancellMessage) {
                        _onCancel?.invoke(_cause?.box, this@Chain)
                    }
                    null
                }
            }.apply { job = this }.await()


    /**
     * 开启一个协程块并执行任务链
     * @param dispatcher 指定对应协程
     * @return 返回协程块所对应的[Job]
     */
    fun call(dispatcher: CoroutineDispatcher = childCoroutine, vararg pair: Pair<Any, Any?>) =
            launch {
                if (!destroyed) {
                    currentStep = REDAY
                    await(dispatcher, *pair)
                }
            }

    /**
     * 启用任务链并在最终销毁。
     *   通过该方式启动的任务链的onFinally会被覆盖，仅适合简单的单次调用任务链
     * @param dispatcher 指定对应协程
     * @return 返回协程块所对应的[Job]
     */
    fun single(dispatcher: CoroutineDispatcher = childCoroutine, vararg pair: Pair<Any, Any?>) =
            onFinally {
                it.destory()
            }.call(dispatcher, *pair)

    /**
     * 终止任务链并抛出[CancellationException]异常，指定其message为[cancellMessage]
     */
    fun cancel() {
        job?.cancel(CancellationException(cancellMessage))
    }

    private var next: Pair<Chain<*>, CoroutineDispatcher>? = null

    /**
     * 串联任务队列
     */
    fun connect(chain: Chain<*>, cd: CoroutineDispatcher = childCoroutine): Chain<*> {
        val extremity = findExtremity(this)
        extremity.next = chain to cd
        if (extremity.currentStep == DONE || extremity.destroyed) {
            chain.call(cd)
        }
        return chain
    }

    private fun findExtremity(chain: Chain<*>): Chain<*> {
        val next = chain.next?.first
        return if (next == null) chain else findExtremity(next)
    }



    val box: Box?
        get() = _cause?.box

    /**
     * 销毁任务链
     * 销毁前，会终止正在执行的任务链
     * 如果不调用该方法，Chain可能会长驻于内存当中
     */
    fun destory() {
        cancel()
        destroyed = true
        _cause?.destroy()
        timeout = 0L
        stepCount = 0
        job = null
        _onFailure = null
        _onCancel = null
        _onTimeout = null
        _finally = null
        _onStart = null
        _cause = null
    }

    private fun findOrigin(knot: Knot<*, *>) {
        knot.chain = this
        stepCount++
        knot.originKnot?.let {
            findOrigin(it)
        }
    }

    override fun toString(): String {
        val step = when (currentStep) {
            UNDO -> "UNDO"
            DONE -> "DONE"
            else -> if (currentStep < 0) "UNKNOWN" else currentStep.toString()
        }
        return "{alias:\"$alias\",currentStep:$step,stepCount:$stepCount}"
    }

    companion object {
        /**
         * 用以标识任务链尚未开始
         */
        const val UNDO = -1

        /**
         * 用以标识任务链执行结束
         */
        const val DONE = -2

        /**
         * 任务已经开始，但未进入链节点
         */
        const val REDAY = -3

        val Chain<*>.executing
            get() = currentStep >= 0 || currentStep == REDAY

    }
}



