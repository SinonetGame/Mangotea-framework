package com.mangotea.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.facebook.common.statfs.StatFsHelper
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.mangotea.rely.id2Uri
import com.mangotea.rely.isExternal
import com.mangotea.view.component.recycler.adapter.DataAdapter
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.support.v4.findOptional

/**-----------------------------------Animate---------------------------------------*/
fun View.slideTop(startDelay: Int = 0, duration: Int = 200, translationY: Float = 100f) {
    this.translationY = translationY
    alpha = 0f
    animate()
            .alpha(1f)
            .translationY(0f)
            .scaleY(1f)
            .setStartDelay(startDelay.toLong()).duration = duration.toLong()
}

fun View.slideOut(startDelay: Int = 0, duration: Int = 200, translationY: Float = 100f) {
    this.translationY = translationY
    alpha = 0f
    animate()
            .alpha(1f)
            .translationY(0f)
            .scaleY(1f)
            .setStartDelay(startDelay.toLong()).duration = duration.toLong()
}

fun View.alphaVary(startDelay: Int = 0, duration: Int = 200) {
    alpha = 0f
    animate()
            .alpha(1f)
            .setStartDelay(startDelay.toLong()).duration = duration.toLong()
}

fun View.parentGroup(blo: ViewGroup.() -> Unit) = parentGroup?.blo()

fun <T : ViewGroup> View.parentGroupGeneric(blo: T.() -> Unit) = (parentGroup as? T)?.blo()

/**
 * Android 6.0 以上设置状态栏颜色
 */
fun Activity.setStatusBar(color: Int, auto: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // 设置状态栏底色颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = color
        if (auto)
            autoTextColorWithBackground(color)
    }
}

const val LIGHT_TEXT = View.SYSTEM_UI_FLAG_VISIBLE
const val DARK_TEXT = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

var Activity.statusBarColor: Int
    set(value) = setStatusBar(value)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = window.statusBarColor

var Activity.statusBarTextColor: Int
    set(value) {
        window.decorView.systemUiVisibility = if (isLightColor(value)) LIGHT_TEXT else DARK_TEXT
    }
    get() = window.decorView.systemUiVisibility


var Activity.autoTextColorWithBackground: Boolean
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    set(value) {
        if (value)
            autoTextColorWithBackground(window.statusBarColor)
    }
    get() = window.decorView.systemUiVisibility == if (isLightColor(statusBarColor)) DARK_TEXT else LIGHT_TEXT


fun Activity.autoTextColorWithBackground(backgroundColor: Int) {
    // 如果亮色，设置状态栏文字为黑色
    window.decorView.systemUiVisibility = if (isLightColor(backgroundColor)) DARK_TEXT else LIGHT_TEXT
}

/**
 * 判断颜色是不是亮色
 *
 * @param color
 * @return
 * @from https://stackoverflow.com/questions/24260853/check-if-color-is-dark-or-light-in-android
 */
fun isLightColor(color: Int): Boolean {
    return ColorUtils.calculateLuminance(color) >= 0.5
}

val frescoStorageType: StatFsHelper.StorageType
    get() = if (isExternal) {
        StatFsHelper.StorageType.EXTERNAL
    } else {
        StatFsHelper.StorageType.INTERNAL
    }

/**-----------------------------------Find---------------------------------------*/

inline fun <reified T : View> View.find(@IdRes id: Int, block: T.() -> Unit): T? =
        findViewById<T>(id)?.apply { block() }

inline fun <reified T : View> Activity.find(@IdRes id: Int, block: T.() -> Unit): T? =
        findViewById<T>(id)?.apply { block() }

inline fun <reified T : View> View.finds(@IdRes vararg id: Int): List<T> {
    val views = arrayListOf<T>()
    id.forEach {
        findViewById<T>(it)?.apply { views.add(this) }
    }
    return views
}

inline fun <reified T : View> Activity.finds(@IdRes vararg id: Int): List<T> {
    val views = arrayListOf<T>()
    id.forEach {
        findViewById<T>(it)?.apply { views.add(this) }
    }
    return views
}

inline fun <reified T : View> Fragment.finds(@IdRes vararg id: Int): List<T> {
    val views = arrayListOf<T>()
    id.forEach {
        findOptional<T>(it)?.apply { views.add(this) }
    }
    return views
}

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun <reified T : View> Fragment.find(@IdRes id: Int, block: T.() -> Unit): T? =
        (view?.findViewById(id) as? T)?.apply { block() }

inline fun <reified T : View> Dialog.find(@IdRes id: Int, block: T.() -> Unit): T =
        findViewById<T>(id).apply { block() }

/**-----------------------------------Listener---------------------------------------*/
inline fun View.onClick(crossinline listener: (View) -> Unit) {
    setOnClickListener {
        listener(it)
    }
}

