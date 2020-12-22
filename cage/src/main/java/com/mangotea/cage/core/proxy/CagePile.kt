package com.mangotea.cage.core.proxy

import com.mangotea.cage.core.cage


open class CagePile<T>(private val key: String) : MutableCollection<T>, List<T> {

    private val pile
        @Synchronized
        get() = cage.all<T>(key)
    override val size: Int
        get() = pile.size

    override fun contains(element: T): Boolean = pile.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = pile.containsAll(elements)

    override fun isEmpty(): Boolean = size <= 0

    override fun iterator(): MutableIterator<T> = pile.iterator()

    override fun add(element: T): Boolean = cage.add(key, element)

    override fun addAll(elements: Collection<T>): Boolean = cage.adds(key, elements)

    override fun clear() {
        cage.removes(key)
    }

    override fun remove(element: T): Boolean = cage.remove<T>(key) { it == element }

    override fun removeAll(elements: Collection<T>): Boolean =
        cage.removes<T>(key) { elements.contains(it) }

    override fun retainAll(elements: Collection<T>): Boolean =
        cage.removes<T>(key) { !elements.contains(it) }

    open fun removeIf(where: (T) -> Boolean) = cage.removes(key, where)

    open fun find(where: (T) -> Boolean) = cage.find(key, where)

    open fun replace(where: (T) -> T?) = cage.replace(key, where)

    open fun replaceOrAdd(value: T, where: (T) -> Boolean) = cage.replaceOrAdd(key, value, where)

    open fun updateOrAdd(new: T, where: (T) -> T?) = cage.updateOrAdd(key, new, where)

    override fun get(index: Int): T = pile[index]

    override fun indexOf(element: T): Int = pile.indexOf(element)

    override fun lastIndexOf(element: T): Int = pile.lastIndexOf(element)

    override fun listIterator(): ListIterator<T> = pile.listIterator()

    override fun listIterator(index: Int): ListIterator<T> = pile.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = pile.subList(fromIndex, toIndex)

}