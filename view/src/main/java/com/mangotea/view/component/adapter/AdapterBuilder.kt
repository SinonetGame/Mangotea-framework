package com.mangotea.view.component.adapter

import android.view.View

interface AdapterBuilder<E, T> {
    fun render(renderItemBlock: View.(item: E, i: Int, type: Int) -> Unit): T

    fun update(renderItemBlock: View.(item: E, i: Int, type: Int, payLoads: MutableList<Any>) -> Unit): T

    fun itemClick(blo: (View, Int, E) -> Unit): T

    fun itemClick(blo: (E) -> Unit): T

    fun itemViewClick(vararg ids: Int, blo: (View, Int, E) -> Unit): T

    fun typeFinder(blo: (Int, E) -> Int): T

    fun items(list: Collection<E>): T

}