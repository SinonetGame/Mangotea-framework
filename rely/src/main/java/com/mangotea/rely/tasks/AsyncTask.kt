package com.mangotea.rely.tasks

import com.mangotea.rely.keeps.tasks.TaskState
import com.mangotea.rely.w
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
@Deprecated("Not recommended for use", level = DeprecationLevel.WARNING)
class AsyncTask<T, R>(
    private val taskDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val taraget: T,
    private val miTask: T.(Continuation<R>) -> Unit
) : BaseTask<R>() {

    @Suppress("UNCHECKED_CAST")
    override fun work(dispatcher: CoroutineDispatcher): Task<R> {
        GlobalScope.async(dispatcher) {
            try {
                state = TaskState.EXECUTING
                job = async(taskDispatcher) {
                    runCatching {
                        suspendCoroutine<R> {
                            try {
                                taraget.miTask(it)
                            } catch (e: Throwable) {
                                it.resumeWithException(e)
                            }
                        }
                    }
                }
                val deferred = job as Deferred<Result<R>>
                val result = if (timeMillis > 0) withTimeout(timeMillis) { deferred.await() } else deferred.await()
                onDone?.let { it(result.getOrThrow()) }
            } catch (e: Throwable) {
                when {
                    isTimeout(e) ->if (onTimeout!=null){
                        onTimeout?.let { it() }
                    }else{
                        onFailed?.let { it(e) }
                    }
                    isCancel(e) -> {
                    }
                    else -> {
                        w(e, "AsyncTask Exception")
                        onFailed?.let { it(e) }
                    }
                }
            }
        }

        return this
    }

}