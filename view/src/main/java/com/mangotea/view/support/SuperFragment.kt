package com.mangotea.view.support

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mangotea.rely.chain.Chain
import com.mangotea.rely.chain.chain
import com.mangotea.rely.chain.end
import com.mangotea.rely.currentTimeMillis
import com.mangotea.rely.ui


open class SuperFragment : Fragment() {

    var uiCreated = false
        protected set

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        uiCreated = true
    }

    private val _clickListener by lazy { View.OnClickListener { v -> onClickView(v) } }

    open fun onClickView(v: View) {}

    fun <T : View> T.toClick(): T {
        this.setOnClickListener(_clickListener)
        return this
    }

    private var waitUiChain: Chain<*>? = null
    fun waitUi(timeOut: Long = 0L, blo: (timeOuted: Boolean) -> Unit) {
        val chain = chain {
            var timeOuted = false
            val ctm = currentTimeMillis
            while (!uiCreated) {
                if (timeOut > 0 && currentTimeMillis - ctm > timeOut) {
                    timeOuted = true
                    break
                }
            }
            timeOuted
        }.end { ui { blo(it) } }
        if (waitUiChain == null) {
            waitUiChain = chain
            chain.single()
        } else
            waitUiChain?.connect(chain)
    }
}
