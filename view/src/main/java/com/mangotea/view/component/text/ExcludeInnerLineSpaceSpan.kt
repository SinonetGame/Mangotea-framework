package com.mangotea.view.component.text

import android.graphics.Paint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.LineHeightSpan
import android.widget.TextView

open class ExcludeInnerLineSpaceSpan(private var mHeight: Int) : LineHeightSpan {

    override fun chooseHeight(text: CharSequence?, start: Int, end: Int, spanstartv: Int, lineHeight: Int, fm: Paint.FontMetricsInt) { // 原始行高
        val originHeight: Int = fm.descent - fm.ascent
        if (originHeight <= 0) {
            return
        } // 计算比例值
        val ratio = mHeight * 1.0f / originHeight // 根据最新行高，修改descent
        fm.descent = Math.round(fm.descent * ratio) // 根据最新行高，修改ascent
        fm.ascent = fm.descent - mHeight
    }
}

var TextView.excludeInnerLineText: CharSequence?
    set(value) {
        excludeInnerLineText(value)
    }
    get() = text

fun TextView.excludeInnerLineText(value: CharSequence?, lineHeight: Int = textSize.toInt()) {
    val textValue: CharSequence = value ?: ""
    val ssb: SpannableStringBuilder = if (textValue is SpannableStringBuilder) textValue else SpannableStringBuilder(textValue)
    ssb.setSpan(ExcludeInnerLineSpaceSpan(lineHeight), 0, textValue.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    text = ssb
}