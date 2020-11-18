package com.mangotea.view.anko

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mangotea.rely.chain.Chain
import com.mangotea.rely.chain.chain
import com.mangotea.rely.chain.end
import com.mangotea.rely.currentTimeMillis
import com.mangotea.rely.d
import com.mangotea.rely.ui
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoContextImpl
import org.jetbrains.anko.internals.AnkoInternals

abstract class AnkoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityRecord.addActivity(this)
        beforeUiCreated()
        AnkoContextImpl(this, this, true).ui()
        uiCreated()
    }

    abstract fun AnkoContext<Activity>.ui(): View

    open fun beforeUiCreated() {}

    open fun uiCreated() {}

    private val _clickListener by lazy { View.OnClickListener { v -> onClickView(v) } }

    open fun onClickView(v: View) {}

    fun <T : View> T.toClick(): T {
        this.setOnClickListener(_clickListener)
        return this
    }

    val resultMap by lazy { hashMapOf<Int, (Int, Intent?) -> Unit>() }

    private val permissionResults by lazy { hashMapOf<Int, (Int, Array<String>, IntArray) -> Unit>() }

    fun permissionResult(code: Int, block: (Int, Array<String>, IntArray) -> Unit) {
        permissionResults[code] = block
    }

    inline fun <reified T : Activity> startActivityForResult(
        vararg params: Pair<String, Any?>,
        noinline blo: (Int, Intent?) -> Unit
    ) {
        val requestCode: Int = androidx.core.view.ViewCompat.generateViewId()
        this.resultMap[requestCode] = blo
        AnkoInternals.internalStartActivityForResult(this, T::class.java, requestCode, params)
    }

    fun startActivityForResult(
        intent: Intent,
        requestCode: Int = androidx.core.view.ViewCompat.generateViewId(),
        blo: (Int, Intent?) -> Unit
    ) {
        this.resultMap[requestCode] = blo
        startActivityForResult(intent, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        d("$requestCode - - $permissionResults", "IPFS")
        permissionResults[requestCode]?.invoke(requestCode, permissions, grantResults)
        permissionResults.remove(requestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        resultMap[requestCode]?.invoke(resultCode, data)
    }

    override fun onDestroy() {
        resultMap.clear()
        d("${this::class.java.name}", "onDestory")
        super.onDestroy()
        ActivityRecord.removeActivity(this)
    }
}

object ActivityRecord {
    private val activityList by lazy { arrayListOf<Activity>() }

    fun addActivity(activity: Activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity)
        }
    }

    fun removeActivity(activity: Activity) {
        if (activityList.contains(activity)) {
            activityList.remove(activity)
        }
    }

    fun removeAllActivity() {
        activityList.forEach {
            it.finish()
        }
    }
}


abstract class AnkoFragment : Fragment() {

    var uiCreated = false
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        AnkoContext.create(context!!).ui()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        uiCreated()
        uiCreated = true
    }

    abstract fun AnkoContext<Context>.ui(): View

    open fun uiCreated() {}

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