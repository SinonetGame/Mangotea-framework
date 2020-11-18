package com.mangotea.view.anko

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mangotea.view.R
import com.mangotea.view.component.NestedScrollingConstraintLayout
import com.mangotea.view.component.drawer.TelescopicLayout
import com.mangotea.view.component.recycler.adapter.FragmentAdapter
import com.mangotea.view.component.recycler.adapter.RecyclerViewAdapter
import com.mangotea.view.component.refresh._RefreshFooter
import com.mangotea.view.component.refresh._RefreshHeader
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import net.lucode.hackware.magicindicator.MagicIndicator
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.internals.AnkoInternals

inline fun ViewManager.flexboxLayout(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    init: _FlexboxLayout.() -> Unit
): _FlexboxLayout {
    return ankoView({ _FlexboxLayout(ctx) }, theme, init)
}


inline fun ViewManager.frescoImage(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    builder: GenericDraweeHierarchyBuilder.() -> Unit = {},
    init: SimpleDraweeView.() -> Unit
): SimpleDraweeView {
    return ankoView({
        SimpleDraweeView(ctx).apply {
            hierarchy = GenericDraweeHierarchyBuilder(ctx.resources).apply {
                fadeDuration = 300
                placeholderImageScaleType = ScalingUtils.ScaleType.CENTER_CROP
                this.builder()
            }.build()
        }
    }, theme, init)
}


inline fun ViewManager.nestedScrollingConstraintLayout(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    init: NestedScrollingConstraintLayout.() -> Unit
): NestedScrollingConstraintLayout {
    return ankoView({ NestedScrollingConstraintLayout(ctx) }, theme, init)
}


fun <T> createAdapter(createBlock: AnkoContext<ViewGroup>.(Int) -> View): RecyclerViewAdapter<T> {
    return RecyclerViewAdapter(object : AnkoTypeAdapter {
        override fun createView(ui: AnkoContext<ViewGroup>, type: Int): View = with(ui) {
            this.createBlock(type)
        }
    })
}

fun <T> createAdapter(ankoUi: AnkoComponent<ViewGroup>): RecyclerViewAdapter<T> {
    return RecyclerViewAdapter(object : AnkoTypeAdapter {
        override fun createView(ui: AnkoContext<ViewGroup>, type: Int): View = ankoUi.createView(ui)
    })
}

fun <T> RecyclerView.adapter(createBlock: AnkoContext<ViewGroup>.(Int) -> View): RecyclerViewAdapter<T> =
    createAdapter<T>(createBlock).apply { adapter = this }

fun <T> RecyclerView.adapter(ankoUi: AnkoComponent<ViewGroup>): RecyclerViewAdapter<T> =
    createAdapter<T>(ankoUi).apply { adapter = this }

fun <P> ViewPager2.fragmentsAdapter(fm: FragmentActivity, datas: MutableList<P>): FragmentAdapter<P> = FragmentAdapter(fm, datas).apply { adapter = this }

fun <P> ViewPager2.fragmentsAdapter(fm: Fragment, datas: MutableList<P>): FragmentAdapter<P> = FragmentAdapter(fm, datas).apply { adapter = this }

fun ViewPager2.setupTab(tabLayout: TabLayout, titles: ArrayList<String>) {
    TabLayoutMediator(tabLayout, this) { tab, position ->
        tab.text = titles[position]
    }.attach()
}

fun ViewPager2.setupTab(magicIndicator: MagicIndicator) {
    val callback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            magicIndicator.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            magicIndicator.onPageScrollStateChanged(state)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }
    this.registerOnPageChangeCallback(callback)
    context.apply {
        when (this) {
            is FragmentActivity -> {
                lifecycle.addObserver(GenericLifecycleObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_DESTROY -> {
                            unregisterOnPageChangeCallback(callback)
                        }
                    }
                })
            }
            is Fragment -> {
                lifecycle.addObserver(GenericLifecycleObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_DESTROY -> {
                            unregisterOnPageChangeCallback(callback)
                        }
                    }
                })
            }
        }
    }

}

inline fun ViewManager.viewPager2(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    init: ViewPager2.() -> Unit
): ViewPager2 {
    return ankoView({ ViewPager2(ctx) }, theme, init)
}

inline fun ViewManager.telescopicLayout(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    init: TelescopicLayout.() -> Unit
): TelescopicLayout {
    return ankoView({ TelescopicLayout(ctx) }, theme, init)
}

inline fun ViewManager.refreshLayout(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    init: SmartRefreshLayout.() -> Unit
): SmartRefreshLayout {
    return ankoView({ SmartRefreshLayout(ctx) }, theme, init)
}

inline fun Context.refreshLayout(init: (@AnkoViewDslMarker SmartRefreshLayout).() -> Unit): SmartRefreshLayout {
    return ankoView({ ctx -> SmartRefreshLayout(this) }, theme = 0) { init() }
}

//inline fun ViewManager.horizontalRefreshLayout(
//        ctx: Context = AnkoInternals.getContext(this),
//        theme: Int = 0,
//        init: SmartRefreshHorizontal.() -> Unit
//): SmartRefreshHorizontal {
//    return ankoView({ SmartRefreshHorizontal(ctx) }, theme, init)
//}
//
//inline fun Context.horizontalRefreshLayout(init: (@AnkoViewDslMarker SmartRefreshHorizontal).() -> Unit): SmartRefreshHorizontal {
//    return ankoView({ ctx -> SmartRefreshHorizontal(this) }, theme = 0) { init() }
//}

inline fun ViewManager.refreshHeader(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    init: _RefreshHeader.() -> Unit
): _RefreshHeader {
    return ankoView({ _RefreshHeader(ctx) }, theme, init)
}

inline fun Context.refreshHeader(init: (@AnkoViewDslMarker _RefreshHeader).() -> Unit): _RefreshHeader {
    return ankoView({ ctx -> _RefreshHeader(this) }, theme = 0) { init() }
}


inline fun ViewManager.refreshFooter(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    init: _RefreshFooter.() -> Unit
): _RefreshFooter {
    return ankoView({ _RefreshFooter(ctx) }, theme, init)
}

inline fun Context.refreshFooter(init: (@AnkoViewDslMarker _RefreshFooter).() -> Unit): _RefreshFooter {
    return ankoView({ ctx -> _RefreshFooter(this) }, theme = 0) { init() }
}


inline fun ViewManager.swipeMenuLayout(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    init: SwipeMenuLayout.() -> Unit
): SwipeMenuLayout {
    return ankoView({ SwipeMenuLayout(ctx) }, theme, init)
}

inline fun SwipeMenuLayout.content(blo: SwipeMenuLayout.() -> View) {
    val view = blo()
    view.id = R.id.content
}