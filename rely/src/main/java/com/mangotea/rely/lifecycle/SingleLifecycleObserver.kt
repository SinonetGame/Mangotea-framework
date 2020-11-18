package com.mangotea.rely.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class SingleLifecycleObserver<O>(private val evt: Lifecycle.Event, private val blo: O.() -> Unit) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (evt == event)
            (source as O).blo()
        if (event == Lifecycle.Event.ON_DESTROY)
            source.lifecycle.removeObserver(this)
    }
}
