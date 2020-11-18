package com.mangotea.rely.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.mangotea.rely.catch
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive

open class SimpleFragmentLifecycleObserver(private var fragment: Fragment?) : LifecycleObserver {
    private var waitingToAdd: Job? = null
    private var isCanceled = false
    private var fragmentActivity: FragmentActivity? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        waitingToAdd = catch {
            while (fragment?.activity == null && isActive && !isCanceled) {
                isCanceled = waitingToAdd?.isCancelled ?: false
            }
            fragmentActivity = fragment?.activity?.apply {
                onActivityCreated()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destory() {
        fragmentActivity?.onDestory()
        waitingToAdd?.cancel()
        waitingToAdd = null
        fragment?.lifecycle?.removeObserver(this)
        fragmentActivity = null
        fragment = null
    }

    open fun FragmentActivity.onActivityCreated() {}

    open fun FragmentActivity.onDestory() {}
}