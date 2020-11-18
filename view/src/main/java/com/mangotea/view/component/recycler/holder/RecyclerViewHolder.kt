package com.mangotea.view.component.recycler.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewHolder<T>(
    rootView: View,
    private val type: Int,
    private val renderItem: View.(item: T, i: Int, type: Int) -> Unit,
    private val renderItemWithPayloads: View.(item: T, i: Int, type: Int, payloads: MutableList<Any>) -> Unit
) : RecyclerView.ViewHolder(rootView) {

    fun renderView(item: T, i: Int) {
        itemView.renderItem(item, i, type)
    }

    fun renderView(item: T, i: Int, payloads: MutableList<Any>) {
        itemView.renderItemWithPayloads(item, i, type, payloads)
    }
}
