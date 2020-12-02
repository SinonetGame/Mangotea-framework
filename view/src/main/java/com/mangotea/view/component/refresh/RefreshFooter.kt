package com.mangotea.view.component.refresh

import android.content.Context
import android.util.AttributeSet
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import org.jetbrains.anko.AnkoContext


@Suppress("ClassName")
open class _RefreshFooter @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : _InternalAbstract(context, attrs, defStyleAttr), RefreshFooter {

    init {
        AnkoContext.createDelegate(this)
    }

    private var _onFinish: ((RefreshLayout, Boolean) -> Unit)? = null
    private var _onNoMoreData: ((Boolean) -> Boolean)? = null
    private var _initialized: ((kernel: RefreshKernel, height: Int, maxDragHeight: Int) -> Unit)? = null
    private var _startAnimator: ((layout: RefreshLayout, height: Int, maxDragHeight: Int) -> Unit)? = null
    private var _onStateChanged: ((RefreshLayout, RefreshState, RefreshState) -> Unit)? = null
    private var _moving: ((isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) -> Unit)? =
        null

    private var _spinnerStyle: SpinnerStyle? = null
    var dealy: Int = 0

    fun finished(block: (RefreshLayout, Boolean) -> Unit): _RefreshFooter {
        _onFinish = block
        return this
    }


    fun initialized(block: (kernel: RefreshKernel, height: Int, maxDragHeight: Int) -> Unit): _RefreshFooter {
        _initialized = block
        return this
    }

    fun startAnimator(block: (kernel: RefreshLayout, height: Int, maxDragHeight: Int) -> Unit): _RefreshFooter {
        _startAnimator = block
        return this
    }

    fun moving(block: (isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) -> Unit): _RefreshFooter {
        _moving = block
        return this
    }

    fun stateChanged(block: (RefreshLayout, RefreshState, RefreshState) -> Unit): _RefreshFooter {
        _onStateChanged = block
        return this
    }

    fun noMoreData(block: (Boolean) -> Boolean): _RefreshFooter {
        _onNoMoreData = block
        return this
    }

    /**
     * 尺寸定义初始化完成 （如果高度不改变（代码修改：setHeader），只调用一次, 在RefreshLayout#onMeasure中调用）
     * @param kernel RefreshKernel 核心接口（用于完成高级Header功能）
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        super.onInitialized(kernel, height, maxDragHeight)
        _initialized?.invoke(kernel, height, maxDragHeight)
    }

    /**
     * 开始动画（开始刷新或者开始加载动画）
     * @param layout RefreshLayout
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    override fun onStartAnimator(layout: RefreshLayout, height: Int, maxDragHeight: Int) {
        super.onStartAnimator(layout, height, maxDragHeight)
        _startAnimator?.invoke(layout, height, maxDragHeight)
    }

    /**
     * 动画结束
     * @param layout RefreshLayout
     * @param success 数据是否成功刷新或加载
     * @return 完成动画所需时间 如果返回 Integer.MAX_VALUE 将取消本次完成事件，继续保持原有状态
     */
    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        _onFinish?.invoke(layout, success)
        super.onFinish(layout, success)
        return dealy //延迟500毫秒之后再弹回
    }

    /**
     * 【仅限框架内调用】手指拖动下拉（会连续多次调用，添加isDragging并取代之前的onPulling、onReleasing）
     * @param isDragging true 手指正在拖动 false 回弹动画
     * @param percent 下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+maxDragHeight) / footerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (footerHeight+maxDragHeight)
     * @param height 高度 HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
//    @RestrictTo(LIBRARY, LIBRARY_GROUP, SUBCLASSES)
    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        super.onMoving(isDragging, percent, offset, height, maxDragHeight)
        _moving?.invoke(isDragging, percent, offset, height, maxDragHeight)
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        _onStateChanged?.invoke(refreshLayout, oldState, newState)
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return _spinnerStyle ?: super.getSpinnerStyle()
    }

    fun setSpinnerStyle(spinnerStyle: SpinnerStyle) {
        _spinnerStyle = spinnerStyle
    }

    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        return _onNoMoreData?.let { it(noMoreData) } ?: false
    }

}
