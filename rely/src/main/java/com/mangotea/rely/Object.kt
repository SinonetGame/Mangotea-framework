package com.mangotea.rely

import android.graphics.Bitmap
import android.os.Build
import kotlinx.coroutines.CancellationException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.lang.ref.WeakReference
import java.security.MessageDigest
import java.util.*

fun <K, V> MutableMap<K, V>.add(vararg params: Pair<K, V>): MutableMap<K, V> {
    params.forEach {
        this[it.first] = it.second
    }
    return this
}

inline fun <T> Boolean.isIf(predicate: () -> T?) = if (this) predicate() else null
inline fun Boolean.notIf(predicate: () -> Unit) = if (!this) predicate() else null

inline fun Boolean.trueLet(predicate: () -> Unit): Boolean {
    if (this)
        predicate()
    return this
}

inline fun Boolean.trueWith(predicate: () -> Boolean): Boolean {
    if (this)
        return predicate()
    return this
}

inline fun Boolean.falseLet(predicate: () -> Unit): Boolean {
    if (!this)
        predicate()
    return this
}

inline fun Boolean.falseWith(predicate: () -> Boolean): Boolean {
    if (!this)
        return predicate()
    return this
}

inline fun <T, R> T.useRun(f: Function1<T, R>): R = f(this)

fun <T> Collection<T>.random(count: Int): ArrayList<T> {
    val data = arrayListOf<T>()
    if (isNotEmpty()) for (i in 1..count) {
        data.add(this.random())
    }
    return data
}

fun <T> Collection<T>.hashRandom(count: Int): ArrayList<T> {
    val data = arrayListOf<T>()
    data.addAll(this)
    data.shuffle()
    var rc = size - count
    if (rc > 0)
        for (i in 1..rc) {
            data.remove(data.random())
        }
    return data
}

/**
 * 安全截取List 0到指定项的列表，如果不足则取完
 * fromIndex (包含)
 * toIndex （不包含）
 */
fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> {
    return if (fromIndex >= size || fromIndex == toIndex) arrayListOf()
    else if (toIndex >= size) subList(fromIndex, size)
    else subList(fromIndex, toIndex)
}

inline fun <T> Iterable<T>.containsAll(
    elements: Collection<@UnsafeVariance T>,
    eq: T.(T) -> Boolean
): Boolean {
    forEach { o ->
        elements.find { i -> o.eq(i) }?.let { return false }
    }
    return true
}

fun Long.clockFormat(unit: CTimeUnit = CTimeUnit.MILLI, omitHour: Boolean = true): String {
    var rtime = this * unit.modulus
    val hour: Long
    var minute: Long
    val second: Long
    rtime /= 1000
    return if (rtime <= 0)
        if (!omitHour) "00:00:00" else "00:00"
    else {
        minute = rtime / 60
        if (minute < 60) {
            second = rtime % 60
            "${if (!omitHour) "00:" else ""}${minute.unitFormat}:${second.unitFormat}"
        } else {
            hour = minute / 60
            minute %= 60
            second = rtime - hour * 3600 - minute * 60
            "${hour.unitFormat}:${minute.unitFormat}:${second.unitFormat}"
        }
    }
}

val Long.unitFormat: String get() = if (this in 0..9) "0$this" else toString()

enum class CTimeUnit(var modulus: Long) {
    HOUR(60 * 60 * 1000), MINUTE(60 * 1000), SECONDS(1000), MILLI(1)
}

val CharSequence.md5: String
    get() = if (isEmpty()) ""
    else runCatching {
        val md5 = MessageDigest.getInstance("MD5")
        val bytes = md5.digest(this.toString().toByteArray(Charsets.UTF_8))
        val result = StringBuilder()
        for (b in bytes) {
            var temp = Integer.toHexString(b.toInt() and 0xff)
            if (temp.length == 1) {
                temp = "0$temp"
            }
            result.append(temp)
        }
        result.toString()
    }.getOrNull() ?: ""

val String.base64Encode: String
    get() = android.util.Base64.encodeToString(
        toByteArray(Charsets.UTF_8),
        android.util.Base64.DEFAULT
    )
val String.base64Decode: String
    get() = android.util.Base64.decode(this, android.util.Base64.DEFAULT).let {
        String(it, Charsets.UTF_8)
    }


fun String.insert(str: String, offset: Int = 0) = StringBuilder(this).insert(offset, str).toString()

