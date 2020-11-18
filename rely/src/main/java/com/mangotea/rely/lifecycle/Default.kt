package com.mangotea.rely.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

fun <O : LifecycleOwner> O.onDestory(blo: O.() -> Unit) = on(Lifecycle.Event.ON_DESTROY, blo)

fun <O : LifecycleOwner> O.on(evt: Lifecycle.Event, blo: O.() -> Unit) = lifecycle.addObserver(SingleLifecycleObserver(evt, blo))


fun Fragment.activityLifecycle(created: FragmentActivity.() -> Unit) {
    lifecycle.addObserver(object : SimpleFragmentLifecycleObserver(this) {
        override fun FragmentActivity.onActivityCreated() {
            created()
        }
    })
}

fun Fragment.activityLifecycle(created: FragmentActivity.() -> Unit, destory: (FragmentActivity.() -> Unit)) {
    lifecycle.addObserver(object : SimpleFragmentLifecycleObserver(this) {
        override fun FragmentActivity.onActivityCreated() {
            created()
        }

        override fun FragmentActivity.onDestory() {
            destory()
        }
    })
}