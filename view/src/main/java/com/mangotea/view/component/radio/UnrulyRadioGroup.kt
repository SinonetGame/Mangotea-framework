package com.mangotea.view.component.radio

import android.view.View
import android.view.ViewGroup
import com.mangotea.rely.Blo
import com.mangotea.view.onClick

/**
 * 自由的单选组
 */
class UnrulyRadioGroup<V : ViewGroup>(private val viewGroup: V) {

    private var mCheckedId = -1
    private val selectables by lazy { hashMapOf<Int, IntArray>() }

    private var mOnCheckedChangeListener: ((group: UnrulyRadioGroup<*>, checkedId: Int) -> Unit)? = null
    private var mOnCheckStateChangeListener: ((checkedId: Int, checked: Boolean) -> Unit)? = null

    fun buildItem(viewId: Int, createView: V.(checkEvent: Blo) -> View): UnrulyRadioGroup<V> {
        val event: Blo = { check(viewId) }
        val itemView = viewGroup.createView(event)
        itemView.id = viewId
        selectables[viewId] = intArrayOf(viewId)
        return this
    }

    fun buildItem(createView: V.() -> View): UnrulyRadioGroup<V> {
        val itemView = viewGroup.createView()
        val viewId = itemView.id
        selectables[viewId] = intArrayOf(viewId)
        return this
    }

    /**
     * 该方法所创建的多个View视为同一个选项
     */
    fun buildsItem(itemId: Int? = null, createView: V.() -> List<View>): UnrulyRadioGroup<V> {
        val itemView = viewGroup.createView()
        val ids: IntArray = IntArray(itemView.size)
        itemView.forEachIndexed { index, view -> ids[index] = view.id }
        val checkId = itemId ?: ids[0]
        selectables[checkId] = ids
        return this
    }


    fun buildClickItem(viewId: Int? = null, createView: V.() -> View): UnrulyRadioGroup<V> {
        val itemView = viewGroup.createView()
        if (viewId != null)
            itemView.id = viewId
        selectables[itemView.id] = intArrayOf(itemView.id)
        itemView.onClick { check(itemView.id) }
        return this
    }

    /**
     * 该方法所创建的多个View视为同一个选项
     */
    fun buildsClickItem(itemId: Int? = null, createView: V.() -> List<View>): UnrulyRadioGroup<V> {
        val itemView = viewGroup.createView()
        val ids: IntArray = IntArray(itemView.size)
        val checkId = itemId ?: itemView[0].id
        itemView.forEachIndexed { index, view ->
            ids[index] = view.id
            view.onClick { check(checkId) }
        }
        selectables[checkId] = ids
        return this
    }

    fun check(target: View) {
        check(target.id)
    }

    fun check(targetId: Int) {
        if (targetId != -1 && targetId == mCheckedId) {
            return
        }
        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false)
        }
        if (targetId != -1) {
            setCheckedStateForView(targetId, true)
        }
        setCheckedId(targetId)
    }

    private fun setCheckedId(id: Int) {
        mCheckedId = id
        mOnCheckedChangeListener?.let { it(this, mCheckedId) }
    }

    private fun setCheckedStateForView(itemId: Int, checked: Boolean) {
        selectables[itemId]?.forEach {
            viewGroup.findViewById<View>(it)?.let { view ->
                view.isSelected = checked
                if (it == itemId)
                    mOnCheckStateChangeListener?.let { it(itemId, checked) }
            }
        }

    }

    fun getCheckedRadioButtonId(): Int {
        return mCheckedId
    }

    fun clearCheck() {
        check(-1)
    }

    fun onCheckedChange(listener: (group: UnrulyRadioGroup<*>, checkedId: Int) -> Unit) {
        mOnCheckedChangeListener = listener
    }

    fun onCheckStateChange(listener: (checkedId: Int, checked: Boolean) -> Unit) {
        mOnCheckStateChangeListener = listener
    }

//    fun demo(ctx: Context) {
//        UnrulyRadioGroup(_ConstraintLayout(ctx)).apply {
//            buildRadioItem(0) { check ->
//                textView {
//                    onClick {
//                        check()
//                    }
//                }.lparams {
//
//                }
//            }.buildRadioItem {
//                textView {
//                    onClick {
//                        check(it)
//                    }
//                }.lparams {
//
//                }
//            }
//        }
//    }

}