package com.mangotea.view.component.recycler.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

open class FragmentAdapter<P> : FragmentStateAdapter {
    private val _datas: MutableList<P>

    constructor(fragmentActivity: FragmentActivity, datas: MutableList<P>) : super(fragmentActivity) {
        _datas = datas
    }

    constructor(fragment: Fragment, datas: MutableList<P>) : super(fragment) {
        _datas = datas
    }

    constructor(fragmentManager: FragmentManager, lifecycle: Lifecycle, datas: MutableList<P>) : super(fragmentManager, lifecycle) {
        _datas = datas
    }

    private var _createFragment: (P.(Int) -> Fragment)? = null
    private val _fragments by lazy { linkedMapOf<Int, Fragment>() }

    override fun getItemCount(): Int = _datas.size

    override fun createFragment(position: Int): Fragment {
        return _fragments[position] ?: _createFragment?.let { _datas[position]?.it(position) }?.apply {
            _fragments[position] = this
        } ?: throw NullPointerException()
    }

    fun render(renderItemBlock: P.(Int) -> Fragment): FragmentAdapter<P> {
        _createFragment = renderItemBlock
        return this
    }

    fun itemFragment(position: Int) = _fragments[position]
}

