package com.mangotea.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.internals.AnkoInternals.noGetter

/**-----------------------------------Property---------------------------------------*/
const val parentId = ConstraintSet.PARENT_ID
const val horizontal = LinearLayout.HORIZONTAL
const val vertical = LinearLayout.VERTICAL

var ConstraintLayout.LayoutParams.centerOf: Int
    get() = startToStart
    set(value) {
        horizontalOf = value
        verticalOf = value
    }

var ConstraintLayout.LayoutParams.topOf: Int
    get() = startToStart
    set(value) {
        topToTop = value
        horizontalOf = value
    }

var ConstraintLayout.LayoutParams.bottomOf: Int
    get() = startToStart
    set(value) {
        horizontalOf = value
        bottomToBottom = value
    }

var ConstraintLayout.LayoutParams.startOf: Int
    get() = startToStart
    set(value) {
        startToStart = value
        verticalOf = value
    }

var ConstraintLayout.LayoutParams.endOf: Int
    get() = startToStart
    set(value) {
        endToEnd = value
        verticalOf = value
    }

var ConstraintLayout.LayoutParams.toTopOf: Int
    get() = bottomToTop
    set(value) {
        bottomToTop = value
        horizontalOf = value
    }

var ConstraintLayout.LayoutParams.toBottomOf: Int
    get() = topToBottom
    set(value) {
        horizontalOf = value
        topToBottom = value
    }

var ConstraintLayout.LayoutParams.toStartOf: Int
    get() = endToStart
    set(value) {
        endToStart = value
        verticalOf = value
    }

var ConstraintLayout.LayoutParams.toEndOf: Int
    get() = startToEnd
    set(value) {
        startToEnd = value
        verticalOf = value
    }

var ConstraintLayout.LayoutParams.horizontalOf: Int
    get() = startToStart
    set(value) {
        startToStart = value
        endToEnd = value
    }

var ConstraintLayout.LayoutParams.verticalOf: Int
    get() = startToStart
    set(value) {
        topToTop = value
        bottomToBottom = value
    }

var View.allowClick: Boolean
    get() = isFocusable && isClickable && isEnabled
    set(value) {
        isFocusable = value
        isClickable = value
        isEnabled = value
    }

var View.show: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

val View.parentGroup: ViewGroup?
    get() = parent as? ViewGroup

var Collection<View>.show: Boolean
    get() = find { !it.show }?.let { false } ?: true
    set(value) {
        forEach {
            it.show = value
        }
    }

var Collection<View>.visibility: Int
    get() = if (isEmpty()) View.GONE else first()?.visibility
    set(value) {
        forEach {
            it.visibility = value
        }
    }

/**
 * 增加竖直方向的分割间距
 * 第一个item的顶部也会相应的距离
 * value >=0 则为水平分割线
 * value <0 则为垂直分割线
 */
var RecyclerView.decoration: Int
    get() = 0
    set(value) {
        this.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                if (value > 0) {
                    if (position == 0) outRect.top = value
                    outRect.bottom = value
                } else {
                    if (position == 0) outRect.left = Math.abs(value)
                    outRect.right = Math.abs(value)
                }
            }
        })
    }


/**
 * 该属性会覆盖原生的maxLine
 * 该属性仅在View绘制完成后才会生效
 * 当使用FlexboxLayoutManger作为用于RecyclerView的layoutManager时,该属性失效
 */
var FlexboxLayout.cutline: Int
    get() = getTag(R.id.flexboxCutline) as? Int ?: -1
    set(value) {
        maxLine = -1
        setTag(R.id.flexboxCutline, value)
        if (value < 0) {
            for (cv in 0 until childCount) {
                getChildAt(cv)?.show = true
            }
        } else {
            val clines = flexLines.size
            if (value < clines) {
                flexLines.filterIndexed { index, _ -> index >= value }.forEach {
                    for (ci in 0 until it.itemCount)
                        getChildAt(ci + it.firstIndex)?.show = false
                }
            }
        }
    }


/**-----------------------------------Method---------------------------------------*/

const val DEFAULT_VIEW_MAX_SIZE = 1024f

//hierarchy.roundingParams = RoundingParams().setRoundAsCircle(true)

var SimpleDraweeView.isRound
    get() = hierarchy.roundingParams?.roundAsCircle ?: false
    set(value) {
        hierarchy.roundingParams = RoundingParams().setRoundAsCircle(value)
    }

var SimpleDraweeView.cornerRadius
    get() = hierarchy.roundingParams?.cornersRadii?.average()?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams = RoundingParams.fromCornersRadius(value)
    }

