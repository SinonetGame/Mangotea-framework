package com.mangotea.cage.core.proxy

class UniqueCagePile<T>(key: String) : CagePile<T>(key) {

    override fun add(element: T): Boolean = if (!contains(element)) super.add(element) else true

    override fun addAll(elements: Collection<T>): Boolean {
        val contains = elements.filter { !contains(it) }
        if (contains.isNotEmpty())
            return super.addAll(contains)
        return true
    }

    @Deprecated("不可用", ReplaceWith("false"), DeprecationLevel.ERROR)
    override fun replace(where: (T) -> T?): Boolean {
        throw RuntimeException()
        return false
    }

    @Deprecated("不可用", ReplaceWith("false"), DeprecationLevel.ERROR)
    override fun replaceOrAdd(value: T, where: (T) -> Boolean): Boolean {
        throw RuntimeException()
        return false
    }

    @Deprecated("不可用", ReplaceWith("false"), DeprecationLevel.ERROR)
    override fun updateOrAdd(new: T, where: (T) -> T?): Boolean {
        throw RuntimeException()
        return false
    }

}