package com.mangotea.rely

interface Logger {
    fun log(tag: String?, msg: Any?, type: String)
}

class DefaultLogger : Logger {
    override fun log(tag: String?, msg: Any?, type: String) {
        if (BuildConfig.DEBUG || msg is Throwable) {
            println()
            val any = when (msg) {
                null -> "null"
                is Array<*> -> if (msg.isEmpty()) "size-0" else StringBuffer(":\n").apply {
                    msg.forEach {
                        append("\t$it\n")
                    }
                }
                is Iterable<*> -> if (msg.iterator().hasNext()) StringBuffer(":\n").apply {
                    msg.forEach {
                        append("\t$it\n")
                    }
                } else "size-0"
                is Throwable -> StringBuffer(":\n$msg").apply {
                    msg.stackTrace.forEach {
                        append("\tat $it\n")
                    }
                }
                else -> msg
            }

            when (type) {
                "v" -> android.util.Log.v(tag, any.toString())
                "d" -> android.util.Log.d(tag, any.toString())
                "w" -> android.util.Log.w(tag, any.toString())
                "e" -> android.util.Log.e(tag, any.toString())
                else -> android.util.Log.i(tag, any.toString())
            }
        }

    }

}

object Log {
    val default: Logger = DefaultLogger()
    private val loggers: HashSet<Logger> = hashSetOf()

    fun add(logger: Logger) {
        loggers.add(logger)
    }

    fun remove(logger: Logger) {
        loggers.remove(logger)
    }

    fun clear() {
        loggers.clear()
    }

    fun log(tag: String?, msg: Any?, type: String) {
        loggers.forEach {
            it.log(tag, msg, type)
        }
    }
}

fun <T : Any> T.v(tag: Any? = null) {
    log("v", tag?.toString(), this)
}


fun v(msg: Any, tag: String? = null) {
    log("v", tag, msg)
}

fun <T : Any> T.i(tag: Any? = null) {
    log("i", tag?.toString(), this)
}


fun i(msg: Any, tag: String? = null) {
    log("i", tag, msg)
}

fun <T : Any> T.d(tag: Any? = null) {
    log("d", tag?.toString(), this)
}


fun d(msg: Any, tag: String? = null) {
    log("d", tag, msg)
}

fun <T : Any> T.w(tag: Any? = null) {
    log("w", tag?.toString(), this)
}


fun w(msg: Any, tag: String? = null) {
    log("w", tag, msg)
}

fun <T : Any> T.e(tag: Any? = null) {
    log("e", tag?.toString(), this)
}

fun e(msg: Any, tag: String? = null) {
    log("e", tag, msg)
}

fun log(type: String, tag: String?, msg: Any?) {
    Log.log(tag, msg, type)
}

inline fun logCatching(tag: Any? = null, block: () -> Unit) = runCatching(block).getOrElse { it.w(tag) }
