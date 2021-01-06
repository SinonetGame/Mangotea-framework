@file:Suppress("UNCHECKED_CAST")

package com.mangotea.cage.core

import android.app.Application
import com.mangotea.rely.WeakRef
import com.mangotea.rely.data
import com.mangotea.rely.dataRemove
import com.mangotea.rely.weakRefrence
import java.io.Serializable


/**
 * 系统配置中心
 * 配置中心所保存的对象必须实现序列化<Serializable>接口
 * 且保存后不会被自动清除
 */
object Settings {
    private lateinit var app: WeakRef<Application>
    fun init(application: Application) {
        app = application.weakRefrence()
    }

    /**
     * 参数不能使用double 或 date
     */
    operator fun <T> get(key: String): T? {
        return cage["setting-$key"] ?: (app().data<Serializable>("setting-$key") as? T)?.apply {
            set(key, this)
            app().dataRemove("setting-$key")
        }
    }

    operator fun <T> get(key: String, defValue: T): T {
        return get(key) ?: defValue.apply { set(key, this) }
    }

    operator fun <T> set(key: String, value: T) {
        val old: Any? = get(key)
        cage["setting-$key"] = value
        listners.forEach {
            it(key, value, old)
        }
    }

    fun remove(key: String) {
        val old: Any? = get(key)
        cage.delete("setting-$key")
        app().dataRemove("setting-$key")
        listners.forEach {
            it(key, null, old)
        }
    }

    private val listners by lazy { hashSetOf<(key: String, new: Any?, old: Any?) -> Unit>() }
    fun addSettingChangedListener(blo: (key: String, new: Any?, old: Any?) -> Unit) {
        listners.add(blo)
    }

    fun removeSettingChangedListner(blo: (key: String, new: Any?, old: Any?) -> Unit) {
        listners.remove(blo)
    }

}

infix fun <V : Serializable> String.set(that: V) {
    Settings[this] = that
}

infix fun <V : Serializable?> String.get(def: V?): V? {
    return if (def == null) {
        Settings.get<Serializable>(this) as? V
    } else {
        Settings.get<Serializable>(this, def) as? V
    }
}

fun <V : Serializable?> String.get(): V? {
    return Settings.get<Serializable>(this) as? V
}