/**-----------------------------------TextView---------------------------------------*/
fun TextView.drawableIntrinsicBounds(
        left: Drawable? = null, top: Drawable? = null, right: Drawable? = null, bottom: Drawable? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

fun TextView.drawableIntrinsicBounds(
        left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

fun TextView.drawableBounds(
        drawable: Drawable? = null,
        mode: Int = DrawableMode.ALL,
        block: (Drawable.() -> Unit)? = null
) {
    if (block != null) {
        drawable?.block()
    }
    when (mode) {
        DrawableMode.LEFT ->
            setCompoundDrawables(drawable, null, null, null)
        DrawableMode.TOP ->
            setCompoundDrawables(null, drawable, null, null)
        DrawableMode.RIGHT ->
            setCompoundDrawables(null, null, drawable, null)
        DrawableMode.BOTTOM ->
            setCompoundDrawables(null, null, null, drawable)
        else ->
            setCompoundDrawables(drawable, drawable, drawable, drawable)
    }
}

var Drawable.colorOver: Int
    get() {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (colorFilter is PorterDuffColorFilter) {
//                val existing = colorFilter as PorterDuffColorFilter
//                existing.getColor()
//            } else 0
//        } else 0
        return 0
    }
    set(color) {
        setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

fun TextView.drawableBounds(
        drawable: Int = 0,
        mode: Int = DrawableMode.ALL,
        block: (Drawable.() -> Unit)? = null
) =
        drawableBounds(drawable(drawable), mode, block)

fun TextView.drawableLeft(drawable: Int = 0, width: Int, height: Int) =
        drawableBounds(drawable(drawable), DrawableMode.LEFT) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

fun TextView.drawableTop(drawable: Int = 0, width: Int, height: Int) =
        drawableBounds(drawable(drawable), DrawableMode.TOP) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

fun TextView.drawableRight(drawable: Int = 0, width: Int, height: Int) =
        drawableBounds(drawable(drawable), DrawableMode.RIGHT) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

fun TextView.drawableBottom(drawable: Int = 0, width: Int, height: Int) =
        drawableBounds(drawable(drawable), DrawableMode.BOTTOM) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

fun TextView.drawableAll(drawable: Int = 0, width: Int, height: Int) =
        drawableBounds(drawable(drawable), DrawableMode.ALL) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

fun TextView.drawableLeft(drawable: Drawable? = null, width: Int, height: Int) =
        drawableBounds(drawable, DrawableMode.LEFT) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

fun TextView.drawableTop(drawable: Drawable? = null, width: Int, height: Int) =
        drawableBounds(drawable, DrawableMode.TOP) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

fun TextView.drawableRight(drawable: Drawable? = null, width: Int, height: Int) =
        drawableBounds(drawable, DrawableMode.RIGHT) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

fun TextView.drawableBottom(drawable: Drawable? = null, width: Int, height: Int) =
        drawableBounds(drawable, DrawableMode.BOTTOM) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

fun TextView.drawableAll(drawable: Drawable? = null, width: Int, height: Int) =
        drawableBounds(drawable, DrawableMode.ALL) {
            if (width != 0 && height != 0)
                setBounds(0, 0, width, height)
        }

object DrawableMode {
    const val LEFT = 0
    const val RIGHT = 1
    const val TOP = 2
    const val BOTTOM = 3
    const val ALL = -1
}

/**-----------------------------------Resources---------------------------------------*/
fun Context.drawable(id: Int) = if (id != 0) ContextCompat.getDrawable(this, id) else null

fun View.drawable(id: Int) = context.drawable(id)
fun AnkoContext<*>.drawable(id: Int) = ctx.drawable(id)
inline fun Fragment.drawable(value: Int) = context?.drawable(value)

inline fun Context.s(id: Int, vararg formatArg: Any) = if (id != 0) getString(id, *formatArg) else ""
inline fun View.s(id: Int, vararg formatArg: Any) = context.s(id, *formatArg)
inline fun AnkoContext<*>.s(id: Int, vararg formatArg: Any) = ctx.s(id, *formatArg)
inline fun Fragment.s(value: Int, vararg formatArg: Any) = context?.s(value, *formatArg) ?: ""

inline fun Context.color(id: Int) = if (id != 0) ContextCompat.getColor(this, id) else 0
inline fun View.color(id: Int) = context.color(id)
inline fun AnkoContext<*>.color(id: Int) = ctx.color(id)
inline fun Fragment.color(value: Int) = context?.color(value) ?: 0

fun Context.color(color: String) = if (!color.isNullOrEmpty()) Color.parseColor(color) else 0
fun View.color(color: String) = context.color(color)
fun AnkoContext<*>.color(color: String) = ctx.color(color)
inline fun Fragment.color(color: String) = requireActivity().color(color)

fun <T : View> Activity.view(id: Int) = IdView<T>(id, this)
fun <T : View> View.view(id: Int) = IdView<T>(id, this)
fun <T : View> Fragment.view(id: Int) = IdView<T>(id, this)
fun <T : View> Dialog.view(id: Int) = IdView<T>(id, this)

fun <T : View> Activity.viewOptional(id: Int) = IdViewOptional<T>(id, this)
fun <T : View> View.viewOptional(id: Int) = IdView<T>(id, this)
fun <T : View> Fragment.viewOptional(id: Int) = IdView<T>(id, this)
fun <T : View> Dialog.viewOptional(id: Int) = IdView<T>(id, this)

/**-----------------------------------Others---------------------------------------*/
@JvmOverloads
fun SimpleDraweeView.preview(
        uri: String,
        w: Int = this.width,
        h: Int = this.height,
        maxSiz: Float = DEFAULT_VIEW_MAX_SIZE,
        auto: Boolean = false
) {
    preview(Uri.parse(uri), w, h, maxSiz, auto)
}

@JvmOverloads
fun SimpleDraweeView.preview(
        uri: Uri,
        w: Int = this.width,
        h: Int = this.height,
        maxSiz: Float = DEFAULT_VIEW_MAX_SIZE,
        auto: Boolean = false
) {
    val builder = ImageRequestBuilder.newBuilderWithSource(uri)
    if (w > 0 && h > 0) {
        builder.resizeOptions = ResizeOptions(w, h, maxSiz)
    }
    val request = builder.build()
    val controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setTapToRetryEnabled(true)
            .setAutoPlayAnimations(auto)
            .setOldController(this.controller)
            .build() as PipelineDraweeController
    this.controller = controller
}

/**
 * 设置图片高斯模糊
 * @param url
 * @param iterations 迭代次数，越大越模化。
 * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
 */
fun SimpleDraweeView.blurImage(uri: Uri, iterations: Int, blurRadius: Int) {
    val request = ImageRequestBuilder.newBuilderWithSource(uri)
            .setPostprocessor(IterativeBoxBlurPostProcessor(iterations, blurRadius))
            .build()
    val newController = Fresco.newDraweeControllerBuilder()
            .setOldController(controller)
            .setImageRequest(request)
            .build()
    controller = newController
}

fun SimpleDraweeView.blurImage(url: String, iterations: Int, blurRadius: Int) {
    blurImage(Uri.parse(url), iterations, blurRadius)
}

fun SimpleDraweeView.blurImage(resId: Int, iterations: Int, blurRadius: Int) {
    blurImage(context.id2Uri(resId), iterations, blurRadius)
}

fun SimpleDraweeView.setImageURI(resId: Int) {
    setImageURI(context.id2Uri(resId))
}

fun View.inLocation(cx: Float, cy: Float): Boolean {
    val location = IntArray(2)
// 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
    getLocationOnScreen(location)
    return RectF(
            location[0].toFloat(), location[1].toFloat(), location[0].toFloat() + width,
            location[1].toFloat() + height
    ).contains(cx, cy)
}

val RecyclerView.itemViews: Collection<View>
    get() {
        val dataAdapter = adapter as? DataAdapter<*>
        val views = arrayListOf<View>()
        dataAdapter?.datas?.forEachIndexed { index, any ->
            layoutManager?.findViewByPosition(index)?.let {
                views.add(it)
            }
        }
        return views
    }

/**
 *
 * 根据坐标获取相对应的子控件<br></br>
 * 在Activity使用
 *
 * @param x坐标
 * @param y坐标
 * @return 目标View
 */
fun Activity.viewAt(x: Float, y: Float): View? {
    // 从Activity里获取容器
    val root: View = window.decorView
    return findViewByXY(root, x, y)
}

/**
 * 根据坐标获取相对应的子控件<br></br>
 * 在重写ViewGroup使用
 *
 * @param x坐标
 * @param y坐标
 * @return 目标View
 */
fun ViewGroup.viewAt(x: Float, y: Float): View? {
    return findViewByXY(this, x, y)
}

fun ViewGroup.forEach(action: (View) -> Unit) {
    if (childCount <= 0) return
    for (i in 0 until childCount) {
        kotlin.runCatching { getChildAt(i) }.getOrNull()?.let(action)
    }
}

fun ViewGroup.views(): ArrayList<View> {
    val views = arrayListOf<View>()
    forEach {
        views.add(it)
    }
    return views
}

fun TextView.center() {
    gravity = Gravity.CENTER
}

fun TextView.top() {
    gravity = Gravity.TOP
}

fun TextView.bottom() {
    gravity = Gravity.BOTTOM
}


fun TextView.left() {
    gravity = Gravity.LEFT
}

fun TextView.right() {
    gravity = Gravity.RIGHT
}


private fun findViewByXY(view: View, x: Float, y: Float): View? {
    var targetView: View? = null
    if (view is ViewGroup) {
        // 父容器,遍历子控件
        for (i in 0 until view.childCount) {
            targetView = findViewByXY(view.getChildAt(i), x, y)
            if (targetView != null) {
                break
            }
        }
    } else {
        targetView = getTouchTarget(view, x, y)
    }
    return targetView
}

private fun getTouchTarget(view: View, x: Float, y: Float): View? {
    var targetView: View? = null
    // 判断view是否可以聚焦
    val touchableViews: ArrayList<View> = view.touchables
    for (child in touchableViews) {
        if (isTouchPointInView(child, x, y)) {
            targetView = child
            break
        }
    }
    return targetView
}

private fun isTouchPointInView(view: View, x: Float, y: Float): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val left = location[0]
    val top = location[1]
    val right: Int = left + view.measuredWidth
    val bottom: Int = top + view.measuredHeight
    return view.isClickable && y >= top && y <= bottom && x >= left && x <= right
}