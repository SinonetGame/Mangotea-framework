package com.mangotea.view.anko

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mangotea.view.support.SuperActivity
import com.mangotea.view.support.SuperFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoContextImpl

abstract class AnkoActivity : SuperActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beforeUiCreated()
        AnkoContextImpl(this, this, true).ui()
        uiCreated()
    }

    abstract fun AnkoContext<Activity>.ui(): View

    open fun beforeUiCreated() {}

    open fun uiCreated() {}
}

abstract class AnkoFragment : SuperFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = AnkoContext.create(context!!).ui()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        uiCreated()
        uiCreated = true
    }

    abstract fun AnkoContext<Context>.ui(): View

    open fun uiCreated() {}

}