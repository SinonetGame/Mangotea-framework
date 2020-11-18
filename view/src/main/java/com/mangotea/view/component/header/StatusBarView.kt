package com.mangotea.view.component.header

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import com.mangotea.view.isLightColor


class StatusBarView : View {
    @RequiresApi(api = 21)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    )

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
            context,
            attrs,
            defStyleAttr
    )

    fun setPermeateStyle(dark: Boolean) = setPermeateStyle(null, dark)

    fun setPermeateStyle(colorResource: Int) = setPermeateStyle(colorResource, null)

    private lateinit var immersionBar: ImmersionBar
    fun setPermeateStyle(colorResource: Int?, dark: Boolean?): ImmersionBar {
        (context as Activity).immersionBar {
            addTag("original")
            immersionBar = this
            statusBarView(this@StatusBarView)
            fitsSystemWindows(true)
            if (colorResource != null)
                statusBarColor(colorResource)
            else
                transparentStatusBar()

            if (dark != null)
                statusBarDarkFont(dark)
            else if (colorResource != null)
                statusBarDarkFont(isLightColor(colorResource))
            addTag("inited")
        }
        return immersionBar
    }

    fun original() {
        immersionBar.getTag("original").init()
    }

    fun inited() {
        immersionBar.getTag("inited").init()
    }

}