fun String.fill(char: Char, max: Int, offset: Int = 0): String {
    return if (length < max) {
        var str = ""
        for (i in 1..max - length) {
            str += char
        }
        insert(str, offset)
    } else
        this
}

fun Any?.readByteArray(): ByteArray? {
    var bytes: ByteArray? = null
    val bos = ByteArrayOutputStream()
    var oos: ObjectOutputStream? = null

    if (bytes != null) {
        try {
            oos = ObjectOutputStream(bos)
            oos.writeObject(this)
            oos.flush()
            bytes = bos.toByteArray()
        } catch (var13: IOException) {
            var13.w()
        } finally {
            try {
                oos?.close()
                bos.close()
            } catch (var12: IOException) {
                var12.w()
            }

        }
    }
    return bytes
}

fun Bitmap.bmpToByteArray(needRecycle: Boolean = true): ByteArray {
    val output = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, output)
    if (needRecycle) {
        this.recycle()
    }

    val result = output.toByteArray()
    try {
        output.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return result
}


fun <E> MutableList<E>.replaceFirst(item: E, filter: (E) -> Boolean): Boolean {
    forEachIndexed { i, e ->
        if (filter(e)) {
            this[i] = item
            return true
        }
    }
    return false
}

fun <E> MutableList<E>.replaceFinds(item: E, filter: (E) -> Boolean): Boolean {
    var replaced = false
    forEachIndexed { i, e ->
        if (filter(e)) {
            this[i] = item
            replaced = true
        }
    }
    return replaced
}

inline fun <T> List<T>.findWithIndex(predicate: (T) -> Boolean): Pair<Int, T>? {
    forEachIndexed { i, it ->
        if (predicate(it)) {
            return i to it
        }
    }
    return null
}


fun <E> MutableCollection<E>._removeIf(filter: (E) -> Boolean): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        removeIf { filter(it) }
    } else {
        var removed = false
        val each = iterator()
        while (each.hasNext()) {
            if (filter(each.next())) {
                each.remove()
                removed = true
            }
        }
        removed
    }
}


fun <E> MutableList<E>.synchronizedRemoveIf(filter: (E) -> Boolean): Boolean {
    val synchronizedList = Collections.synchronizedList<E>(this)
    return synchronized(synchronizedList) {
        var removed = false
        val each = iterator()
        while (each.hasNext()) {
            if (filter(each.next())) {
                each.remove()
                removed = true
            }
        }
        removed
    }
}

/**
 * 源list和目标list内容都需保证不一致
 */
fun <T> List<T>.diffrentOf(target: List<T>): List<T> {
    val diff: MutableList<T> = ArrayList()
    val map: MutableMap<T, Int> = HashMap(size + target.size)
    var maxList = this
    var minList = target
    if (target.size > size) {
        maxList = target
        minList = this
    }
    for (item in maxList) {
        map[item] = 1
    }
    for (item in minList) {
        var count = map[item]
        if (count != null) {
            map[item] = ++count
            continue
        }
        map[item] = 1
    }
    for ((key, value) in map) {
        if (value == 1) {
            diff.add(key)
        }
    }
    return diff
}

inline fun <K, V> MutableMap<K, V>.removeIf(filter: (Map.Entry<K, V>) -> Boolean): Boolean {
    var removed = false
    val each = iterator()
    while (each.hasNext()) {
        if (filter(each.next())) {
            each.remove()
            removed = true
        }
    }
    return removed
}

inline fun <K, V> MutableMap<K, V>.removeAll(key: Collection<K>): Collection<V?> {
    var removed = arrayListOf<V?>()
    key.forEach { removed.add(remove(it)) }
    return removed
}

fun <T> Any?.asto(blo: (T.() -> Unit)? = null) = (this as? T)?.apply { blo?.let { it() } }

fun <T> T.weakRefrence() = WeakRef(this)

class WeakRef<T> internal constructor(any: T) {
    private val weakRef = WeakReference(any)
    operator fun invoke(): T = weakRef.get() ?: throw  CancellationException() as Throwable
}

typealias Blo = () -> Unit
typealias Sub<T> = T.() -> Unit
typealias Pre<T> = (T) -> Unit
typealias Phr<S, P> = S.(P) -> Unit

typealias BloRes<R> = () -> R
typealias SubRes<T, R> = T.() -> R
typealias PreRes<T, R> = (T) -> R
typealias PhrRes<S, P, R> = S.(P) -> R