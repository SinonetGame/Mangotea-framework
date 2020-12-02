package com.mangotea.view.component

import android.content.Context
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParentHelper
import android.view.View
import org.jetbrains.anko.constraint.layout._ConstraintLayout

open class NestedScrollingConstraintLayout(ctx: Context) : _ConstraintLayout(ctx), NestedScrollingChild2,
    NestedScrollingParent  {
    private var _nestedScrollingChildHelper: NestedScrollingChildHelper = NestedScrollingChildHelper(this)
    private var _nestedScrollingParentHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)

    /**
     * 设置嵌套滑动是否能用
     */
    override fun setNestedScrollingEnabled(enable: Boolean) {
        _nestedScrollingChildHelper.isNestedScrollingEnabled = enable
    }

    /**
     * 判断嵌套滑动是否可用
     */
    override fun isNestedScrollingEnabled(): Boolean = _nestedScrollingChildHelper.isNestedScrollingEnabled

    /**
     * 开始嵌套滑动
     *
     * @param axes 表示方向轴，有横向和竖向
     */
    override fun startNestedScroll(axes: Int): Boolean {
        return _nestedScrollingChildHelper.startNestedScroll(axes)
    }

    /**
     * 开始嵌套滑动
     *
     * @param axes 表示方向轴，有横向和竖向
     */
    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return _nestedScrollingChildHelper.startNestedScroll(axes, type)
    }

    /**
     * 停止嵌套滑动
     */
    override fun stopNestedScroll(type: Int) {
        _nestedScrollingChildHelper.stopNestedScroll(type)
    }

    /**
     * 停止嵌套滑动
     */
    override fun stopNestedScroll() {
        _nestedScrollingChildHelper.stopNestedScroll()
    }

    /**
     * 判断是否有父View 支持嵌套滑动
     */
    override fun hasNestedScrollingParent(type: Int): Boolean =
        _nestedScrollingChildHelper.hasNestedScrollingParent(type)

    /**
     * 判断是否有父View 支持嵌套滑动
     */
    override fun hasNestedScrollingParent(): Boolean = _nestedScrollingChildHelper.hasNestedScrollingParent()

    /**
     * 滑行时调用
     * @param velocityX x 轴上的滑动速率
     * @param velocityY y 轴上的滑动速率
     * @param consumed 是否被消费
     * @return  true if the nested scrolling parent consumed or otherwise reacted to the fling
     */
    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return _nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    /**
     * 进行滑行前调用
     * @param velocityX x 轴上的滑动速率
     * @param velocityY y 轴上的滑动速率
     * @return true if a nested scrolling parent consumed the fling
     */
    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return _nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    /**
     * 子view处理scroll后调用
     * @param dxConsumed x轴上被消费的距离（横向）
     * @param dyConsumed y轴上被消费的距离（竖向）
     * @param dxUnconsumed x轴上未被消费的距离
     * @param dyUnconsumed y轴上未被消费的距离
     * @param offsetInWindow 子View的窗体偏移量
     * @return  true if the event was dispatched, false if it could not be dispatched.
     */
    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return _nestedScrollingChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        return _nestedScrollingChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow
        )
    }

    /**
     * 在子View的onInterceptTouchEvent或者onTouch中，调用该方法通知父View滑动的距离
     * @param dx  x轴上滑动的距离
     * @param dy  y轴上滑动的距离
     * @param consumed 父view消费掉的scroll长度
     * @param offsetInWindow   子View的窗体偏移量
     * @return 支持的嵌套的父View 是否处理了 滑动事件
     */
    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return _nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return _nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return super.onStartNestedScroll(child, target, nestedScrollAxes)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        super.onNestedPreScroll(target, dx, dy, consumed)
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return super.onNestedPreFling(target, velocityX, velocityY)
    }

    override fun onStopNestedScroll(child: View) {
        _nestedScrollingParentHelper.onStopNestedScroll(child)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        _nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
    }

    override fun getNestedScrollAxes(): Int {
        return _nestedScrollingParentHelper.nestedScrollAxes
    }
}