var SimpleDraweeView.cornerRadiusTop
    get() = (cornerRadiusTopLeft + cornerRadiusTopRight) / 2.0f
    set(value) {
        cornerRadiusTopLeft = value
        cornerRadiusTopRight = value
    }

var SimpleDraweeView.cornerRadiusBottom
    get() = (cornerRadiusBottomLeft + cornerRadiusBottomRight) / 2.0f
    set(value) {
        cornerRadiusBottomLeft = value
        cornerRadiusBottomRight = value
    }

var SimpleDraweeView.cornerRadiusLeft
    get() = (cornerRadiusTopLeft + cornerRadiusBottomLeft) / 2.0f
    set(value) {
        cornerRadiusTopLeft = value
        cornerRadiusBottomLeft = value
    }

var SimpleDraweeView.cornerRadiusRight
    get() = (cornerRadiusTopRight + cornerRadiusBottomRight) / 2.0f
    set(value) {
        cornerRadiusTopRight = value
        cornerRadiusBottomRight = value
    }

var SimpleDraweeView.cornerRadiusTopLeft
    get() = hierarchy.roundingParams?.cornersRadii?.let { it[0] }?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams =
            RoundingParams.fromCornersRadii(
                value,
                cornerRadiusTopRight, cornerRadiusBottomRight, cornerRadiusBottomLeft
            )
    }

var SimpleDraweeView.cornerRadiusTopRight
    get() = hierarchy.roundingParams?.cornersRadii?.let { it[1] }?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams =
            RoundingParams.fromCornersRadii(
                cornerRadiusTopLeft, value,
                cornerRadiusBottomRight, cornerRadiusBottomLeft
            )
    }

var SimpleDraweeView.cornerRadiusBottomRight
    get() = hierarchy.roundingParams?.cornersRadii?.let { it[2] }?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams =
            RoundingParams.fromCornersRadii(
                cornerRadiusTopLeft,
                cornerRadiusTopRight, value, cornerRadiusBottomLeft
            )
    }

var SimpleDraweeView.cornerRadiusBottomLeft
    get() = hierarchy.roundingParams?.cornersRadii?.let { it[3] }?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams =
            RoundingParams.fromCornersRadii(
                cornerRadiusTopLeft,
                cornerRadiusTopRight, cornerRadiusBottomRight, value
            )
    }

var GradientDrawable.colorSet
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        colors?.average()?.toInt() ?: 0x0
    } else {
        0x0
    }
    set(value) {
        setColor(value)
    }

var GradientDrawable.cornerRadiusTop
    get() = (cornerRadiusTopLeft + cornerRadiusTopRight) / 2.0f
    set(value) {
        cornerRadius(value, value, cornerRadiusBottomRight, cornerRadiusBottomLeft)
    }

var GradientDrawable.cornerRadiusBottom
    get() = (cornerRadiusBottomLeft + cornerRadiusBottomRight) / 2.0f
    set(value) {
        cornerRadius(cornerRadiusTopLeft, cornerRadiusTopRight, value, value)
    }

var GradientDrawable.cornerRadiusLeft
    get() = (cornerRadiusTopLeft + cornerRadiusBottomLeft) / 2.0f
    set(value) {
        cornerRadius(cornerRadiusTopLeft, value, value, cornerRadiusBottomLeft)
    }

var GradientDrawable.cornerRadiusRight
    get() = (cornerRadiusTopRight + cornerRadiusBottomRight) / 2.0f
    set(value) {
        cornerRadius(value, cornerRadiusTopRight, cornerRadiusBottomRight, value)
    }

var GradientDrawable.cornerRadiusTopLeft
    @SuppressLint("NewApi") @RequiresApi(Build.VERSION_CODES.N)
    get() = kotlin.runCatching { cornerRadii?.let { (it[0] + it[1]) / 2f }?.toFloat() }.getOrNull()
        ?: 0f
    set(value) {
        cornerRadius(value, cornerRadiusTopRight, cornerRadiusBottomRight, cornerRadiusBottomLeft)
    }

var GradientDrawable.cornerRadiusTopRight
    @SuppressLint("NewApi") @RequiresApi(Build.VERSION_CODES.N)
    get() = kotlin.runCatching { cornerRadii?.let { (it[2] + it[3]) / 2f }?.toFloat() }.getOrNull()
        ?: 0f
    set(value) {
        cornerRadius(cornerRadiusTopLeft, value, cornerRadiusBottomRight, cornerRadiusBottomLeft)
    }

