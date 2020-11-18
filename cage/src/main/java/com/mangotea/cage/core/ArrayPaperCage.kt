package com.mangotea.cage.core

import android.os.Build
import com.mangotea.rely._removeIf
import com.mangotea.rely.findWithIndex
import com.mangotea.rely.w


open class ArrayPaperCage(version: Int) : PaperCage(version) {

    private val String.aryKey
        get() = "$this-ArrayPaperCage"

    @Synchronized
    override fun <T> add(key: String, value: T) = ifCatching {
        val ary = get<MutableList<T>>(key.aryKey) ?: arrayListOf()
        ary.add(value) && set(key.aryKey, ary)
    }

    @Synchronized
    override fun <T> adds(key: String, values: Iterable<T>) = ifCatching {
        val ary = get<MutableList<T>>(key.aryKey) ?: arrayListOf()
        ary.addAll(values) && set(key.aryKey, ary)
    }

    override fun <T> each(key: String, block: (T) -> Unit) {
        get<MutableList<T>>(key.aryKey)?.forEach(block)
    }

    override fun <T> find(key: String, where: (T) -> Boolean): T? {
        return get<MutableList<T>>(key.aryKey)?.find(where)
    }

    override fun <T> finds(key: String): ArrayList<T> {
        val array = arrayListOf<T>()
        get<MutableList<T>>(key.aryKey)?.let { array.addAll(it) }
        return array
    }


    override fun <T> finds(key: String, where: (T) -> Boolean): ArrayList<T> {
        val array = arrayListOf<T>()
        get<MutableList<T>>(key.aryKey)?.filter(where)?.let { array.addAll(it) }
        return array
    }

    @Synchronized
    override fun <T> replace(key: String, editFirst: (T) -> T?) = ifCatching {
        val array = get<MutableList<T>>(key.aryKey)
        var newValue: T? = null
        val new = array?.findWithIndex {
            editFirst(it).apply { if (this != null) newValue = this } != null
        }
        if (new != null && new.first >= 0 && newValue != null) {
            array.set(new.first, newValue!!)?.let { set(key.aryKey, array) } ?: false
        } else false
    }

    @Synchronized
    override fun <T> replaceOrAdd(key: String, value: T, where: (T) -> Boolean) = ifCatching {
        val array = get<MutableList<T>>(key.aryKey) ?: arrayListOf()
        val new = array.findWithIndex { where(it) }
        if (new != null && new.first >= 0) {
            array.set(new.first, value)?.let { set(key.aryKey, array) } ?: false
        } else if (array.add(value))
            set(key.aryKey, array)
        else false
    }

    @Synchronized
    override fun <T> updateOrAdd(key: String, new: T, where: (T) -> T?) = ifCatching {
        val array = get<MutableList<T>>(key.aryKey) ?: arrayListOf()
        var value: T? = null
        val pair = array.findWithIndex {
            val value = where(it)
            value != null
        }
        if (pair != null && pair.first >= 0 && value != null) {
            value?.let { array.set(pair.first, it)?.let { set(key.aryKey, array) } } ?: false
        } else if (array.add(new))
            set(key.aryKey, array)
        else false
    }


    @Synchronized
    override fun <T> replaces(key: String, editFirst: (T) -> T?) = ifCatching {
        val array = get<MutableList<T>>(key.aryKey)
        var news = arrayListOf<Pair<Int, T?>>()
        array?.forEachIndexed { i, old ->
            editFirst(old)?.let {
                news.add(i to it)
            }
        }
        var success = true
        news.forEach { new ->
            if (new.second != null && new.first >= 0) {
                success = success && (array?.set(new.first, new.second!!)?.let { true } ?: false)
            }
        }
        success && set(key.aryKey, news)
    }

    @Synchronized
    override fun <T> remove(key: String, where: (T) -> Boolean) = ifCatching {
        val ary = get<MutableList<T>>(key.aryKey)
        ary?.find(where)?.let { ary.remove(it) && set(key.aryKey, ary) } ?: false
    }

    @Synchronized
    override fun removes(key: String): Boolean = ifCatching { delete(key.aryKey) }

    @Synchronized
    override fun <T> removes(key: String, where: (T) -> Boolean) = ifCatching {
        val ary = get<MutableList<T>>(key.aryKey)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ary?.removeIf(where) ?: false
        } else {
            ary?._removeIf(where) ?: false
        } && set(key.aryKey, ary)
    }

    @Synchronized
    override fun pileContains(key: String): Boolean {
        val ary = get<MutableList<*>>(key.aryKey)
        return ary != null && ary.size > 0
    }


    @Synchronized
    override fun isPile(key: String): Boolean = get<Any>(key.aryKey)?.let { it is Collection<*> } ?: false

    private fun ifCatching(blo: () -> Boolean) = runCatching { blo() }.getOrElse {
        it.w(TAG)
        false
    }


}