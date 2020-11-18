package com.mangotea.view.enko

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat


fun selector(block: StateListDrawable.() -> Unit): StateListDrawable {
    return StateListDrawable().apply { block() }
}

fun colors(block: ColorSelector.() -> Unit): ColorStateList {
    return ColorSelector().apply { block() }.create()
}


fun drawable(
    orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
    colors: IntArray? = null,
    block: GradientDrawable.() -> Unit
): GradientDrawable {
    return GradientDrawable(orientation, colors).apply { block() }
}

fun colorDrawable(color: Int? = null, block: ColorDrawable.() -> Unit): ColorDrawable {
    return (color?.let { ColorDrawable(it) } ?: ColorDrawable()).apply { block() }
}


fun Context.vectorDrawable(res: Int, block: VectorDrawableCompat.() -> Unit): VectorDrawableCompat? {
    return VectorDrawableCompat.create(resources, res, null)?.apply { block() }
}