var GradientDrawable.cornerRadiusBottomRight
    @SuppressLint("NewApi") @RequiresApi(Build.VERSION_CODES.N)
    get() = kotlin.runCatching { cornerRadii?.let { (it[4] + it[5]) / 2f }?.toFloat() }.getOrNull()
        ?: 0f
    set(value) {
        cornerRadius(cornerRadiusTopLeft, cornerRadiusTopRight, value, cornerRadiusBottomLeft)
    }


var GradientDrawable.cornerRadiusBottomLeft
    @SuppressLint("NewApi") @RequiresApi(Build.VERSION_CODES.N)
    get() = kotlin.runCatching { cornerRadii?.let { (it[6] + it[7]) / 2f }?.toFloat() }.getOrNull()
        ?: 0f
    set(value) {
        cornerRadius(cornerRadiusTopLeft, cornerRadiusTopRight, cornerRadiusBottomRight, value)
    }

fun GradientDrawable.cornerRadius(
    topleft: Float,
    topRight: Float,
    bottomRight: Float,
    bottomLeft: Float
) {
    cornerRadii = floatArrayOf(
        topleft, topleft,
        topRight, topRight,
        bottomRight, bottomRight,
        bottomLeft, bottomLeft
    )
}

var TextView.drawableLeft
    get() = kotlin.runCatching { compoundDrawables?.let { it[0] } }.getOrNull()
    set(left) {
        drawableIntrinsicBounds(left)
    }

var TextView.drawableTop
    get() = kotlin.runCatching { compoundDrawables?.let { it[1] } }.getOrNull()
    set(top) {
        drawableIntrinsicBounds(top = top)
    }

var TextView.drawableRight
    get() = kotlin.runCatching { compoundDrawables?.let { it[2] } }.getOrNull()
    set(right) {
        drawableIntrinsicBounds(right = right)
    }

var TextView.drawableBottom
    get() = kotlin.runCatching { compoundDrawables?.let { it[3] } }.getOrNull()
    set(bottom) {
        drawableIntrinsicBounds(bottom = bottom)
    }

var TextView.drawableLeftResource: Int
    get() = noGetter()
    set(value) {
        drawableIntrinsicBounds(value)
    }

var TextView.drawableTopResource: Int
    get() = noGetter()
    set(value) {
        drawableIntrinsicBounds(top = value)
    }

var TextView.drawableRightResource: Int
    get() = noGetter()
    set(value) {
        drawableIntrinsicBounds(right = value)
    }

var TextView.drawableBottomResource: Int
    get() = noGetter()
    set(value) {
        drawableIntrinsicBounds(bottom = value)
    }

var TextView.drawablePadding
    get() = compoundDrawablePadding
    set(value) {
        compoundDrawablePadding = value
    }

var EditText.textCursorColorResource: Int
    get() = noGetter()
    set(value) {
        textCursorColor = value
    }
var EditText.textCursorColor: Int
    get() = noGetter()
    set(color) {
        try {
            val fCursorDrawableRes =
                TextView::class.java.getDeclaredField("mCursorDrawableRes")//获取这个字段
            fCursorDrawableRes.isAccessible = true//代表这个字段、方法等等可以被访问
            val mCursorDrawableRes = fCursorDrawableRes.getInt(this)

            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(this)

            val clazz = editor.javaClass
            val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true

            val drawables = arrayOf(
                this.context.drawable(mCursorDrawableRes),
                this.context.drawable(mCursorDrawableRes)
            )
            drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)//SRC_IN 上下层都显示。下层居上显示。
            drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            fCursorDrawable.set(editor, drawables)
        } catch (ignored: Throwable) {
        }
    }

val AppBarLayout.scroll
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
val AppBarLayout.enterAlways
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
val AppBarLayout.enterAlwaysCollapsed
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
val AppBarLayout.snap
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
val AppBarLayout.exitUntilCollapsed
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED


val CollapsingToolbarLayout.pin
    get() = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
val CollapsingToolbarLayout.off
    get() = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_OFF
val CollapsingToolbarLayout.parallax
    get() = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX

val Context.statusBarHeight
    get() = resources.getIdentifier("status_bar_height", "dimen", "android").let {
        if (it > 0) resources.getDimensionPixelSize(it) else kotlin.math.ceil((20 * this.resources.displayMetrics.density).toDouble())
            .toInt()
    }

val Fragment.statusBarHeight
    get() = context?.statusBarHeight ?: 0

val View.statusBarHeight
    get() = context.statusBarHeight

val AnkoContext<*>.statusBarHeight
    get() = ctx.statusBarHeight