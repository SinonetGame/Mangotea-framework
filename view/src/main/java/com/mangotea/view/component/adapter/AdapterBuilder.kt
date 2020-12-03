package com.mangotea.view.component.adapter

import android.view.View

interface AdapterBuilder<E> {

    fun render(renderItemBlock: View.(item: E, i: Int, type: Int) -> Unit): AdapterBuilder<E>

    fun update(renderItemBlock: View.(item: E, i: Int, type: Int, payLoads: MutableList<Any>) -> Unit): AdapterBuilder<E>

    fun itemClick(blo: (View, Int, E) -> Unit): AdapterBuilder<E>

    fun itemClick(blo: (E) -> Unit): AdapterBuilder<E>

    fun itemViewClick(vararg ids: Int, blo: (View, Int, E) -> Unit): AdapterBuilder<E>

    fun typeFinder(blo: (Int, E) -> Int): AdapterBuilder<E>

    fun items(list: Collection<E>): AdapterBuilder<E>


}