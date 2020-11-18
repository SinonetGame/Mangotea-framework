package com.mangotea.view.component.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import org.jetbrains.anko.AnkoContext

open class DataBaseAdapter<T, V : View>(protected val creator: AnkoContext<ViewGroup>.() -> V) : BaseAdapter() {
    protected val data by lazy { arrayListOf<T>() }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val itemView = creator(AnkoContext.create(parent.context, parent))
        _renderItemBlock?.invoke(itemView, data[position], position)
        return itemView
    }

    override fun getItem(position: Int): T = data[position]

    override fun getItemId(position: Int): Long = data[position].hashCode().toLong()

    override fun getCount(): Int = data.size

    protected var _renderItemBlock: (V.(item: T, i: Int) -> Unit)? = null

    fun render(renderItemBlock: V.(item: T, i: Int) -> Unit): DataBaseAdapter<T,V> {
        this._renderItemBlock = renderItemBlock
        return this
    }

    fun items(datas: Collection<T>): DataBaseAdapter<T,V> {
        data.clear()
        data.addAll(datas)
        notifyDataSetChanged()
        return this
    }

}