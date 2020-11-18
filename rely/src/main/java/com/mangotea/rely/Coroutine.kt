package com.mangotea.rely

import kotlinx.coroutines.*

val mainCoroutine = Dispatchers.Main

val childCoroutine = Dispatchers.Default

fun launch(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit
): Job = GlobalScope.launch(dispatcher, block = block)

fun <T> async(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = GlobalScope.async(dispatcher, block = block)


fun <T> catch(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend CoroutineScope.() -> T
): Deferred<T?> = GlobalScope.async(dispatcher, block = {
    try {
        block()
    } catch (e: Throwable) {
        e.e("CATCH-ERROR")
        null
    }
})

fun <T> ui(block: suspend CoroutineScope.() -> T): Deferred<T> =
    async(mainCoroutine, block)

suspend fun <T : Any?> await(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend CoroutineScope.() -> T
): T = async(dispatcher, block).await()

