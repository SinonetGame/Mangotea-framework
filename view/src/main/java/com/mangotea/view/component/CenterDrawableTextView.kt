package com.mangotea.view.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.TextView

@SuppressLint("AppCompatCustomView")
open class CenterDrawableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {
    private var drawables: Array<Drawable?> = arrayOf<Drawable?>(null, null, null, null)
    private var widths: IntArray = intArrayOf(0, 0, 0, 0)
    private var heights: IntArray = intArrayOf(0, 0, 0, 0)


    override fun setCompoundDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        val drawables = arrayOf<Drawable?>(left, top, right, bottom)
        val widths: IntArray = intArrayOf(
            left?.intrinsicWidth ?: 0,
            top?.intrinsicWidth ?: 0,
            right?.intrinsicWidth ?: 0,
            bottom?.intrinsicWidth ?: 0
        )
        val heights: IntArray = intArrayOf(
            left?.intrinsicHeight ?: 0,
            top?.intrinsicHeight ?: 0,
            right?.intrinsicHeight ?: 0,
            bottom?.intrinsicHeight ?: 0
        )
        setDrawables(drawables, widths, heights)
    }


    private fun setDrawables(drawables: Array<Drawable?>?, widths: IntArray?, heights: IntArray?) {
        if (drawables != null && drawables.size >= 4 && widths != null && widths.size >= 4 && heights != null && heights.size >= 4
        ) {
            this.drawables = drawables
            this.widths = widths
            this.heights = heights
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val drawablePadding = compoundDrawablePadding
        translateText(canvas, drawablePadding)
        super.onDraw(canvas)
        val centerX = (width + paddingLeft - paddingRight) / 2.toFloat()
        val centerY = (height + paddingTop - paddingBottom) / 2.toFloat()
        val halfTextWidth = paint.measureText(
            if (text.toString().isEmpty()) hint.toString() else text.toString()
        ) / 2
        val fontMetrics = paint.fontMetrics
        val halfTextHeight = (fontMetrics.descent - fontMetrics.ascent) / 2
        if (drawables[0] != null) {
            val left = (centerX - drawablePadding - halfTextWidth - widths[0]).toInt()
            val top = (centerY - heights[0] / 2).toInt()
            drawables[0]?.setBounds(
                left,
                top,
                left + widths[0],
                top + heights[0]
            )
            canvas.save()
            drawables[0]?.draw(canvas)
            canvas.restore()
        }
        if (drawables[2] != null) {
            val left = (centerX + halfTextWidth + drawablePadding).toInt()
            val top = (centerY - heights[2] / 2).toInt()
            drawables[2]?.setBounds(
                left,
                top,
                left + widths[2],
                top + heights[2]
            )
            canvas.save()
            drawables[2]?.draw(canvas)
            canvas.restore()
        }
        if (drawables[1] != null) {
            val left = (centerX - widths[1] / 2).toInt()
            val bottom = (centerY - halfTextHeight - drawablePadding).toInt()
            drawables[1]?.setBounds(
                left,
                bottom - heights[1],
                left + widths[1],
                bottom
            )
            canvas.save()
            drawables[1]?.draw(canvas)
            canvas.restore()
        }
        if (drawables[3] != null) {
            val left = (centerX - widths[3] / 2).toInt()
            val top = (centerY + halfTextHeight + drawablePadding).toInt()
            drawables[3]?.setBounds(
                left,
                top,
                left + widths[3],
                top + heights[3]
            )
            canvas.save()
            drawables[3]?.draw(canvas)
            canvas.restore()
        }
    }

    private fun translateText(canvas: Canvas, drawablePadding: Int) {
        var translateWidth = 0
        if (drawables[0] != null && drawables[2] != null) {
            translateWidth = (widths[0] - widths[2]) / 2
        } else if (drawables[0] != null) {
            translateWidth = (widths[0] + drawablePadding) / 2
        } else if (drawables[2] != null) {
            translateWidth = -(widths[2] + drawablePadding) / 2
        }
        var translateHeight = 0
        if (drawables[1] != null && drawables[3] != null) {
            translateHeight = (heights[1] - heights[3]) / 2
        } else if (drawables[1] != null) {
            translateHeight = (heights[1] + drawablePadding) / 2
        } else if (drawables[3] != null) {
            translateHeight = -(heights[3] - drawablePadding) / 2
        }
        canvas.translate(translateWidth.toFloat(), translateHeight.toFloat())
    }

}