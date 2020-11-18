package com.mangotea.cage.core

import com.mangotea.rely.e
import com.mangotea.rely.w
import io.paperdb.Paper

/**
 * 一个支持类自动升级(添加或删除字段)的快速NoSQL数据存储，可保存任何类型的Plain Old Java Objects或使用Kryo序列化的集合。
 * 每个自定义类都必须具有无参数构造函数，或者可以直接使用[kotlin]中的 data class。开箱即用地支持通用类。
 * <p/>
 * 自动升级的工作方式为：在旧版本上忽略新版本已删除的字段，并为新增的字段设置默认值。
 * <p/>
 * 每个对象都保存在单独的Paper文件中，其名称类似于object_key.pt。
 * 所有Paper文件均在应用程序专用存储区的/files/io.paperdb目录中创建。
 */
open class PaperCage(override var version: Int) : Cage {

    protected val book
        @Synchronized get() = Paper.book()

    @Synchronized
    override fun <T> set(key: String, value: T) = ifCatching { book.write(key, value).let { true } }

    @Synchronized
    override fun <T> get(key: String): T? = kotlin.runCatching { book.read<T>(key) }.getOrElse {
        e(it)
        null
    }

    override fun <T> use(key: String, block: T.() -> Unit) = get<T>(key)?.block() ?: Unit

    @Synchronized
    override fun delete(key: String) = ifCatching { book.delete(key).let { true } }

    @Synchronized
    override fun <T> edit(key: String, edit: T.() -> T?): T? {
        return get<T>(key)?.let<T, T?> {
            it.edit()?.let { result ->
                if (set(key, result))
                    result
                else null
            }
        }
    }

    @Synchronized
    override fun contains(key: String): Boolean = book.contains(key)

    @Synchronized
    override fun <T> add(key: String, value: T) = ifCatching {
        if (!key.setPile())
            return@ifCatching false
        var keys = get<HashSet<Int>>(key)
        val max = (keys?.max() ?: 0) + 1
        if (keys == null) {
            keys = hashSetOf()
        }
        keys.add(max)
        if (!set(key, keys))
            return@ifCatching false
        var itemKey = itemKey(key, max)
        set(itemKey, value)
    }

    @Synchronized
    override fun <T> adds(key: String, values: Iterable<T>) = ifCatching {
        var success = true
        values.forEach {
            success = add(key, it) && success
        }
        success
    }

    override fun <T> one(key: String): T? = find<T>(key) { true }

    override fun <T> all(key: String): ArrayList<T> = finds(key) { true }

    override fun <T> each(key: String, block: (T) -> Unit) {
        if (!isPile(key))
            return
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            get<T>(itemKey(key, it))?.let { item ->
                block(item)
            }
        }
    }

    @Synchronized
    override fun <T> find(key: String, where: (T) -> Boolean): T? {
        if (!isPile(key))
            return null
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            get<T>(itemKey(key, it))?.let { item ->
                if (where(item))
                    return item
            }
        }
        return null
    }

    override fun <T> finds(key: String): ArrayList<T> {
        val array = arrayListOf<T>()
        if (!isPile(key))
            return array
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            get<T>(itemKey(key, it))?.let { item ->
                array.add(item)
            }
        }
        return array
    }

    @Synchronized
    override fun <T> finds(key: String, where: (T) -> Boolean): ArrayList<T> {
        val array = arrayListOf<T>()
        if (!isPile(key))
            return array
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            get<T>(itemKey(key, it))?.let { item ->
                if (where(item))
                    array.add(item)
            }
        }
        return array
    }

    @Synchronized
    override fun <T> replace(key: String, where: (T) -> T?) = ifCatching {
        if (!isPile(key)) return@ifCatching true
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            val itemKey = itemKey(key, it)
            get<T>(itemKey)?.let { old ->
                where(old)?.let { new ->
                    return@ifCatching set(itemKey, new)
                }
            }
        }
        true
    }

    override fun <T> replaceOrAdd(key: String, value: T, where: (T) -> Boolean) = ifCatching {
        if (!isPile(key)) return@ifCatching true
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            val itemKey = itemKey(key, it)
            get<T>(itemKey)?.let { old ->
                if (where(old)) return@ifCatching set(itemKey, value)
            }
        }
        add(key, value)
    }

    @Synchronized
    override fun <T> updateOrAdd(key: String, new: T, where: (T) -> T?) = ifCatching {
        if (!isPile(key)) return@ifCatching true
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            val itemKey = itemKey(key, it)
            get<T>(itemKey)?.let { old ->
                val value = where(old)
                if (value != null) return@ifCatching set(itemKey, value)
            }
        }
        add(key, new)
    }

    @Synchronized
    override fun <T> replaces(key: String, where: (T) -> T?) = ifCatching {
        if (!isPile(key)) return@ifCatching true
        var success = true
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            val itemKey = itemKey(key, it)
            get<T>(itemKey)?.let { old ->
                where(old)?.let { new ->
                    success = set(itemKey, new) && success
                }
            }
        }
        success
    }

    @Synchronized
    override fun <T> remove(key: String, where: (T) -> Boolean) = ifCatching {
        if (!isPile(key))
            return@ifCatching true
        var success = true
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            val itemKey = itemKey(key, it)
            get<T>(itemKey)?.let { item ->
                if (where(item)) {
                    success = if (delete(itemKey)) keys.remove(it) else false
                    return@forEach
                }
            }
        }

        if (keys.isNullOrEmpty())
            key.setPile(false)
        set(key, keys) && success
    }

    override fun removes(key: String): Boolean = ifCatching {
        if (!isPile(key))
            return@ifCatching true
        var keys = get<HashSet<Int>>(key)
        val successed = hashSetOf<Int>()
        keys?.forEach {
            val itemKey = itemKey(key, it)
            if (delete(itemKey))
                successed.add(it)
        }
        keys?.removeAll(successed)
        if (keys.isNullOrEmpty())
            key.setPile(false)
        set(key, keys)
    }


    @Synchronized
    override fun <T> removes(key: String, where: (T) -> Boolean) = ifCatching {
        if (!isPile(key))
            return@ifCatching true
        var success = true
        var keys = get<HashSet<Int>>(key)
        keys?.forEach {
            val itemKey = itemKey(key, it)
            get<T>(itemKey)?.let { item ->
                if (where(item)) {
                    success = (if (delete(itemKey)) keys.remove(it) else false) && success
                }
            }
        }

        if (keys.isNullOrEmpty())
            key.setPile(false)
        set(key, keys) && success
    }

    @Synchronized
    override fun pileContains(key: String): Boolean {
        if (!isPile(key))
            return false
        var keys: HashSet<Int> = get<HashSet<Int>>(key) ?: return false
        return !keys.none { contains(itemKey(key, it)) }
    }

    @Synchronized
    override fun clean(key: String) = if (isPile(key)) removes(key) else delete(key)

    @Synchronized
    override fun clean() = ifCatching {
        book.destroy()
        true
    }

    @Synchronized
    override fun isPile(key: String): Boolean = get<Boolean>("pile_${key}_hash_set") ?: false

    @Synchronized
    private fun String.setPile(value: Boolean = true) = if (value) set("pile_${this}_hash_set", true) else delete("pile_${this}_hash_set")

    private fun itemKey(key: String, index: Int) = "pile_${key}_$index"

    private fun ifCatching(blo: () -> Boolean) = runCatching { blo() }.getOrElse {
        it.w(TAG)
        false
    }


}